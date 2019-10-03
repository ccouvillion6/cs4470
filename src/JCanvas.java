import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.util.LinkedList;
import java.util.ArrayList;

public class JCanvas extends JPanel {

    private LinkedList<Shape> displayList;
    private String selectedButton;
    private ArrayList<Point> points = new ArrayList<>();
    private boolean drawing = false;

    public JCanvas() {
        this.displayList = new LinkedList<>();
        this.selectedButton = paintWindow.selectedButton;
        this.addMouseListener(new MouseListener() {



            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getSource() instanceof JCanvas) {
                    ((JCanvas) mouseEvent.getSource()).setSelectedButton(paintWindow.selectedButton);
                }
                points = new ArrayList<>();
                points.add(mouseEvent.getPoint());
                drawing = true;
            }


            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                drawing = false;
                points.add(mouseEvent.getPoint());
                alterDisplayList(points);
                setCanvasBounds();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });
        this.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                points.add(mouseEvent.getPoint());
                if (!displayList.isEmpty()) {
                    displayList.removeLast();
                }
                alterDisplayList(points);
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        } );

    }

    private void setSelectedButton(String newSelection) {
        if (!newSelection.equals("Line Width") && !newSelection.equals("Color")) {
            this.selectedButton = newSelection;
        }

    }

    private void setCanvasBounds() {
        double rightmost = 0;
        double downmost = 0;
        for (Shape s : displayList) {
            for (Point p : s.points) {
                if (p.getX() > rightmost) {
                    rightmost = p.getX();
                }
                if (p.getY() > downmost) {
                    downmost = p.getY();
                }
            }
        }
        paintWindow.curr.setPreferredSize(new Dimension((int)rightmost, (int)downmost));
    }

    private Shape alterDisplayList(ArrayList<Point> points) {
        if (selectedButton.equals("Select")) {
            // select commands
            // don't worry about this one for now
            return null;
        } else if (selectedButton.equals("Line")) {
            Line newLine = new Line(points, paintWindow.lineWidth, paintWindow.chosenColor);
            displayList.add(newLine);
            this.repaint();
            return newLine;
        } else if (selectedButton.equals("Line Width")) {
            return null;
        } else if (selectedButton.equals("Rectangle")) {
            Rectangle newRect = new Rectangle(points, paintWindow.lineWidth, paintWindow.chosenColor);
            displayList.add(newRect);
            this.repaint();
            return newRect;
        } else if (selectedButton.equals("Oval")) {
            Oval newOval = new Oval(points, paintWindow.lineWidth, paintWindow.chosenColor);
            displayList.add(newOval);
            this.repaint();
            return newOval;
        } else if (selectedButton.equals("Pen")) {
            FreeFormMonstrosity newMonstrosity = new FreeFormMonstrosity(points, paintWindow.lineWidth, paintWindow.chosenColor);
            displayList.add(newMonstrosity);
            this.repaint();
            return newMonstrosity;
        } else if (selectedButton.equals("Text")) {
            return null;
        }

        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (Shape s : displayList) {
            g2.setStroke(new BasicStroke(s.lineWidth));
            g2.setColor(s.color);
            if (s instanceof Line) {
                g2.drawLine(s.points.get(0).x, s.points.get(0).y, s.points.get(s.points.size()-1).x,
                        s.points.get(s.points.size()-1).y);
            }
            if (s instanceof Oval) {
                g2.draw(new Ellipse2D.Double(Math.min(s.points.get(0).x, s.points.get(s.points.size()-1).x),
                        Math.min(s.points.get(0).y, s.points.get(s.points.size()-1).y),
                        Math.abs(s.points.get(s.points.size()-1).x-s.points.get(0).x),
                        Math.abs(s.points.get(s.points.size()-1).y-s.points.get(0).y)));
                //rubberbanding will be bounding rectangle
            }
            if (s instanceof Rectangle) {
                g2.drawRect(Math.min(s.points.get(0).x, s.points.get(s.points.size()-1).x),
                        Math.min(s.points.get(0).y, s.points.get(s.points.size()-1).y),
                        Math.abs(s.points.get(s.points.size()-1).x-s.points.get(0).x),
                        Math.abs(s.points.get(s.points.size()-1).y-s.points.get(0).y));
            }
            if (s instanceof FreeFormMonstrosity) {
                for (int i = 0; i < s.points.size() - 2; i++)
                {
                    Point p1 = s.points.get(i);
                    Point p2 = s.points.get(i + 1);

                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }
 //       if (drawing) {
 //           Shape newKid = alterDisplayList(startingPoint, MouseInfo.getPointerInfo().getLocation());
 //           repaint();


 //       }
    }

    public class Shape extends JComponent {
        ArrayList<Point> points;
        int lineWidth;
        Color color;

        public Shape(ArrayList<Point> points, int lineWidth, Color color) {
            this.points = points;
            this.lineWidth = paintWindow.lineWidth;
            this.color = color;
        }


    }

    public class Line extends Shape {

        public Line(ArrayList<Point> points, int lineWidth, Color color) {
            super(points, lineWidth, color);
        }

    }

    public class Oval extends Shape {
        public Oval(ArrayList<Point> points, int lineWidth, Color color) {
            super(points, lineWidth, color);
        }
    }

    public class Rectangle extends Shape {
        public Rectangle(ArrayList<Point> points, int lineWidth, Color color) {
            super(points, lineWidth, color);
        }
    }

    public class FreeFormMonstrosity extends Shape {
        public FreeFormMonstrosity(ArrayList<Point> points, int lineWidth, Color color) {
            super(points, lineWidth, color);
        }
    }

}