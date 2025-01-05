package org.mql.java;


import javax.swing.*;
import java.util.List;

import org.mql.java.models.ClassInfo;
import org.mql.java.ui.DiagramPanel;
import org.mql.java.utils.PackageScanner;

public class UMLDiagramApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("UML Diagram Generator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            
            String packageName = "org.mql.java.examples";
            PackageScanner scanner = new PackageScanner(packageName);
            List<ClassInfo> classInfos = scanner.getClasses();
            
            DiagramPanel panel = new DiagramPanel(classInfos);
            frame.add(new JScrollPane(panel));
            
            frame.setVisible(true);
        });
    }
}
