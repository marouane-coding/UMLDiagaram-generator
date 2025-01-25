package org.mql.java;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

import org.mql.java.models.ClassInfo;
import org.mql.java.models.PackageInfo;
import org.mql.java.ui.ClassDiagramPanel;
import org.mql.java.ui.PackageDiagramPanel;
import org.mql.java.utils.PackageScanner;
import org.mql.java.utils.XMLGenerator;
import org.mql.java.utils.XMLParser;
import org.w3c.dom.Document;

public class UMLDiagramApp {
	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("UML Diagram Generator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 600);

            Object[] options = {"Generate Diagrams For The Current Project", "Generate For An External Project"};
            int projectChoice = JOptionPane.showOptionDialog(frame,
                    "Select an option",
                    "Choose Project",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            if (projectChoice == JOptionPane.CLOSED_OPTION) return;

            String projectPath;
            if (projectChoice == 0) {
                projectPath = System.getProperty("user.dir");
            } else {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    projectPath = fileChooser.getSelectedFile().getAbsolutePath();
                } else {
                    return; 
                }
            }

            try {
                Object[] diagramOptions = {"Generate Class Diagrams", "Generate Package Diagram"};
                int diagramChoice = JOptionPane.showOptionDialog(frame,
                        "Select the type of diagram",
                        "Choose Diagram Type",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, diagramOptions, diagramOptions[0]);

                if (diagramChoice == JOptionPane.CLOSED_OPTION) return;

                PackageScanner scanner = new PackageScanner(projectPath);
                Map<String, List<ClassInfo>> map = scanner.mapClassesToPackages();

                JPanel parentPanel = new JPanel();
                parentPanel.setLayout(new GridLayout(0, 1, 20, 20));

                if (diagramChoice == 0) {
                    map.forEach((name, classes) -> {
                        ClassDiagramPanel diagramPanel = new ClassDiagramPanel(name, classes);

                        diagramPanel.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(Color.BLACK, 2),
                            new EmptyBorder(10, 10, 10, 10) 
                        ));

                        parentPanel.add(diagramPanel);
                    });
                    
                } else {
                	PackageDiagramPanel packagePanel = new PackageDiagramPanel(map);
                	parentPanel.add(packagePanel);
                }

                JScrollPane scrollPane = new JScrollPane(parentPanel);
                frame.add(scrollPane);
                frame.setVisible(true);
                
                // Generating the XML representation and saving it to "resources/generatedXML"
                // Also parsing the same XML file and Storing data in the memory structure.
                
                PackageInfo basePackage = scanner.scan();
                System.out.println(basePackage);

                Document generatedXML = XMLGenerator.generateXML(basePackage);
                XMLGenerator.printXML(generatedXML);
                
                PackageInfo pkg = new XMLParser("resources/generatedXML/java.xml").parse();
                System.out.println(pkg);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
