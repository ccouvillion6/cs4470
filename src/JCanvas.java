import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
                if (displayList.size() != 0) {
                    if (displayList.getLast() instanceof TextBox) {
                        ((TextBox)(displayList.getLast())).typing = false;
                    }
                }
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
                paintWindow.curr.revalidate();
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
                if (drawing) {
                    points.add(mouseEvent.getPoint());
                }
                if (!displayList.isEmpty()) {
                    displayList.removeLast();
                }
                alterDisplayList(points);
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        } );

        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if (displayList.getLast() instanceof TextBox) {
                    ((TextBox)(displayList.getLast())).text += keyEvent.getKeyChar();
                    repaint();
                }




            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });
        this.requestFocus();

    }

    private void setSelectedButton(String newSelection) {
        if (!newSelection.equals("Line Width") && !newSelection.equals("Color")) {
            this.selectedButton = newSelection;
        }

    }

    public void setCanvasBounds() {
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
        paintWindow.curr.setPreferredSize(new Dimension((int)rightmost + 20, (int)downmost + 20));
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
            TextBox newTextBox = new TextBox(points, paintWindow.lineWidth, paintWindow.chosenColor, "");
            displayList.add(newTextBox);
            this.repaint();
            this.requestFocus();
            return newTextBox;
        }

        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        RenderingHints rh2 = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);
        g2.setRenderingHints(rh2);

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
            if (s instanceof TextBox) {
                TextBox ess = (TextBox)s;
                // USE FONTMETRICS!!
                int xedge = Math.max(s.points.get(s.points.size()-1).x,s.points.get(0).x);
                int yedge = Math.max(s.points.get(s.points.size()-1).y,s.points.get(0).y);
                int numExtraLines = 0;
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                FontMetrics metrics = g2.getFontMetrics(g2.getFont());
                int fontHeight;
                int fontWidth;
                int letterX = Math.min(s.points.get(0).x, s.points.get(s.points.size()-1).x) + 5;
                int letterY = Math.min(s.points.get(0).y, s.points.get(s.points.size()-1).y) + 15;
                for (int i = 0; i < ess.getText().length(); i++) {
                    fontHeight = metrics.getHeight();
                    if (letterX + 10 >= xedge) {
                        letterY += fontHeight + 2;
                        letterX = Math.min(s.points.get(0).x, s.points.get(s.points.size()-1).x) + 5;
                    }
                    if (letterY + 10 >= yedge) {
                        numExtraLines += 1;
                        yedge += fontHeight + 2;
                        s.points.add(new Point(Math.max(s.points.get(0).x, s.points.get(s.points.size()-1).x), yedge));
                    }
                    if (((TextBox)s).typing) {
                        g2.drawRect(Math.min(s.points.get(0).x, s.points.get(s.points.size()-1).x),
                                Math.min(s.points.get(0).y, s.points.get(s.points.size()-1).y), Math.abs(s.points.get(s.points.size()-1).x-s.points.get(0).x),
                                Math.abs(s.points.get(s.points.size()-1).y-s.points.get(0).y) + numExtraLines*fontHeight);
                    }

                    g2.drawString(ess.getText().charAt(i) + "", letterX, letterY);
                    //System.out.println(ess.getText().charAt(i) + " at " + letterX + " , " + letterY);
                    fontWidth = metrics.stringWidth(ess.getText().charAt(i) + "");
                    letterX += fontWidth;
                    //setCanvasBounds();




                }
            }
        }

    }

    public class Shape extends JComponent {
        ArrayList<Point> points;
        int lineWidth;
        Color color;

        public Shape(ArrayList<Point> points, int lineWidth, Color color) {
            this.points = points;
            this.lineWidth = lineWidth;
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

    public class TextBox extends Rectangle {
        String text;
        boolean typing;
        public TextBox(ArrayList<Point> points, int lineWidth, Color color, String text) {
            super(points, lineWidth, color);
            this.text = text;
            this.typing = true;

        }

        public String getText() {
            return this.text;
        }

        public void setText(String s) {
            this.text = s;
        }
    }

}