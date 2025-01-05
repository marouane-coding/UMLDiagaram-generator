package org.mql.java.utils;

import org.mql.java.models.ClassInfo;
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

public class XMLGenerator {

    public static Document generateXML(PackageInfo rootPackage) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement("package");
        doc.appendChild(rootElement);

        createPackageXML(rootPackage, doc, rootElement);
        
        String filePath = "resources/generatedXML/" + rootPackage.getName().replace('.', '/') + ".xml";
        saveXMLToFile(doc, filePath);
        return doc;
    }

    private static void createPackageXML(PackageInfo packageInfo, Document doc, Element parentElement) {
        Element packageElement = doc.createElement("package");
        parentElement.appendChild(packageElement);

        Element nameElement = doc.createElement("name");
        nameElement.appendChild(doc.createTextNode(packageInfo.getName()));
        packageElement.appendChild(nameElement);

        for (ClassInfo classInfo : packageInfo.getClasses()) {
            createClassXML(classInfo, doc, packageElement);
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
        classNameElement.appendChild(doc.createTextNode(classInfo.getName()));
        classElement.appendChild(classNameElement);

        createRelationshipsXML(classInfo, doc, classElement);

        if (classInfo.getImplemetedInterfaces().size() != 0) {
        	Element implementedInterfaces = doc.createElement("implementedInterfaces");
            classElement.appendChild(implementedInterfaces);
        	for (InterfaceInfo iface : classInfo.getImplemetedInterfaces()) {
                createInterfaceXML(iface, doc, implementedInterfaces);
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

    private static void createRelationshipsXML(ClassInfo classInfo, Document doc, Element parentElement) {
        Element relationsElement = doc.createElement("relationships");
        parentElement.appendChild(relationsElement);

        if (classInfo.getExtendedClass() != null) {
            Element inheritanceElement = doc.createElement("parent");
            inheritanceElement.appendChild(doc.createTextNode(classInfo.getExtendedClass()));
            relationsElement.appendChild(inheritanceElement);
        }

        for (RelationshipInfo composedClass : classInfo.getComposedClasses()) {
            Element compositionElement = doc.createElement("composition");
            compositionElement.setAttribute("from", composedClass.getFrom());
            compositionElement.setAttribute("to", composedClass.getTo());
            relationsElement.appendChild(compositionElement);
        }

        for (RelationshipInfo aggregatedClass : classInfo.getAggregatedClasses()) {
            Element aggregationElement = doc.createElement("aggregation");
            aggregationElement.setAttribute("from", aggregatedClass.getFrom());
            aggregationElement.setAttribute("to", aggregatedClass.getTo());
            relationsElement.appendChild(aggregationElement);
        }
    }
    
    private static void createInterfaceXML(InterfaceInfo iface, Document doc, Element parentElement) {
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
