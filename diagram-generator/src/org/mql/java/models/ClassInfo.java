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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void getExtendedClass(Class<?> cls) {
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
		
		for (FieldInfo field : fields) {
			if (field.isCustomType()) {
				Relation rel = field.getField().getAnnotation(Relation.class);
				String from = this.name;
				String to = field.getType();
				relations.add(new RelationshipInfo(from, to, rel));
			}
		}
		
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
	
	

}
