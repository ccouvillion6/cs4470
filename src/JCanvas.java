import dollar.DollarRecognizer;
import dollar.Result;


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
    private DollarRecognizer dr = new DollarRecognizer();
    private int shapeIndex;
    private Shape overlaps;
    private int grabX;
    private int grabY;
    private Rectangle2D upperHandle;
    private Rectangle2D lowerHandle;
    private boolean isDragging = false;


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
                if (selectedButton.equals("Select")) {
                    grabX = mouseEvent.getX();
                    grabY = mouseEvent.getY();
                    overlaps = findIntersections(mouseEvent);
                    repaint();
                } else {
                    if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                        isContext = false;
                    }
                    if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                        isContext = true;
                    }
                    shapeIndex = displayList.size() <= 0 ? 0 : displayList.size();
                    currentTextBox = null;
                    if (displayList.size() != 0) {
                        if (displayList.getLast() instanceof TextBox) {
                            ((TextBox)(displayList.getLast())).typing = false;
                        }
                    }
                    points = new ArrayList<>();
                    points.add(mouseEvent.getPoint());
                    drawing = true;
                }
                
            }


            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                drawing = false;
                isDragging = false;
                paintWindow.curr.repaint();
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
                    if (!displayList.isEmpty() && shapeIndex <= displayList.size()-1) {
                        displayList.subList(shapeIndex, displayList.size()-1).clear();
                    }

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
                if (selectedButton.equals("Select")) {
                    //draw grid
                    isDragging = true;
                    

                    Rectangle2D clickBounds = new Rectangle2D.Double(grabX - 2, grabY - 2, 4, 4);
                    int offsetX = mouseEvent.getX() - grabX;
                    int offsetY = mouseEvent.getY() - grabY;
                    if (overlaps != null) {
                        if (upperHandle != null && lowerHandle != null) {
                            if (clickBounds.intersects(upperHandle)) {
                                overlaps.points.add(0, new Point2D.Double(grabX + offsetX, grabY + offsetY));
                            } else if (clickBounds.intersects(lowerHandle)) {
                                overlaps.points.add(new Point2D.Double(grabX + offsetX, grabY + offsetY));
                            } else {
                                handleSnap(mouseEvent, overlaps);
                                
                            }
                        } else {
                            handleSnap(mouseEvent, overlaps);
                        }
                        
                        
                        repaint();
                    }
                    
                    grabX = mouseEvent.getX();
                    grabY = mouseEvent.getY();
                    repaint();

                    
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

    private String recognizeGesture() {
        Result result = dr.recognize(points);
        if (result == null || result.getMatchedTemplate() == null || result.getName() == null) {
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
        } else if (result.getName().equals("pigtail")) {
            paintWindow.lineWidthButton.doClick();
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

    private Shape findIntersections(MouseEvent e) {
        // returns the topmost shape in the display list that intersect the click (or near it)
        Shape toReturn = null;
        Rectangle2D clickBounds = new Rectangle2D.Double(e.getX() - 3, e.getY() - 3, 6, 6);
        for (int i = 0; i < displayList.size(); i++) {
            Shape s = displayList.get(i);
            if (s instanceof Rectangle) {
                ((Rectangle)s).upperLeft = ((Rectangle)s).calculateUpperLeft();
                double ulx = ((Rectangle)s).upperLeft.getX();
                double uly = ((Rectangle)s).upperLeft.getY();
                ((Rectangle)s).width = ((Rectangle)s).calculateWidth();
                double w = ((Rectangle)s).width;
                ((Rectangle)s).height = ((Rectangle)s).calculateHeight();
                double h = ((Rectangle)s).height;
                Rectangle2D boundingBox = new Rectangle2D.Double(ulx, uly, w, h);
                if (boundingBox.intersects(clickBounds)) {
                    toReturn = s;
                }

            } else if (s instanceof Oval) {
                ((Oval)s).upperLeft = ((Oval)s).calculateUpperLeft();
                double ulx = ((Oval)s).upperLeft.getX();
                double uly = ((Oval)s).upperLeft.getY();
                ((Oval)s).width = ((Oval)s).calculateWidth();
                double w = ((Oval)s).width;
                ((Oval)s).height = ((Oval)s).calculateHeight();
                double h = ((Oval)s).height;
                Rectangle2D boundingBox = new Rectangle2D.Double(ulx, uly, w, h);
                if (boundingBox.intersects(clickBounds)) {
                    toReturn = s;
                }
            } else if (s instanceof Line) {
                ((Line)s).p1 = ((Line)s).calculatep1();
                ((Line)s).p2 = ((Line)s).calculatep2();
                Line2D line = new Line2D.Double(((Line)s).p1.getX(), ((Line)s).p1.getY(), ((Line)s).p2.getX(), ((Line)s).p2.getY());
                if (clickBounds.intersectsLine(line)) {
                    toReturn = s;
                }
            } else if (s instanceof FreeFormMonstrosity) {
                for (Point2D q : s.points) {
                    Rectangle2D boundingBox = new Rectangle2D.Double(q.getX() - 3, q.getY() - 3, 6, 6);
                    if (boundingBox.intersects(clickBounds)) {
                        toReturn = s;
                    }
                }
            } else if (s instanceof TextBox) {
                double ulx = ((TextBox)s).upperLeft.getX();
                double uly = ((TextBox)s).upperLeft.getY();
                double w = ((TextBox)s).width;
                double h = ((TextBox)s).height;
                Rectangle2D boundingBox = new Rectangle2D.Double(ulx, uly, w, h);
                if (boundingBox.intersects(clickBounds)) {
                    toReturn = s;
                }
            }

        }
        return toReturn;
    }

    private void deleteObject() {
        //delete object
        double leftmost = 10000;
        double upmost = 10000;
        double rightmost = 0;
        double downmost = 0;
        for (Point2D p : points) {
            if (p.getX() > rightmost) {
                rightmost = p.getX();
            }
            if (p.getY() > downmost) {
                downmost = p.getY();
            }
            if (p.getX() < leftmost) {
                leftmost = p.getX();
            }
            if (p.getY() < upmost) {
                upmost = p.getY();
            }
        }
        Rectangle2D gestureBounds = new Rectangle2D.Double(leftmost - 3, upmost - 3, rightmost - leftmost + 3, downmost - upmost + 3);
        for (Shape s : displayList) {
            if (s instanceof Rectangle) {
                double ulx = ((Rectangle)s).upperLeft.getX();
                double uly = ((Rectangle)s).upperLeft.getY();
                double w = ((Rectangle)s).width;
                double h = ((Rectangle)s).height;
                Rectangle2D boundingBox = new Rectangle2D.Double(ulx, uly, w, h);
                if (boundingBox.intersects(gestureBounds)) {
                    s.setRemoveLater(true);
                }

            }
            if (s instanceof Oval) {
                double ulx = ((Oval)s).upperLeft.getX();
                double uly = ((Oval)s).upperLeft.getY();
                double w = ((Oval)s).width;
                double h = ((Oval)s).height;
                Rectangle2D boundingBox = new Rectangle2D.Double(ulx, uly, w, h);
                if (boundingBox.intersects(gestureBounds)) {
                    s.setRemoveLater(true);
                }
            }
            if (s instanceof TextBox) {

                double ulx = ((TextBox)s).upperLeft.getX();
                double uly = ((TextBox)s).upperLeft.getY();
                double w = ((TextBox)s).width;
                double h = ((TextBox)s).height;
                Rectangle2D boundingBox = new Rectangle2D.Double(ulx, uly, w, h);
                if (boundingBox.intersects(gestureBounds)) {
                    s.setRemoveLater(true);
                }

            }
            if (s instanceof Line) {
                Line2D line = new Line2D.Double(((Line)s).p1.getX(), ((Line)s).p1.getY(), ((Line)s).p2.getX(), ((Line)s).p2.getY());
                if (gestureBounds.intersectsLine(line)) {
                    s.setRemoveLater(true);
                }
            }
            if (s instanceof FreeFormMonstrosity) {
                for (Point2D q : s.points) {
                    Rectangle2D boundingBox = new Rectangle2D.Double(q.getX() - 3, q.getY() - 3, 6, 6);
                    if (boundingBox.intersects(gestureBounds)) {
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
        overlaps = null;
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

    private void handleSnap(MouseEvent mouseEvent, Shape s) {
        if (!isDragging || s == null) {
            return;
        }
        Rectangle2D bb = findBoundingBox(s.points);
        int offsetX = mouseEvent.getX() - grabX;
        int offsetY = mouseEvent.getY() - grabY;
        boolean north = false;
        boolean south = false;
        boolean east = false;
        boolean west = false;
        if (offsetX > 0) {
            east = true;
        }
        if (offsetX < 0) {
            west = true;
        }
        if (offsetY > 0) {
            south = true;
        }
        if (offsetY < 0) {
            north = true;
        }
        int threshold = 70;
        boolean isSnappedNorth = (bb.getY() % 100 == 0);
        boolean isSnappedSouth = ((bb.getY() + bb.getHeight()) % 100 == 0);
        boolean isSnappedWest = (bb.getX() % 100 == 0);
        boolean isSnappedEast = ((bb.getX() + bb.getWidth()) % 100 == 0);
        
        
        boolean toUnsnap = false;
        String statusBarText = "";
        if (north) {
            if (isSnappedNorth || isSnappedSouth) {
                toUnsnap = false;
                if (isSnappedNorth) {
                    if (bb.getY() - mouseEvent.getY() > threshold) {
                        toUnsnap = true;
                    }
                }
                if (isSnappedSouth) {
                    if (bb.getY() - mouseEvent.getY() > threshold) {
                        toUnsnap = true;
                    }
                }
                // check to see if we've exceeded the threshold to unsnap
                //if not, do nothing
                if (toUnsnap) {
                    //unsnap in direction of travel
                    statusBarText += "Unsnapped from NORTH ";
                    for (Point2D p : overlaps.points) {
                        p.setLocation(p.getX(), p.getY() + offsetY);
                    }
                    grabY = mouseEvent.getY();
                    repaint();
                }

            } else {
                double prevGrid = Math.floor(bb.getY()/100)*100;
                double gridDist = bb.getY() - prevGrid;
                if (gridDist < threshold && gridDist > 0) {
                    
                    //SNAP
                    if (statusBarText != "") {
                        statusBarText += " & ";
                    }
                    statusBarText += "Snapped to NORTH";
                    for (Point2D p : overlaps.points) {
                        
                        p.setLocation(p.getX(), p.getY() - gridDist);
                    }
                    grabY = mouseEvent.getY();
                    isSnappedNorth = true;
                    
                    repaint();
                   // isDragging = false;
                } else {

                    //no snap, just regular dragging
                    for (Point2D p : overlaps.points) {
                        p.setLocation(p.getX(), p.getY() + offsetY);
                    }
                    repaint();
                    grabY = mouseEvent.getY();
                }
                
            }
        } else if (south) {
            if (isSnappedNorth || isSnappedSouth) {
                // check to see if we've exceeded the threshold to unsnap
                //if not, do nothing
                toUnsnap = false;
                if (isSnappedNorth) {
                    if (mouseEvent.getY() - bb.getY() > threshold) {
                        toUnsnap = true;
                    }
                }
                if (isSnappedSouth) {
                    if (mouseEvent.getY() - bb.getY() > threshold) {
                        toUnsnap = true;
                    }
                }
                if (toUnsnap) {
                    //unsnap in direction of travel
                    if (statusBarText != "") {
                        statusBarText += " & ";
                    }
                    statusBarText += "Unsnapped from SOUTH";
                    for (Point2D p : overlaps.points) {
                        p.setLocation(p.getX(), p.getY() + offsetY);
                    }
                    grabY = mouseEvent.getY();
                    repaint();
                }

            } else {
                double nextGrid = Math.ceil((bb.getY() + bb.getHeight())/100)*100;
                double gridDist = nextGrid - bb.getY() - bb.getHeight();
                if (gridDist < threshold && gridDist > 0) {
                    if (statusBarText != "") {
                        statusBarText += " & ";
                    }
                    statusBarText += "Snapped to SOUTH";
                    //SNAP
                    for (Point2D p : overlaps.points) {
                        
                        p.setLocation(p.getX(), p.getY() + gridDist);
                    }
                    grabY = mouseEvent.getY();
                    isSnappedSouth = true;
                    
                    repaint();
                   // isDragging = false;
                } else {

                    //no snap, just regular dragging
                    for (Point2D p : overlaps.points) {
                        p.setLocation(p.getX(), p.getY() + offsetY);
                    }
                    repaint();
                    grabY = mouseEvent.getY();
                }
                
            }
        }

        bb = findBoundingBox(s.points);
        offsetX = mouseEvent.getX() - grabX;
        offsetY = mouseEvent.getY() - grabY;
        
        if (west) {
            if (isSnappedWest || isSnappedEast) {
                toUnsnap = false;
                if (isSnappedWest) {
                    if (bb.getX() - mouseEvent.getX() > threshold) {
                        toUnsnap = true;
                    }
                }
                if (isSnappedEast) {
                    if (bb.getX() - mouseEvent.getX() > threshold) {
                        toUnsnap = true;
                    }
                }
                // check to see if we've exceeded the threshold to unsnap
                //if not, do nothing
                if (toUnsnap) {
                    //unsnap in direction of travel
                    if (statusBarText != "") {
                        statusBarText += " & ";
                    }
                    statusBarText += "Unsnapped from WEST";
                    for (Point2D p : overlaps.points) {
                        p.setLocation(p.getX() + offsetX, p.getY());
                    }
                    grabX = mouseEvent.getX();
                    repaint();
                }

            } else {
                double prevGrid = Math.floor(bb.getX()/100)*100;
                double gridDist = bb.getX() - prevGrid;
                if (gridDist < threshold && gridDist > 0) {
                    
                    //SNAP
                    if (statusBarText != "") {
                        statusBarText += " & ";
                    }
                    statusBarText += "Snapped to WEST";
                    for (Point2D p : overlaps.points) {
                        
                        p.setLocation(p.getX() - gridDist, p.getY());
                    }
                    grabX = mouseEvent.getX();
                    isSnappedWest = true;
                    
                    repaint();
                  //  isDragging = false;
                } else {

                    //no snap, just regular dragging
                    for (Point2D p : overlaps.points) {
                        p.setLocation(p.getX() + offsetX, p.getY());
                    }
                    repaint();
                    grabX = mouseEvent.getX();
                }
                
            }
        } else if (east) {
            if (isSnappedWest || isSnappedEast) {
                // check to see if we've exceeded the threshold to unsnap
                //if not, do nothing
                toUnsnap = false;
                if (isSnappedWest) {
                    if (mouseEvent.getX() - bb.getX() > threshold) {
                        toUnsnap = true;
                    }
                }
                if (isSnappedEast) {
                    if (mouseEvent.getX() - bb.getX() > threshold) {
                        toUnsnap = true;
                    }
                }
                if (toUnsnap) {
                    //unsnap in direction of travel
                    if (statusBarText != "") {
                        statusBarText += " & ";
                    }
                    statusBarText += "Unsnapped from EAST";
                    for (Point2D p : overlaps.points) {
                        p.setLocation(p.getX() + offsetX, p.getY());
                    }
                    grabX = mouseEvent.getX();
                    repaint();
                }

            } else {
                double nextGrid = Math.ceil((bb.getX() + bb.getWidth())/100)*100;
                
                double gridDist = nextGrid - bb.getX() - bb.getWidth();
                if (gridDist < threshold && gridDist > 0) {
                    if (statusBarText != "") {
                        statusBarText += " & ";
                    }
                    statusBarText += "Snapped to EAST";
                    //SNAP
                    for (Point2D p : overlaps.points) {
                        
                        p.setLocation(p.getX() + gridDist, p.getY());
                    }
                    grabX = mouseEvent.getX();
                    isSnappedEast = true;
                    
                    repaint();
                    //isDragging = false;
                } else {

                    //no snap, just regular dragging
                    for (Point2D p : overlaps.points) {
                        p.setLocation(p.getX() + offsetX, p.getY());
                    }
                    repaint();
                    grabX = mouseEvent.getX();
                }
                
            }
        }
        if (statusBarText != "") {
            paintWindow.statusBar.setText(statusBarText);
        }



    }

    private Rectangle2D findBoundingBox(ArrayList<Point2D> points) {
        double leftmost = 10000;
        double upmost = 10000;
        double rightmost = 0;
        double downmost = 0;
        for (Point2D p : points) {
            if (p.getX() > rightmost) {
                rightmost = p.getX();
            }
            if (p.getY() > downmost) {
                downmost = p.getY();
            }
            if (p.getX() < leftmost) {
                leftmost = p.getX();
            }
            if (p.getY() < upmost) {
                upmost = p.getY();
            }
        }
        Point2D p1 = new Point2D.Double(leftmost, upmost);
        Point2D p2 = new Point2D.Double(rightmost, downmost);
        double w = rightmost - leftmost;
        double h = downmost - upmost;
        Rectangle2D boundingBox = new Rectangle2D.Double(leftmost, upmost, w, h);
        return boundingBox;
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

        //draw grid
        if (isDragging) {
            Graphics2D g2d = (Graphics2D) g.create();
            Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
            g2d.setStroke(dashed);
            g2d.setColor(Color.GRAY);
            for (int x=100 ; x<getWidth() ; x += 100) {
                g2d.drawLine(x, 0, x, getHeight());
            }
            for (int y=100 ; y<getHeight() ; y += 100) {
                g2d.drawLine(0, y, getWidth(), y);
            }
        }

        for (Shape s : displayList) {
            g2.setStroke(new BasicStroke(s.lineWidth));
            g2.setColor(s.color);
            if (s instanceof Line) {
                ((Line)s).p1 = ((Line)s).calculatep1();
                ((Line)s).p2 = ((Line)s).calculatep2();
                g2.drawLine((int)((Line)s).p1.getX(), (int)((Line)s).p1.getY(), (int)((Line)s).p2.getX(), (int)((Line)s).p2.getY());
            }
            if (s instanceof Oval) {
                Oval ess = ((Oval)s);
                ess.upperLeft = ess.calculateUpperLeft();
                ess.width = ess.calculateWidth();
                ess.height = ess.calculateHeight();
                g2.draw(new Ellipse2D.Double(ess.upperLeft.getX(), ess.upperLeft.getY(), ess.width, ess.height));
            }
            if (s instanceof Rectangle) {
                Rectangle ess = ((Rectangle)s);
                ess.upperLeft = ess.calculateUpperLeft();
                ess.width = ess.calculateWidth();
                ess.height = ess.calculateHeight();
                g2.drawRect((int)(ess.upperLeft.getX()), (int)(ess.upperLeft.getY()), (int)ess.width, (int)ess.height);
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
                ess.upperLeft = ess.calculateUpperLeft();
                ess.width = ess.calculateWidth();
                ess.height = ess.calculateHeight();
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
                    }
                    if (!wrapped) {
                        g2.drawString(word + "", letterX, letterY);
                        letterX += fontWidth;

                    }

                }
            }

        }

        //draw resize handles
        if (overlaps != null) {
            if (overlaps instanceof Rectangle) {
                ((Rectangle)overlaps).upperLeft = ((Rectangle)overlaps).calculateUpperLeft();
                ((Rectangle)overlaps).width = ((Rectangle)overlaps).calculateWidth();
                ((Rectangle)overlaps).height = ((Rectangle)overlaps).calculateHeight();
                int ulx = (int)((Rectangle)overlaps).upperLeft.getX() - 2;
                int uly = (int)((Rectangle)overlaps).upperLeft.getY() - 2;
                int lrx = (int)((Rectangle)overlaps).upperLeft.getX() - 2 + (int)((Rectangle)overlaps).width;
                int lry = (int)((Rectangle)overlaps).upperLeft.getY() - 2 + (int)((Rectangle)overlaps).height;
                upperHandle = new Rectangle2D.Double(ulx, uly, 4, 4);
                lowerHandle = new Rectangle2D.Double(lrx, lry, 4, 4);
                g2.drawRect(ulx, uly, 4, 4);
                g2.drawRect(lrx, lry, 4, 4);
                
            } else if (overlaps instanceof Oval) {
                ((Oval)overlaps).upperLeft = ((Oval)overlaps).calculateUpperLeft();
                ((Oval)overlaps).width = ((Oval)overlaps).calculateWidth();
                ((Oval)overlaps).height = ((Oval)overlaps).calculateHeight();
                int ulx = (int)((Oval)overlaps).upperLeft.getX() - 2;
                int uly = (int)((Oval)overlaps).upperLeft.getY() - 2;
                int lrx = (int)((Oval)overlaps).upperLeft.getX() - 2 + (int)((Oval)overlaps).width;
                int lry = (int)((Oval)overlaps).upperLeft.getY() - 2 + (int)((Oval)overlaps).height;
                upperHandle = new Rectangle2D.Double(ulx, uly, 4, 4);
                lowerHandle = new Rectangle2D.Double(lrx, lry, 4, 4);
                g2.drawRect(ulx, uly, 4, 4);
                g2.drawRect(lrx, lry, 4, 4);
            } else if (overlaps instanceof Line) {
                ((Line)overlaps).p1 = ((Line)overlaps).calculatep1();
                ((Line)overlaps).p2 = ((Line)overlaps).calculatep2();
                int p1x = (int)((Line)overlaps).p1.getX() - 2;
                int p1y = (int)((Line)overlaps).p1.getY() - 2;
                int p2x = (int)((Line)overlaps).p2.getX() - 2;
                int p2y = (int)((Line)overlaps).p2.getY() - 2;
                upperHandle = new Rectangle2D.Double(p1x, p1y, 4, 4);
                lowerHandle = new Rectangle2D.Double(p2x, p2y, 4, 4);
                g2.drawRect(p1x, p1y, 4, 4);
                g2.drawRect(p2x, p2y, 4, 4);

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

        public Point2D calculatep1() {
            return new Point2D.Double(this.points.get(0).getX(), this.points.get(0).getY());
        }
        Point2D p1 = calculatep1();
        public Point2D calculatep2() {
            return new Point2D.Double(this.points.get(this.points.size()-1).getX(), this.points.get(this.points.size()-1).getY());
        }
        Point2D p2 = calculatep2();

    }

    public class Oval extends Shape {
        public Oval(ArrayList<Point2D> points, int lineWidth, Color color) {
            super(points, lineWidth, color);
        }
        public Point2D calculateUpperLeft() {
            return new Point2D.Double((int)Math.min(this.points.get(0).getX(), this.points.get(this.points.size()-1).getX()),
            (int)Math.min(this.points.get(0).getY(), this.points.get(this.points.size()-1).getY()));
        }
        public Point2D upperLeft = calculateUpperLeft();

        public double calculateWidth() {
            return (int)Math.abs(this.points.get(this.points.size()-1).getX()-this.points.get(0).getX());
        }
        public double width = calculateWidth();

        public double calculateHeight() {
            return (int)Math.abs(this.points.get(this.points.size()-1).getY()-this.points.get(0).getY());
        }
        public double height = calculateHeight();
    }

    public class Rectangle extends Shape {
        public Rectangle(ArrayList<Point2D> points, int lineWidth, Color color) {
            super(points, lineWidth, color);
        }
        public Point2D calculateUpperLeft() {
            return new Point2D.Double((int)Math.min(this.points.get(0).getX(), this.points.get(this.points.size()-1).getX()),
            (int)Math.min(this.points.get(0).getY(), this.points.get(this.points.size()-1).getY()));
        }
        public Point2D upperLeft = calculateUpperLeft();

        public double calculateWidth() {
            return (int)Math.abs(this.points.get(this.points.size()-1).getX()-this.points.get(0).getX());
        }
        public double width = calculateWidth();

        public double calculateHeight() {
            return (int)Math.abs(this.points.get(this.points.size()-1).getY()-this.points.get(0).getY());
        }
        public double height = calculateHeight();
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

        

        public Point2D calculateUpperLeft() {
            return new Point2D.Double((int)Math.min(this.points.get(0).getX(), this.points.get(this.points.size()-1).getX()),
            (int)Math.min(this.points.get(0).getY(), this.points.get(this.points.size()-1).getY()));
        }
        public Point2D upperLeft = calculateUpperLeft();

        public double calculateWidth() {
            return (int)Math.abs(this.points.get(this.points.size()-1).getX()-this.points.get(0).getX());
        }
        public double width = calculateWidth();

        public double calculateHeight() {
            return (int)Math.abs(this.points.get(this.points.size()-1).getY()-this.points.get(0).getY());
        }
        public double height = calculateHeight();

    }

}