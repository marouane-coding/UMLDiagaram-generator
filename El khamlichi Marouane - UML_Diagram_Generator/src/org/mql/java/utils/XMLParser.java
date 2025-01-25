package org.mql.java.utils;

import java.util.ArrayList;
import java.util.List;

import org.mql.java.models.AnnotationInfo;
import org.mql.java.models.ClassInfo;
import org.mql.java.models.EnumInfo;
import org.mql.java.models.FieldInfo;
import org.mql.java.models.InterfaceInfo;
import org.mql.java.models.MethodInfo;
import org.mql.java.models.PackageInfo;
import org.mql.java.models.RelationshipInfo;

public class XMLParser {
	private XMLNode xmlNode;

	public XMLParser(String path) {
		this.xmlNode = new XMLNode(path);
	}
	
	public PackageInfo parse() {
		PackageInfo myPackage = new PackageInfo();		
		XMLNode mainPackage = xmlNode.firstChild();
		myPackage = parseRecursively(mainPackage);
		
		return myPackage;
	}
	
	private PackageInfo parseRecursively(XMLNode node) {
		PackageInfo myPackage = new PackageInfo();
		List<PackageInfo> subPackages = new ArrayList<PackageInfo>();
		List<ClassInfo> packageClasses = new ArrayList<ClassInfo>();
//		List<InterfaceInfo> packageInterfaces = new ArrayList<InterfaceInfo>();
//		List<EnumInfo> packageEnums = new ArrayList<EnumInfo>();
//		List<AnnotationInfo> packageAnnotations = new ArrayList<AnnotationInfo>();
		
		String packageName = node.getAttribute("name");
		myPackage.setName(packageName);

		List<XMLNode> children = node.children() != null ? node.children() : new ArrayList<XMLNode>();
		
		for (XMLNode child : children) {
			if (child.getName().equals("package")) {
				subPackages.add(parseRecursively(child));
			} else if (child.getName().equals("class")) {
				ClassInfo cls = generateClass(child);
				packageClasses.add(cls);
			}
		}
		
		myPackage.setClasses(packageClasses);
		myPackage.setSubPackages(subPackages);
		
		return myPackage;
	}
	
	private ClassInfo generateClass(XMLNode node) {
		ClassInfo cls = new ClassInfo();
		List<FieldInfo> classFields = new ArrayList<FieldInfo>();
		List<MethodInfo> classMethods = new ArrayList<MethodInfo>();
		List<RelationshipInfo> relationsList = new ArrayList<RelationshipInfo>();
		List<RelationshipInfo> composedClasses = new ArrayList<RelationshipInfo>();
		List<RelationshipInfo> aggregatedClaases = new ArrayList<RelationshipInfo>();
		List<InterfaceInfo> implementedInterfaces = new ArrayList<InterfaceInfo>();

		String name = node.child("name").getValue();
		
		cls.setName(name);
		
		XMLNode fieldsNode = node.child("fields");
		List<XMLNode> fields = fieldsNode != null ? fieldsNode.children() : new ArrayList<>();
		for (XMLNode field : fields) {
		    String fieldName = field.child("name").getValue();
		    String fieldType = field.child("type").getValue();
		    char fieldModifier = field.child("modifier").getValue().charAt(0);
		    
		    FieldInfo fieldInfo = new FieldInfo();
		    fieldInfo.setName(fieldName);
		    fieldInfo.setType(fieldType);
		    fieldInfo.setModifier(fieldModifier);
		    classFields.add(fieldInfo);
		}

		XMLNode methodsNode = node.child("methods");
		List<XMLNode> methods = methodsNode != null ? methodsNode.children() : new ArrayList<>();
		for (XMLNode method : methods) {
		    String methodName = method.child("name").getValue();
		    String methodType = method.child("returnType").getValue();
		    char methodModifier = method.child("modifier").getValue().charAt(0);
		    
		    MethodInfo methodInfo = new MethodInfo();
		    methodInfo.setName(methodName);
		    methodInfo.setReturnType(methodType);
		    methodInfo.setModifier(methodModifier);
		    classMethods.add(methodInfo);
		}

		XMLNode relationsNode = node.child("relationships");
		List<XMLNode> relations = relationsNode != null ? relationsNode.children() : new ArrayList<>();
		for (XMLNode relation : relations) {
		    if (!relation.getName().equals("parent")) {
		        String relationName = relation.getName();
		        String from = relation.getAttribute("from");
		        String to = relation.getAttribute("to");
		        
		        RelationshipInfo relationInfo = new RelationshipInfo();
		        relationInfo.setFrom(from);
		        relationInfo.setTo(to);
		        relationInfo.setRelation(relationName);
		        relationsList.add(relationInfo);
		        
		        if (relationName.equals("aggregation")) {
		            aggregatedClaases.add(relationInfo);
		        } else {
		            composedClasses.add(relationInfo);
		        }
		    } else {
		        cls.setExtendedClass(relation.getValue());
		    }
		}
		cls.setRelations(relationsList);
		cls.setAggregatedClasses(aggregatedClaases);
		cls.setComposedClasses(composedClasses);

		
		XMLNode implementedInterfacesNode = node.child("implementedInterfaces");

		List<XMLNode> ifaces = new ArrayList<>();
		if (implementedInterfacesNode != null) {
		    ifaces = implementedInterfacesNode.children();
		}

		for (XMLNode iface : ifaces) {
		    InterfaceInfo ifaceInfo = new InterfaceInfo();
		    
		    String ifaceName = iface.getAttribute("name");
		    ifaceInfo.setName(ifaceName);
		    implementedInterfaces.add(ifaceInfo);
		}
		cls.setImplemetedInterfaces(implementedInterfaces);

		return cls;
	}

}
