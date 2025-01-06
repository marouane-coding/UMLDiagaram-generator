package org.mql.java.models;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.mql.java.annotations.Relation;

public class ClassInfo {
	private String simpleName;
	private String name;
	private String modifiers;
	private String extendedClass;
	private List<FieldInfo> fields;
	private List<MethodInfo> methods;
	private List<RelationshipInfo> relations;
	private List<RelationshipInfo> composedClasses; 
    private List<RelationshipInfo> aggregatedClasses;
    private List<InterfaceInfo> implemetedInterfaces;
	
    public ClassInfo() {
		// TODO Auto-generated constructor stub
	}
    
	public ClassInfo(String classPath) {
		try {
			Class<?> cls = Class.forName(classPath);
			simpleName = cls.getSimpleName();
			name = cls.getName();
			getModifiers(cls);
			getExtendedClass(cls);
			getFields(cls);	
			getMethods(cls);
			getRelations(fields);
			getComposedClasses(fields);
            getAggregatedClasses(fields);
            getImplementedInterfaces(cls); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void getExtendedClass(Class<?> cls) {
		if (cls.getSuperclass() == null) return;
		extendedClass = cls.getSuperclass().getName();
		
		if ("java.lang.Object".equals(extendedClass)) {
			extendedClass =  null;
		}
	}
	
	private void getModifiers(Class<?> cls) {
		modifiers = Modifier.toString(cls.getModifiers());
	}
	
	private void getFields(Class<?> cls){
		fields = new ArrayList<FieldInfo>();
		
		for (Field field : cls.getDeclaredFields()) {
			fields.add(new FieldInfo(field));
		}
	}
	
	private void getMethods(Class<?> cls){
		methods = new ArrayList<MethodInfo>();
		
		for (Method method : cls.getDeclaredMethods()) {
			methods.add(new MethodInfo(method));
		}
	}
	
	private void getRelations(List<FieldInfo> fields) {
		relations = new ArrayList<RelationshipInfo>();
		
		// I'll handle the inheritance relation here.
	    if (extendedClass != null) {
	        relations.add(new RelationshipInfo(name, extendedClass, new Relation() {
	            @Override
	            public Class<? extends java.lang.annotation.Annotation> annotationType() {
	                return Relation.class;
	            }

	            @Override
	            public String value() {
	                return "Inheritance";
	            }
	        }));
	    }
	}
	
    private void getComposedClasses(List<FieldInfo> fields) {
        composedClasses = new ArrayList<RelationshipInfo>();
        
        for (FieldInfo field : fields) {
            if (field.isCustomType() && Modifier.isFinal(field.getField().getModifiers())) {
                String from = this.name;
                String to = field.getType();
                RelationshipInfo relation = new RelationshipInfo(from, to, new Relation() {
    	            @Override
    	            public Class<? extends java.lang.annotation.Annotation> annotationType() {
    	                return Relation.class;
    	            }

    	            @Override
    	            public String value() {
    	                return "Composition";
    	            }
                });
                composedClasses.add(relation);
                relations.add(relation);
            }
        }
    }

    private void getAggregatedClasses(List<FieldInfo> fields) {
        aggregatedClasses = new ArrayList<>();
        for (FieldInfo field : fields) {
            if (field.isCustomType() && !Modifier.isFinal(field.getField().getModifiers())) {
                String from = this.name;
                String to = field.getType();
                RelationshipInfo relation = new RelationshipInfo(from, to, new Relation() {
    	            @Override
    	            public Class<? extends java.lang.annotation.Annotation> annotationType() {
    	                return Relation.class;
    	            }

    	            @Override
    	            public String value() {
    	                return "Aggregation";
    	            }
                });
                aggregatedClasses.add(relation);
                relations.add(relation);
            }
        }
    }
	
	private void getImplementedInterfaces(Class<?> cls) {
	    implemetedInterfaces = new ArrayList<>();
	    for (Class<?> iface : cls.getInterfaces()) {
	        implemetedInterfaces.add(new InterfaceInfo(iface));
	    }
	}


	public String getSimpleName() {
		return simpleName;
	}
	
	public String getName() {
		return name;
	}

	public String getModifiers() {
		return modifiers;
	}

	public String getExtendedClass() {
		return extendedClass;
	}

	public List<FieldInfo> getFields() {
		return fields;
	}

	public List<MethodInfo> getMethods() {
		return methods;
	}

	public List<RelationshipInfo> getRelations() {
		return relations;
	}

	public List<RelationshipInfo> getComposedClasses() {
		return composedClasses;
	}

	public List<RelationshipInfo> getAggregatedClasses() {
		return aggregatedClasses;
	}

	public List<InterfaceInfo> getImplemetedInterfaces() {
		return implemetedInterfaces;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setModifiers(String modifiers) {
		this.modifiers = modifiers;
	}

	public void setExtendedClass(String extendedClass) {
		this.extendedClass = extendedClass;
	}

	public void setFields(List<FieldInfo> fields) {
		this.fields = fields;
	}

	public void setMethods(List<MethodInfo> methods) {
		this.methods = methods;
	}

	public void setRelations(List<RelationshipInfo> relations) {
		this.relations = relations;
	}

	public void setComposedClasses(List<RelationshipInfo> composedClasses) {
		this.composedClasses = composedClasses;
	}

	public void setAggregatedClasses(List<RelationshipInfo> aggregatedClasses) {
		this.aggregatedClasses = aggregatedClasses;
	}

	public void setImplemetedInterfaces(List<InterfaceInfo> implemetedInterfaces) {
		this.implemetedInterfaces = implemetedInterfaces;
	}
	
	
}
