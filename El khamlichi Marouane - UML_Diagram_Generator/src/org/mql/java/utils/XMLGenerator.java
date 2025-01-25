package org.mql.java.utils;

import org.mql.java.models.AnnotationInfo;
import org.mql.java.models.ClassInfo;
import org.mql.java.models.EnumInfo;
import org.mql.java.models.FieldInfo;
import org.mql.java.models.InterfaceInfo;
import org.mql.java.models.MethodInfo;
import org.mql.java.models.PackageInfo;
import org.mql.java.models.RelationshipInfo;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class XMLGenerator {

    public static Document generateXML(PackageInfo rootPackage) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement("project");
        doc.appendChild(rootElement);

        createPackageXML(rootPackage, doc, rootElement);

        String filePath = "resources/generatedXML/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".xml";
        
        saveXMLToFile(doc, filePath);
        return doc;
    }

    private static void createPackageXML(PackageInfo packageInfo, Document doc, Element parentElement) {
        Element packageElement = doc.createElement("package");
        parentElement.appendChild(packageElement);

        packageElement.setAttribute("name", packageInfo.getName());

        for (ClassInfo classInfo : packageInfo.getClasses()) {
            createClassXML(classInfo, doc, packageElement);
        }
        
        for (InterfaceInfo interfaceInfo : packageInfo.getInterfaces()) {
            createInterfaceXML(interfaceInfo, doc, packageElement);
        }
        
        for (EnumInfo enumInfo : packageInfo.getEnums()) {
            createEnumXML(enumInfo, doc, packageElement);
        }
        
        for (AnnotationInfo annotationInfo : packageInfo.getAnnotations()) {
            createAnnotationXML(annotationInfo, doc, packageElement);
        }

        if (packageInfo.getSubPackages() != null) {
            for (PackageInfo subPackage : packageInfo.getSubPackages()) {
                createPackageXML(subPackage, doc, packageElement);
            }
        }
    }

    private static void createClassXML(ClassInfo classInfo, Document doc, Element parentElement) {
        Element classElement = doc.createElement("class");
        parentElement.appendChild(classElement);

        Element classNameElement = doc.createElement("name");
        classNameElement.appendChild(doc.createTextNode(classInfo.getSimpleName()));
        classElement.appendChild(classNameElement);

        createRelationshipsXML(classInfo, doc, classElement);

        if (classInfo.getImplemetedInterfaces().size() != 0) {
        	Element implementedInterfaces = doc.createElement("implementedInterfaces");
            classElement.appendChild(implementedInterfaces);
        	for (InterfaceInfo iface : classInfo.getImplemetedInterfaces()) {
                createImplementedInterfaceXML(iface, doc, implementedInterfaces);
            }
        }
        
        Element fieldsElement = doc.createElement("fields");
        classElement.appendChild(fieldsElement);

        for (FieldInfo field : classInfo.getFields()) {
            createFieldXML(field, doc, fieldsElement);
        }

        Element methodsElement = doc.createElement("methods");
        classElement.appendChild(methodsElement);

        for (MethodInfo method : classInfo.getMethods()) {
            createMethodXML(method, doc, methodsElement);
        }
    }
    
    public static void createInterfaceXML(InterfaceInfo interfaceInfo, Document doc, Element parentElement) {
        Element interfaceElement = doc.createElement("interface");
        parentElement.appendChild(interfaceElement);

        Element simpleNameElement = doc.createElement("simpleName");
        simpleNameElement.appendChild(doc.createTextNode(interfaceInfo.getSimpleName()));
        interfaceElement.appendChild(simpleNameElement);

        Element nameElement = doc.createElement("name");
        nameElement.appendChild(doc.createTextNode(interfaceInfo.getName()));
        interfaceElement.appendChild(nameElement);

        Element modifiersElement = doc.createElement("modifiers");
        modifiersElement.appendChild(doc.createTextNode(interfaceInfo.getModifiers()));
        interfaceElement.appendChild(modifiersElement);

        if (interfaceInfo.getExtendedClass() != null) {
            Element extendedClassElement = doc.createElement("extendedClass");
            extendedClassElement.appendChild(doc.createTextNode(interfaceInfo.getExtendedClass()));
            interfaceElement.appendChild(extendedClassElement);
        }

        Element fieldsElement = doc.createElement("fields");
        interfaceElement.appendChild(fieldsElement);

        for (FieldInfo field : interfaceInfo.getFields()) {
            createFieldXML(field, doc, fieldsElement);
        }

        Element methodsElement = doc.createElement("methods");
        interfaceElement.appendChild(methodsElement);

        for (MethodInfo method : interfaceInfo.getMethods()) {
            createMethodXML(method, doc, methodsElement);
        }
    }
    
    private static void createAnnotationXML(AnnotationInfo annotationInfo, Document doc, Element parentElement) {
        Element annotationElement = doc.createElement("annotation");
        parentElement.appendChild(annotationElement);

        Element annotationNameElement = doc.createElement("name");
        annotationNameElement.appendChild(doc.createTextNode(annotationInfo.getName()));
        annotationElement.appendChild(annotationNameElement);

        Element retentionElement = doc.createElement("retentionPolicy");
        retentionElement.appendChild(doc.createTextNode(annotationInfo.getRetentionPolicy().toString()));
        annotationElement.appendChild(retentionElement);

        Element inheritedElement = doc.createElement("isInherited");
        inheritedElement.appendChild(doc.createTextNode(String.valueOf(annotationInfo.isInherited())));
        annotationElement.appendChild(inheritedElement);

        Element attributesElement = doc.createElement("attributes");
        annotationElement.appendChild(attributesElement);

        for (Map.Entry<String, String> attribute : annotationInfo.getAttributes().entrySet()) {
            Element attributeElement = doc.createElement("attribute");
            attributesElement.appendChild(attributeElement);

            Element attributeNameElement = doc.createElement("name");
            attributeNameElement.appendChild(doc.createTextNode(attribute.getKey()));
            attributeElement.appendChild(attributeNameElement);

            Element attributeTypeElement = doc.createElement("type");
            attributeTypeElement.appendChild(doc.createTextNode(attribute.getValue()));
            attributeElement.appendChild(attributeTypeElement);
        }
    }

    private static void createEnumXML(EnumInfo enumInfo, Document doc, Element parentElement) {
        Element enumElement = doc.createElement("enum");
        parentElement.appendChild(enumElement);

        Element enumNameElement = doc.createElement("name");
        enumNameElement.appendChild(doc.createTextNode(enumInfo.getName()));
        enumElement.appendChild(enumNameElement);

        Element fieldsElement = doc.createElement("fields");
        enumElement.appendChild(fieldsElement);

        for (String field : enumInfo.getFields()) {
            Element fieldElement = doc.createElement("field");
            fieldElement.appendChild(doc.createTextNode(field));
            fieldsElement.appendChild(fieldElement);
        }
    }


    private static void createRelationshipsXML(ClassInfo classInfo, Document doc, Element parentElement) {
        Element relationsElement = doc.createElement("relationships");
        parentElement.appendChild(relationsElement);

        if (classInfo.getExtendedClass() != null) {
            Element inheritanceElement = doc.createElement("parent");
            inheritanceElement.appendChild(doc.createTextNode(classInfo.getExtendedClass()));
            relationsElement.appendChild(inheritanceElement);
        }

        for (RelationshipInfo usedClasse : classInfo.getUsedClasses()) {
            Element usedElement = doc.createElement("uses");
            usedElement.setAttribute("from", usedClasse.getSimpleFrom());
            usedElement.setAttribute("to", usedClasse.getSimpleTo());
//            usedElement.setAttribute("maxOccurs", usedClasse.getMaxOccurs());
            relationsElement.appendChild(usedElement);
        }
        
        for (RelationshipInfo composedClass : classInfo.getComposedClasses()) {
            Element compositionElement = doc.createElement("composition");
            compositionElement.setAttribute("from", composedClass.getSimpleFrom());
            compositionElement.setAttribute("to", composedClass.getSimpleTo());
            compositionElement.setAttribute("maxOccurs", composedClass.getMaxOccurs());
            relationsElement.appendChild(compositionElement);
        }

        for (RelationshipInfo aggregatedClass : classInfo.getAggregatedClasses()) {
            Element aggregationElement = doc.createElement("aggregation");
            aggregationElement.setAttribute("from", aggregatedClass.getSimpleFrom());
            aggregationElement.setAttribute("to", aggregatedClass.getSimpleTo());
            aggregationElement.setAttribute("maxOccurs", aggregatedClass.getMaxOccurs());
            relationsElement.appendChild(aggregationElement);
        }
    }
    
    private static void createImplementedInterfaceXML(InterfaceInfo iface, Document doc, Element parentElement) {
    	Element interfaceElement = doc.createElement("interface");
        parentElement.appendChild(interfaceElement);

        interfaceElement.setAttribute("name", iface.getSimpleName());
    }

    private static void createFieldXML(FieldInfo field, Document doc, Element parentElement) {
        Element fieldElement = doc.createElement("field");
        parentElement.appendChild(fieldElement);

        Element nameElement = doc.createElement("name");
        nameElement.appendChild(doc.createTextNode(field.getName()));
        fieldElement.appendChild(nameElement);

        Element typeElement = doc.createElement("type");
        typeElement.appendChild(doc.createTextNode(field.getType()));
        fieldElement.appendChild(typeElement);

        Element modifierElement = doc.createElement("modifier");
        modifierElement.appendChild(doc.createTextNode(String.valueOf(field.getModifier())));
        fieldElement.appendChild(modifierElement);
    }

    private static void createMethodXML(MethodInfo method, Document doc, Element parentElement) {
        Element methodElement = doc.createElement("method");
        parentElement.appendChild(methodElement);

        Element nameElement = doc.createElement("name");
        nameElement.appendChild(doc.createTextNode(method.getName()));
        methodElement.appendChild(nameElement);

        Element returnTypeElement = doc.createElement("returnType");
        returnTypeElement.appendChild(doc.createTextNode(method.getReturnType()));
        methodElement.appendChild(returnTypeElement);

        Element modifierElement = doc.createElement("modifier");
        modifierElement.appendChild(doc.createTextNode(String.valueOf(method.getModifier())));
        methodElement.appendChild(modifierElement);
    }

    public static void printXML(Document doc) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(System.out);
        transformer.transform(source, result);
    }
    
    public static void saveXMLToFile(Document doc, String filePath) throws TransformerException, IOException {
        File file = new File(filePath);
        
        file.getParentFile().mkdirs();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(file);
        transformer.transform(new DOMSource(doc), result);

        System.out.println("XML file saved to: " + file.getAbsolutePath());
    }
}
