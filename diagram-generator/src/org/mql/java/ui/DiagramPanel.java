package org.mql.java.ui;

import org.mql.java.models.ClassInfo;
import org.mql.java.models.FieldInfo;
import org.mql.java.models.MethodInfo;
import org.mql.java.models.RelationshipInfo;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagramPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private List<ClassInfo> classes;
    private Map<String, Point> classLocations;

    public DiagramPanel(List<ClassInfo> classes) {
        this.classes = classes;
        this.classLocations = new HashMap<>();
        setPreferredSize(new Dimension(1000, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int x = 50, y = 50;
        for (ClassInfo cls : classes) {
            Point location = new Point(x, y);
            classLocations.put(cls.getName(), location);
            drawClass(g2, cls, location);
            y += 300;
            if (y > getHeight() - 200) {
                y = 50;
                x += 250;
            }
        }

        drawRelationships(g2);
    }

    private void drawClass(Graphics2D g2, ClassInfo cls, Point location) {
        int width = 200;
        int x = location.x, y = location.y;

        // Class name
        int height = 30;
        g2.drawRect(x, y, width, height);
        g2.drawString(cls.getSimpleName(), x + 10, y + 20);

        // Fields section
        int fieldStartY = y + height;
        int fieldHeight = drawFields(g2, cls.getFields(), x, fieldStartY, width);

        // Methods section
        int methodStartY = fieldStartY + fieldHeight;
        int methodHeight = drawMethods(g2, cls.getMethods(), x, methodStartY, width);

        // Total class height
        g2.drawRect(x, y, width, height + fieldHeight + methodHeight);
    }

    private int drawFields(Graphics2D g2, List<FieldInfo> fields, int x, int y, int width) {
        int fieldHeight = fields.size() * 20;
        g2.drawRect(x, y, width, fieldHeight);
        int currentY = y + 15;
        for (FieldInfo field : fields) {
            g2.drawString(field.getRepresentation(), x + 5, currentY);
            currentY += 20;
        }
        return fieldHeight;
    }

    private int drawMethods(Graphics2D g2, List<MethodInfo> methods, int x, int y, int width) {
        int methodHeight = methods.size() * 20;
        g2.drawRect(x, y, width, methodHeight);
        int currentY = y + 15;
        for (MethodInfo method : methods) {
            g2.drawString(method.getRepresentation(), x + 5, currentY);
            currentY += 20;
        }
        return methodHeight;
    }

    private void drawRelationships(Graphics2D g2) {
        for (ClassInfo cls : classes) {
            for (RelationshipInfo rel : cls.getRelations()) {
                Point from = classLocations.get(rel.getFrom());
                Point to = classLocations.get(rel.getTo());
                if (from != null && to != null) {
                    drawRelationLine(g2, from, to, rel.getRelation());
                }
            }
        }
    }

    private void drawRelationLine(Graphics2D g2, Point from, Point to, String relationType) {
        g2.drawLine(from.x + 75, from.y + 100, to.x + 75, to.y);
        if (relationType.equals("Inheritance")) {
            drawArrow(g2, from, to);
        } else if (relationType.equals("Aggregation")) {
            drawDiamond(g2, from, to, false);
        } else if (relationType.equals("Composition")) {
            drawDiamond(g2, from, to, true);
        }
    }

    private void drawArrow(Graphics2D g2, Point from, Point to) {
        int arrowSize = 10;
        g2.fillPolygon(new int[]{to.x + 75, to.x + 70, to.x + 80},
                       new int[]{to.y, to.y - arrowSize, to.y - arrowSize}, 3);
    }

    private void drawDiamond(Graphics2D g2, Point from, Point to, boolean filled) {
        int[] xPoints = {to.x + 75, to.x + 65, to.x + 75, to.x + 85};
        int[] yPoints = {to.y, to.y - 10, to.y - 20, to.y - 10};
        Polygon diamond = new Polygon(xPoints, yPoints, 4);
        if (filled) {
            g2.fill(diamond);
        } else {
            g2.draw(diamond);
        }
    }
}
