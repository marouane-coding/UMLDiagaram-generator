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
            frame.setLayout(new BorderLayout());

            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            leftPanel.setPreferredSize(new Dimension(300, frame.getHeight()));
            leftPanel.setBackground(new Color(240, 240, 240));

            JLabel selectedProjectLabel = new JLabel("Selected Project: None");
            selectedProjectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton selectCurrentProjectButton = new JButton("Select Current Project");
            JButton selectExternalProjectButton = new JButton("Select External Project");
            JButton generateClassDiagramButton = new JButton("Generate Class Diagrams");
            JButton generatePackageDiagramButton = new JButton("Generate Package Diagram");
            JButton generateXMLButton = new JButton("Generate XML for Project");

            generateClassDiagramButton.setEnabled(false);
            generatePackageDiagramButton.setEnabled(false);
            generateXMLButton.setEnabled(false);

            selectCurrentProjectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            selectExternalProjectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            generateClassDiagramButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            generatePackageDiagramButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            generateXMLButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            leftPanel.add(selectedProjectLabel);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            leftPanel.add(selectCurrentProjectButton);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            leftPanel.add(selectExternalProjectButton);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            leftPanel.add(generateClassDiagramButton);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            leftPanel.add(generatePackageDiagramButton);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            leftPanel.add(generateXMLButton);

            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BorderLayout());
            rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            JScrollPane scrollPane = new JScrollPane(rightPanel);

            frame.add(leftPanel, BorderLayout.WEST);
            frame.add(scrollPane, BorderLayout.CENTER);

            selectCurrentProjectButton.addActionListener(e -> {
                try {
                    String projectPath = System.getProperty("user.dir"); 
                    selectedProjectLabel.setText("Selected Project: " + projectPath);
                    generateClassDiagramButton.setEnabled(true);
                    generatePackageDiagramButton.setEnabled(true);
                    generateXMLButton.setEnabled(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error selecting current project: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            selectExternalProjectButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String projectPath = fileChooser.getSelectedFile().getAbsolutePath();
                    selectedProjectLabel.setText("Selected Project: " + projectPath);
                    generateClassDiagramButton.setEnabled(true);
                    generatePackageDiagramButton.setEnabled(true);
                    generateXMLButton.setEnabled(true);
                }
            });

            generateClassDiagramButton.addActionListener(e -> {
                try {
                    String projectPath = selectedProjectLabel.getText().replace("Selected Project: ", "").trim();
                    if (projectPath.equals("None")) {
                        JOptionPane.showMessageDialog(frame, "Please select a project first!");
                        return;
                    }

                    PackageScanner scanner = new PackageScanner(projectPath);
                    Map<String, List<ClassInfo>> map = scanner.mapClassesToPackages();

                    rightPanel.removeAll();
                    JPanel diagramPanel = new JPanel();
                    diagramPanel.setLayout(new BoxLayout(diagramPanel, BoxLayout.Y_AXIS));

                    map.forEach((name, classes) -> {
                        ClassDiagramPanel classDiagram = new ClassDiagramPanel(name, classes);
                        classDiagram.setBorder(BorderFactory.createCompoundBorder(
                                new LineBorder(Color.BLACK, 2),
                                new EmptyBorder(10, 10, 10, 10)
                        ));
                        diagramPanel.add(classDiagram);
                    });

                    rightPanel.add(diagramPanel, BorderLayout.CENTER);
                    rightPanel.revalidate();
                    rightPanel.repaint();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error generating class diagram: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            generatePackageDiagramButton.addActionListener(e -> {
                try {
                    String projectPath = selectedProjectLabel.getText().replace("Selected Project: ", "").trim();
                    if (projectPath.equals("None")) {
                        JOptionPane.showMessageDialog(frame, "Please select a project first!");
                        return;
                    }

                    PackageScanner scanner = new PackageScanner(projectPath);
                    Map<String, List<ClassInfo>> map = scanner.mapClassesToPackages();

                    rightPanel.removeAll();
                    PackageDiagramPanel packageDiagram = new PackageDiagramPanel(map);
                    rightPanel.add(packageDiagram, BorderLayout.CENTER);
                    rightPanel.revalidate();
                    rightPanel.repaint();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error generating package diagram: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            generateXMLButton.addActionListener(e -> {
                try {
                    String projectPath = selectedProjectLabel.getText().replace("Selected Project: ", "").trim();
                    if (projectPath.equals("None")) {
                        JOptionPane.showMessageDialog(frame, "Please select a project first!");
                        return;
                    }

                    PackageScanner scanner = new PackageScanner(projectPath);
                    PackageInfo basePackage = scanner.scan();
                    Document generatedXML = XMLGenerator.generateXML(basePackage);

                    XMLGenerator.printXML(generatedXML);

                    PackageInfo parsedPackage = new XMLParser("resources/generatedXML/java.xml").parse();
                    System.out.println(parsedPackage);

                    JOptionPane.showMessageDialog(frame, "XML successfully generated and parsed!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error generating XML: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            frame.setVisible(true);
        });
    }
}
