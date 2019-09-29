import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JCanvas extends JPanel {

    public HashSet<Shape> displayList;
    private String selectedButton;

    public JCanvas() {
        this.displayList = new HashSet<>();
        this.selectedButton = paintWindow.selectedButton;
        this.addMouseListener(new MouseListener() {

            private Point currentlyDown;

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getSource() instanceof JCanvas) {
                    ((JCanvas) mouseEvent.getSource()).setSelectedButton(paintWindow.selectedButton);
                }
                currentlyDown = new Point(mouseEvent.getX(), mouseEvent.getY());
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                Point currentlyUp = new Point(mouseEvent.getX(), mouseEvent.getY());
                alterDisplayList(currentlyDown, currentlyUp);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

    }

    public void setSelectedButton(String newSelection) {
        this.selectedButton = newSelection;
    }

    private Shape alterDisplayList(Point p1, Point p2) {
        if (selectedButton == "Select") {
            // select commands
            // don't worry about this one for now
            return null;
        } else if (selectedButton == "Line") {
            Line newLine = new Line(p1, p2);
            displayList.add(newLine);
            this.repaint();
            return newLine;
        } else if (selectedButton == "Line Width") {
            return null;
        } else if (selectedButton == "Rectangle") {
            return null;
        } else if (selectedButton == "Oval") {
            return null;
        } else if (selectedButton == "Pen") {
            return null;
        } else if (selectedButton == "Text") {
            return null;
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape s : displayList) {
            if (s instanceof Line) {
                g.drawLine(s.e1.x, s.e1.y, s.e2.x, s.e2.y);
            }
        }
    }

    public class Shape extends JComponent {
        Point e1;
        Point e2;

        public Shape(Point e1, Point e2) {
            this.e1 = e1;
            this.e2 = e2;
        }


    }

    public class Line extends Shape {

        public Line(Point e1, Point e2) {
            super(e1, e2);
        }

    }

}