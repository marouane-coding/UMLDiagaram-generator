package org.mql.java;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import org.mql.java.models.ClassInfo;
import org.mql.java.models.PackageInfo;
import org.mql.java.ui.DiagramPanel;
import org.mql.java.utils.PackageScanner;
import org.mql.java.utils.XMLGenerator;
import org.mql.java.utils.XMLParser;
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
            //Currently : 2 columns, unlimited rows, gaps of 20px
            //Could be updated later
            parentPanel.setLayout(new GridLayout(0, 2, 200, 200));

            map.forEach((name, classes) -> {
                DiagramPanel diagramPanel = new DiagramPanel(name, classes);

                // Add a compound border: LineBorder + Margin
                diagramPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Color.BLACK, 2), // Black border
                    new EmptyBorder(10, 10, 10, 10) // Margin inside the border
                ));

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
            
            PackageInfo mypkg = new XMLParser("resources/generatedXML/java.xml").parse();
            System.out.println(mypkg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
