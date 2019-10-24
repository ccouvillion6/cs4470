import dollar.DollarRecognizer;
import dollar.Result;
import org.w3c.dom.Text;

//problem: referencing non-static method from static context?

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.ArrayList;

public class JCanvas extends JPanel {

    private LinkedList<Shape> displayList;
    private String selectedButton;
    private ArrayList<Point2D> points = new ArrayList<>();
    private boolean drawing = false;
    private TextBox currentTextBox = null;
    private boolean isContext = false;
    public DollarRecognizer dr = new DollarRecognizer();
    private int shapeIndex;

    public JCanvas() {
        this.displayList = new LinkedList<>();
        this.selectedButton = paintWindow.selectedButton;
        this.addMouseListener(new MouseListener() {



            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    isContext = false;
                }
                if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                    isContext = true;
                    shapeIndex = displayList.size() <= 0 ? 0 : displayList.size();
                }
                currentTextBox = null;
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
                if (isContext) {
                    String gesture = recognizeGesture();
                    // remove gesture drawing from display list
                    if (gesture.equals("rectangle") || gesture.equals("circle")) {
                        displayList.subList(shapeIndex, displayList.size()-1).clear();
                    } else {
                        if (shapeIndex <= displayList.size()) {
                            displayList.subList(shapeIndex, displayList.size()).clear();
                        }
                    }
                    repaint();
                    if (gesture.equals("x")) {
                        deleteObject();
                    }


                } else {
                    displayList.subList(shapeIndex, displayList.size()-1).clear();
                }
                isContext = false;
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
                    alterDisplayList(points);
                }
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        } );

        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if (displayList.getLast() instanceof TextBox) {
                    boolean isTyping = ((TextBox)displayList.getLast()).typing;
                    if (isTyping) {
                        ArrayList<String> textArray = ((TextBox)(displayList.getLast())).text;
                        String currWord = "";
                        if (textArray.size() > 0) {
                            currWord = textArray.get(textArray.size()-1);
                        }
                        String newChar = keyEvent.getKeyChar() + "";
                        if (textArray.size() == 0) {
                            textArray.add(newChar);
                        } else {
                            textArray.remove(currWord);
                            currWord += newChar;
                            textArray.add(currWord);
                            if (newChar.equals(" ")) {
                                textArray.add("");
                            }
                        }
                        ((TextBox)(displayList.getLast())).text = textArray;
                        repaint();
                    }
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

    public String recognizeGesture() {
        Result result = dr.recognize(points);
        if (result == null) {
            paintWindow.statusBar.setText(" Gesture was not recognized");
            return "";

        } else if (result.getName().equals("v")) {
            if (paintWindow.nextItem.isEnabled()) {
                paintWindow.nextItem.doClick();
                paintWindow.statusBar.setText(" Next gesture recognized");
            } else {
                paintWindow.statusBar.setText(" Next canvas is not available");
            }
        } else if (result.getName().equals("caret")) {
            if (paintWindow.previousItem.isEnabled()) {
                paintWindow.previousItem.doClick();
                paintWindow.statusBar.setText(" Previous gesture recognized");
            } else {
                paintWindow.statusBar.setText(" Previous canvas is not available");
            }
        } else if (result.getName().equals("star")) {
            if (paintWindow.newItem.isEnabled()) {
                paintWindow.newItem.doClick();
                paintWindow.statusBar.setText(" New gesture recognized");
            } else {
                paintWindow.statusBar.setText(" New canvas could not be created");
            }
        } else if (result.getName().equals("delete")) {
            if (paintWindow.deleteItem.isEnabled()) {
                paintWindow.deleteItem.doClick();
                paintWindow.statusBar.setText(" Delete canvas gesture recognized");
            } else {
                paintWindow.statusBar.setText(" Canvas could not be deleted");
            }
        } else if (result.getName().equals("rectangle")) {
            paintWindow.statusBar.setText(" Rectangle gesture recognized");
            // find bottom right corner of rectangle
            double rightmost = 0;
            double downmost = 0;
            for (Point2D p : points) {
                if (p.getX() > rightmost) {
                    rightmost = p.getX();
                }
                if (p.getY() > downmost) {
                    downmost = p.getY();
                }
            }
            ArrayList<Point2D> newArr = new ArrayList<>();
            newArr.add(points.get(0));
            newArr.add(new Point2D.Double(rightmost, downmost));
            Rectangle newRect = new Rectangle(newArr, paintWindow.lineWidth, paintWindow.chosenColor);
            displayList.add(newRect);
            this.repaint();
        } else if (result.getName().equals("circle")) {
            paintWindow.statusBar.setText(" Oval gesture recognized");
            // find bottom right corner of bounding rectangle of oval
            double rightmost = 0;
            double downmost = 0;
            for (Point2D p : points) {
                if (p.getX() > rightmost) {
                    rightmost = p.getX();
                }
                if (p.getY() > downmost) {
                    downmost = p.getY();
                }
            }
            ArrayList<Point2D> newArr = new ArrayList<>();
            newArr.add(points.get(0));
            newArr.add(new Point2D.Double(rightmost, downmost));
            Oval newOval = new Oval(newArr, paintWindow.lineWidth, paintWindow.chosenColor);
            displayList.add(newOval);
            this.repaint();
        } else if (result.getName().equals("x")) {
            paintWindow.statusBar.setText(" Delete object gesture recognized");
        } else {
            paintWindow.statusBar.setText(" Gesture was not recognized");
            return "";
        }
        return result.getName();


    }

    private void deleteObject() {
        //delete object
        for (Shape s : displayList) {
            System.out.println(s);
            if (s instanceof Rectangle) {
                for (Point2D p : s.points) {
                    double ulx = ((Rectangle)s).upperLeft.getX();
                    double uly = ((Rectangle)s).upperLeft.getY();
                    double w = ((Rectangle)s).width;
                    double h = ((Rectangle)s).height;
                    if (p.getX() >= ulx && p.getY() >= uly && p.getX() <= ulx + w && p.getY() <= uly + h) {
                        s.setRemoveLater(true);
                        System.out.println("set");
                    }
                }
            }
            if (s instanceof Oval) {
                for (Point2D p : s.points) {
                    double ulx = ((Oval)s).upperLeft.getX();
                    double uly = ((Oval)s).upperLeft.getY();
                    double w = ((Oval)s).width;
                    double h = ((Oval)s).height;
                    if (p.getX() >= ulx && p.getY() >= uly && p.getX() <= ulx + w && p.getY() <= uly + h) {
                        s.setRemoveLater(true);
                    }
                }
            }
            if (s instanceof TextBox) {
                for (Point2D p : s.points) {
                    double ulx = ((TextBox)s).upperLeft.getX();
                    double uly = ((TextBox)s).upperLeft.getY();
                    double w = ((TextBox)s).width;
                    double h = ((TextBox)s).height;
                    if (p.getX() >= ulx && p.getY() >= uly && p.getX() <= ulx + w && p.getY() <= uly + h) {
                        s.setRemoveLater(true);
                    }
                }
            }
            if (s instanceof Line) {
                for (Point2D p : s.points) {
                    Rectangle2D boundingBox = new Rectangle2D.Double(p.getX() - 3, p.getY() - 3, 6, 6);
                    Line2D line = new Line2D.Double(((Line)s).p1.getX(), ((Line)s).p1.getY(), ((Line)s).p2.getX(), ((Line)s).p2.getY());

                    if (boundingBox.intersectsLine(line)) {
                        s.setRemoveLater(true);
                    }
                }
            }


        }
        int len = displayList.size();
        for (int i = 0; i < len; i++) {
            if (displayList.size() > i) {
                if (displayList.get(i).removeLater) {
                    displayList.remove(displayList.get(i));
                    i--;
                    repaint();
                }
            }
        }
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
            for (Point2D p : s.points) {
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

    private Shape alterDisplayList(ArrayList<Point2D> points) {
        if (isContext) {
            FreeFormMonstrosity newMonstrosity = new FreeFormMonstrosity(points, paintWindow.lineWidth, Color.lightGray, true);
            displayList.add(newMonstrosity);
            this.repaint();
            return newMonstrosity;
        }
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
            FreeFormMonstrosity newMonstrosity;
            if (isContext) {
                newMonstrosity = new FreeFormMonstrosity(points, paintWindow.lineWidth, Color.lightGray, true);
            } else {
                newMonstrosity = new FreeFormMonstrosity(points, paintWindow.lineWidth, paintWindow.chosenColor, false);
            }
            displayList.add(newMonstrosity);
            this.repaint();
            return newMonstrosity;
        } else if (selectedButton.equals("Text")) {
            TextBox newTextBox = new TextBox(points, paintWindow.lineWidth, paintWindow.chosenColor, new ArrayList<>());
            displayList.add(newTextBox);
            newTextBox.typing = true;
            currentTextBox = newTextBox;
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
                g2.drawLine((int)s.points.get(0).getX(), (int)s.points.get(0).getY(), (int)s.points.get(s.points.size()-1).getX(),
                        (int)s.points.get(s.points.size()-1).getY());
            }
            if (s instanceof Oval) {
                g2.draw(new Ellipse2D.Double(Math.min(s.points.get(0).getX(), s.points.get(s.points.size()-1).getX()),
                        Math.min(s.points.get(0).getY(), s.points.get(s.points.size()-1).getY()),
                        Math.abs(s.points.get(s.points.size()-1).getX()-s.points.get(0).getX()),
                        Math.abs(s.points.get(s.points.size()-1).getY()-s.points.get(0).getY())));
            }
            if (s instanceof Rectangle) {
                g2.drawRect((int)Math.min(s.points.get(0).getX(), s.points.get(s.points.size()-1).getX()),
                        (int)Math.min(s.points.get(0).getY(), s.points.get(s.points.size()-1).getY()),
                        (int)Math.abs(s.points.get(s.points.size()-1).getX()-s.points.get(0).getX()),
                        (int)Math.abs(s.points.get(s.points.size()-1).getY()-s.points.get(0).getY()));
            }
            if (s instanceof FreeFormMonstrosity) {
                for (int i = 0; i < s.points.size() - 2; i++) {
                    Point2D p1 = s.points.get(i);
                    Point2D p2 = s.points.get(i + 1);
                    g2.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
                }
            }
            if (s instanceof TextBox) {
                // original bounding rectangle
                TextBox ess = (TextBox) s;
                if (ess.equals(currentTextBox)) {
                    g2.drawRect((int)Math.min(s.points.get(0).getX(), s.points.get(s.points.size() - 1).getX()),
                            (int)Math.min(s.points.get(0).getY(), s.points.get(s.points.size() - 1).getY()),
                            (int)Math.abs(s.points.get(s.points.size() - 1).getX() - s.points.get(0).getX()),
                            (int)Math.abs(s.points.get(s.points.size() - 1).getY() - s.points.get(0).getY()));
                }

                // USE FONTMETRICS!!
                int xedge = (int)Math.max(s.points.get(s.points.size() - 1).getX(), s.points.get(0).getX());
                int yedge = (int)Math.max(s.points.get(s.points.size() - 1).getY(), s.points.get(0).getY());
                int numExtraLines = 0;
                FontMetrics metrics = g2.getFontMetrics(g2.getFont());
                int fontHeight = metrics.getHeight();
                int fontWidth;
                int letterX = (int)Math.min(s.points.get(0).getX(), s.points.get(s.points.size() - 1).getX()) + 5;
                int letterY = (int)Math.min(s.points.get(0).getY(), s.points.get(s.points.size() - 1).getY()) + 15;

                for (int i = 0; i < ess.getText().size(); i++) {
                    if (((TextBox) s).typing) {
                        g2.drawRect((int)Math.min(s.points.get(0).getX(), s.points.get(s.points.size() - 1).getX()),
                                (int)Math.min(s.points.get(0).getY(), s.points.get(s.points.size() - 1).getY()),
                                (int)Math.abs(s.points.get(s.points.size() - 1).getX() - s.points.get(0).getX()),
                                (int)Math.abs(s.points.get(s.points.size() - 1).getY() - s.points.get(0).getY()) + numExtraLines * fontHeight);
                    }
                    boolean wrapped = false;
                    String word = ess.getText().get(i);
                    fontWidth = metrics.stringWidth(word);
                    if (letterX + fontWidth + 10 >= xedge) {
                        letterY += fontHeight;
                        letterX = (int)Math.min(s.points.get(0).getX(), s.points.get(s.points.size() - 1).getX()) + 5;
                        wrapped = true;
                    }
                    if (letterY + 10 >= yedge) {
                        if (ess.getText().size() == 1) {
                            try {
                                throw new Exception("Tiny text boxes or long words make this thing crash.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        numExtraLines += 1;
                        yedge += fontHeight;
                        s.points.add(new Point2D.Double(Math.max(s.points.get(0).getX(), s.points.get(s.points.size() - 1).getX()), yedge));
                        wrapped = true;
                    }
                    if (wrapped) {
                        i -= 1;
                        //g2.drawRect(Math.min(s.points.get(0).x, s.points.get(s.points.size() - 1).x),
                        //        Math.min(s.points.get(0).y, s.points.get(s.points.size() - 1).y), Math.abs(s.points.get(s.points.size() - 1).x - s.points.get(0).x),
                        //        Math.abs(s.points.get(s.points.size() - 1).y - s.points.get(0).y) + numExtraLines * fontHeight);

                    }
                    if (!wrapped) {
                        g2.drawString(word + "", letterX, letterY);
                        //System.out.println(ess.getText().charAt(i) + " at " + letterX + " , " + letterY);
                        letterX += fontWidth;
                        //setCanvasBounds();

                    }

                }
            }

        }

    }

    public class Shape extends JComponent {
        ArrayList<Point2D> points;
        int lineWidth;
        Color color;
        boolean isContext;

        public Shape(ArrayList<Point2D> points, int lineWidth, Color color) {
            this.points = points;
            this.lineWidth = lineWidth;
            this.color = color;
        }

        boolean removeLater = false;

        public void setRemoveLater(boolean removeLater) {
            this.removeLater = removeLater;
        }


    }

    public class Line extends Shape {

        public Line(ArrayList<Point2D> points, int lineWidth, Color color) {
            super(points, lineWidth, color);
        }

        Point2D p1 = new Point2D.Double(this.points.get(0).getX(), this.points.get(0).getY());
        Point2D p2 = new Point2D.Double(this.points.get(this.points.size()-1).getX(), this.points.get(this.points.size()-1).getY());

    }

    public class Oval extends Shape {
        public Oval(ArrayList<Point2D> points, int lineWidth, Color color) {
            super(points, lineWidth, color);
        }
        public Point2D upperLeft = new Point2D.Double((int)Math.min(this.points.get(0).getX(), this.points.get(this.points.size()-1).getX()),
                (int)Math.min(this.points.get(0).getY(), this.points.get(this.points.size()-1).getY()));
        public double width = (int)Math.abs(this.points.get(this.points.size()-1).getX()-this.points.get(0).getX());
        public double height = (int)Math.abs(this.points.get(this.points.size()-1).getY()-this.points.get(0).getY());
    }

    public class Rectangle extends Shape {
        public Rectangle(ArrayList<Point2D> points, int lineWidth, Color color) {
            super(points, lineWidth, color);
        }
        public Point2D upperLeft = new Point2D.Double((int)Math.min(this.points.get(0).getX(), this.points.get(this.points.size()-1).getX()),
                (int)Math.min(this.points.get(0).getY(), this.points.get(this.points.size()-1).getY()));
        public double width = (int)Math.abs(this.points.get(this.points.size()-1).getX()-this.points.get(0).getX());
        public double height = (int)Math.abs(this.points.get(this.points.size()-1).getY()-this.points.get(0).getY());
    }

    public class FreeFormMonstrosity extends Shape {
        public FreeFormMonstrosity(ArrayList<Point2D> points, int lineWidth, Color color, boolean isContext) {
            super(points, lineWidth, color);
            this.isContext = isContext;
        }
    }

    public class TextBox extends Shape {
        ArrayList<String> text;
        boolean typing;
        public TextBox(ArrayList<Point2D> points, int lineWidth, Color color, ArrayList<String> text) {
            super(points, lineWidth, color);
            this.text = text;
            this.typing = true;

        }

        public ArrayList<String> getText() {
            return this.text;
        }

        public Point2D upperLeft = new Point2D.Double((int)Math.min(this.points.get(0).getX(), this.points.get(this.points.size()-1).getX()),
                (int)Math.min(this.points.get(0).getY(), this.points.get(this.points.size()-1).getY()));
        public double width = (int)Math.abs(this.points.get(this.points.size()-1).getX()-this.points.get(0).getX());
        public double height = (int)Math.abs(this.points.get(this.points.size()-1).getY()-this.points.get(0).getY());

    }

}