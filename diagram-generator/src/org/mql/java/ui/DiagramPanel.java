package org.mql.java.ui;

import org.mql.java.models.ClassInfo;
import org.mql.java.models.FieldInfo;
import org.mql.java.models.MethodInfo;
import org.mql.java.models.RelationshipInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagramPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private List<ClassInfo> classes;
    private Map<String, Point> classLocations;

    public DiagramPanel(String name, List<ClassInfo> classes) {
        this.classes = classes;
        this.classLocations = new HashMap<>();
        setPreferredSize(new Dimension(1000, 1000));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setLayout(new BorderLayout());
        JLabel label = new JLabel(name, SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 3;

        int numClasses = classes.size();
        double angleStep = 2 * Math.PI / numClasses;

        for (int i = 0; i < numClasses; i++) {
            double angle = i * angleStep;
            int x = (int) (centerX + radius * Math.cos(angle)) - 100; 
            int y = (int) (centerY + radius * Math.sin(angle)) - 50; 

            Point location = new Point(x, y);
            classLocations.put(classes.get(i).getName(), location);
            drawClass(g2, classes.get(i), location);
        }

        drawRelationships(g2);
    }


    private void drawClass(Graphics2D g2, ClassInfo cls, Point location) {
        int width = 150; 
        int x = location.x, y = location.y;

        // Class name header
        int height = 30; 
        g2.setColor(new Color(70, 130, 180)); 
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.WHITE); 
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(cls.getSimpleName(), x + 10, y + 20);

        // Fields section
        int fieldStartY = y + height;
        int fieldHeight = drawFields(g2, cls.getFields(), x, fieldStartY, width);

        // Methods section
        int methodStartY = fieldStartY + fieldHeight;
        int methodHeight = drawMethods(g2, cls.getMethods(), x, methodStartY, width);

        // Total class height
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height + fieldHeight + methodHeight);
    }


    private int drawFields(Graphics2D g2, List<FieldInfo> fields, int x, int y, int width) {
        int fieldHeight = fields.size() * 15; 
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, width, fieldHeight);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, fieldHeight);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        int currentY = y + 12;
        for (FieldInfo field : fields) {
            g2.drawString(field.getRepresentation(), x + 5, currentY);
            currentY += 15; // Spacing between fields here
        }
        return fieldHeight;
    }

    private int drawMethods(Graphics2D g2, List<MethodInfo> methods, int x, int y, int width) {
        int methodHeight = methods.size() * 15; 
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10)); 
        int currentY = y + 12;
        for (MethodInfo method : methods) {
            g2.drawString(method.getRepresentation(), x + 5, currentY);
            currentY += 15;
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
        int width = 150;
        int height = 80;

        Point start = calculateConnectionPoint(from, to, width, height);
        Point end = calculateConnectionPoint(to, from, width, height);

        // Line between points (From and To)
        g2.drawLine(start.x, start.y, end.x, end.y);

        // Draw markers based on relation type
        if (relationType.equals("Inheritance")) {
            drawArrow(g2, start, end);
        } else if (relationType.equals("Aggregation")) {
            drawDiamond(g2, start, end, false);
        } else if (relationType.equals("Composition")) {
            drawDiamond(g2, start, end, true);
        } else if (relationType.equals("Implementation")) {
            drawDashedLine(g2, start, end);
            drawHollowArrow(g2, start, end);
        }
    }

    private Point calculateConnectionPoint(Point source, Point target, int width, int height) {
        int x = source.x + width / 2;
        int y = source.y + height / 2;

        if (source.y + height < target.y) {
            y = source.y + height;
        } else if (source.y > target.y + height) {
            y = source.y;
        }

        if (source.x + width < target.x) {
            x = source.x + width;
        } else if (source.x > target.x + width) {
            x = source.x;
        }

        return new Point(x, y);
    }

    private void drawDiamond(Graphics2D g2, Point from, Point to, boolean filled) {
        int diamondSize = 10;

        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double length = Math.sqrt(dx * dx + dy * dy);

        double unitDx = dx / length;
        double unitDy = dy / length;

        int offsetX = (int) (unitDx * diamondSize);
        int offsetY = (int) (unitDy * diamondSize);

        int centerX = from.x + offsetX;
        int centerY = from.y + offsetY;

        int topX = centerX;
        int topY = centerY - diamondSize;

        int leftX = centerX - diamondSize;
        int leftY = centerY;

        int bottomX = centerX;
        int bottomY = centerY + diamondSize;

        int rightX = centerX + diamondSize;
        int rightY = centerY;

        Polygon diamond = new Polygon(
            new int[]{topX, leftX, bottomX, rightX},
            new int[]{topY, leftY, bottomY, rightY},
            4
        );

        double angle = Math.atan2(dy, dx);
        AffineTransform transform = new AffineTransform();
        transform.setToRotation(angle, centerX, centerY);

        Shape rotatedDiamond = transform.createTransformedShape(diamond);

        if (filled) {
            g2.setColor(Color.BLACK);
            g2.fill(rotatedDiamond);
        } else {
            g2.setColor(Color.WHITE);
            g2.fill(rotatedDiamond);
        }

        g2.setColor(Color.BLACK);
        g2.draw(rotatedDiamond);
    }

    private void drawDashedLine(Graphics2D g2, Point start, Point end) {
        float[] dashPattern = {10.0f, 10.0f};
        Stroke originalStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
        g2.drawLine(start.x, start.y, end.x, end.y);
        g2.setStroke(originalStroke);
    }

    private void drawArrow(Graphics2D g2, Point start, Point end) {
        int arrowSize = 20;
        double angle = Math.atan2(end.y - start.y, end.x - start.x);

        int x1 = end.x - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
        int y1 = end.y - (int) (arrowSize * Math.sin(angle - Math.PI / 6));

        int x2 = end.x - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
        int y2 = end.y - (int) (arrowSize * Math.sin(angle + Math.PI / 6));

        Polygon arrowHead = new Polygon(
            new int[]{end.x, x1, x2},
            new int[]{end.y, y1, y2},
            3
        );

        g2.fillPolygon(arrowHead);
    }

    private void drawHollowArrow(Graphics2D g2, Point start, Point end) {
        int arrowSize = 10;
        double angle = Math.atan2(end.y - start.y, end.x - start.x);

        int x1 = end.x - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
        int y1 = end.y - (int) (arrowSize * Math.sin(angle - Math.PI / 6));

        int x2 = end.x - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
        int y2 = end.y - (int) (arrowSize * Math.sin(angle + Math.PI / 6));

        g2.setColor(Color.BLACK);
        g2.drawLine(end.x, end.y, x1, y1);
        g2.drawLine(end.x, end.y, x2, y2);
    }
}
