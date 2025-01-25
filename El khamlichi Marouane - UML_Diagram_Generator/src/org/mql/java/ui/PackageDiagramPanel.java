package org.mql.java.ui;

import org.mql.java.models.ClassInfo;
import org.mql.java.models.RelationshipInfo;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageDiagramPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private Map<String, List<ClassInfo>> packageMap;
    private Map<String, Point> classLocations; 

    public PackageDiagramPanel(Map<String, List<ClassInfo>> packageMap) {
        this.packageMap = packageMap;
        this.classLocations = new HashMap<>();
        setPreferredSize(new Dimension(1200, 800));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setLayout(null); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = 50; 
        int y = 50; 
        int packageWidth = 250; 
        int packageHeight = 200; 
        int spacing = 30; 

        for (Map.Entry<String, List<ClassInfo>> entry : packageMap.entrySet()) {
            String packageName = entry.getKey();
            List<ClassInfo> classes = entry.getValue();

            drawPackage(g2, packageName, classes, x, y, packageWidth, packageHeight);

            x += packageWidth + spacing;
            if (x + packageWidth > getWidth()) {
                x = 50;
                y += packageHeight + spacing;
            }
        }

        drawLinks(g2);
    }

    private void drawPackage(Graphics2D g2, String packageName, List<ClassInfo> classes, int x, int y, int width, int height) {
        // package container
        g2.setColor(new Color(200, 200, 250));
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height);

        // package name header
        g2.setColor(new Color(100, 149, 237));
        g2.fillRect(x, y, width, 30);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(packageName, x + 10, y + 20);

        // class rectangles inside the package
        int classY = y + 40; 
        int classHeight = 30; 
        int classSpacing = 10; 

        for (ClassInfo cls : classes) {
            if (classY + classHeight > y + height) break; // Prevent overflow
            drawClass(g2, cls, x + 10, classY, width - 20, classHeight);
            classY += classHeight + classSpacing;
        }
    }

    private void drawClass(Graphics2D g2, ClassInfo cls, int x, int y, int width, int height) {
        // class rectangle
        g2.setColor(new Color(240, 240, 240));
        g2.fillRoundRect(x, y, width, height, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(x, y, width, height, 10, 10);

        // class name
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics metrics = g2.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(cls.getSimpleName())) / 2;
        int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2.drawString(cls.getSimpleName(), textX, textY);

        classLocations.put(cls.getName(), new Point(x + width / 2, y + height / 2));
    }

    private void drawLinks(Graphics2D g2) {
        for (Map.Entry<String, List<ClassInfo>> entry : packageMap.entrySet()) {
            String fromPackage = entry.getKey(); 
            List<ClassInfo> classes = entry.getValue();

            for (ClassInfo cls : classes) {
                for (RelationshipInfo rel : cls.getRelations()) {
                    String toPackage = findClassPackage(rel.getTo());

                    if (toPackage != null && !fromPackage.equals(toPackage)) {
                        Point from = classLocations.get(rel.getFrom());
                        Point to = classLocations.get(rel.getTo());

                        if (from != null && to != null) {
                            drawArrow(g2, from, to);
                        }
                    }
                }
            }
        }
    }

    private String findClassPackage(String className) {
        for (Map.Entry<String, List<ClassInfo>> entry : packageMap.entrySet()) {
            for (ClassInfo cls : entry.getValue()) {
                if (cls.getName().equals(className)) {
                    return entry.getKey(); 
                }
            }
        }
        return null; 
    }


    private void drawArrow(Graphics2D g2, Point from, Point to) {
        g2.setColor(Color.BLACK);
        g2.drawLine(from.x, from.y, to.x, to.y);

        int arrowSize = 10;
        double angle = Math.atan2(to.y - from.y, to.x - from.x);

        int x1 = to.x - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
        int y1 = to.y - (int) (arrowSize * Math.sin(angle - Math.PI / 6));

        int x2 = to.x - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
        int y2 = to.y - (int) (arrowSize * Math.sin(angle + Math.PI / 6));

        g2.fillPolygon(new int[]{to.x, x1, x2}, new int[]{to.y, y1, y2}, 3);
    }
}
