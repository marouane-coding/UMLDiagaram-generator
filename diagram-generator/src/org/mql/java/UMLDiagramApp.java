package org.mql.java;

import javax.swing.*;
import java.util.List;
import java.util.Map;

import org.mql.java.models.ClassInfo;
import org.mql.java.models.PackageInfo;
import org.mql.java.ui.DiagramPanel;
import org.mql.java.utils.PackageScanner;
import org.mql.java.utils.XMLGenerator;
import org.w3c.dom.Document;

public class UMLDiagramApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("UML Diagram Generator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            
            String packageName = "org.mql.java";
            PackageScanner scanner = new PackageScanner(packageName);
            PackageInfo basePackage = scanner.scan();
            System.out.println(basePackage);
            
            Map<String, List<ClassInfo>> map = scanner.mapClassesToPackages();
            
            JPanel parentPanel = new JPanel();
            parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
            
            map.forEach((name, classes) -> {
                DiagramPanel diagramPanel = new DiagramPanel(classes);
                parentPanel.add(diagramPanel); 
            });
            
            JScrollPane scrollPane = new JScrollPane(parentPanel);
            frame.add(scrollPane);
            frame.setVisible(true);
        });     
        
        try {
            PackageInfo rootPackage = new PackageScanner("org.mql.java").scan();

            Document xmlDocument = XMLGenerator.generateXML(rootPackage);

            XMLGenerator.printXML(xmlDocument);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
