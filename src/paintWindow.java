import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class paintWindow {

    public static LinkedList<JCanvas> canvases = new LinkedList<>();
    public static JCanvas curr;
    public static JCanvas prev;
    public static JCanvas next;
    public static int currIndex = 0;
    public static String selectedButton = "Select";

    private static void createAndShowGUI()  {

        int APP_WIDTH = 700;
        int APP_HEIGHT = 400;

        // create a new JFrame, which is a top-level window
        JFrame frame = new JFrame("Main Window");
        // Tell the frame that the application should exit when we close it
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(300, 350));
        frame.setLayout(new BorderLayout());
        frame.setSize(new Dimension(APP_WIDTH, APP_HEIGHT));

        //Main content panel
        JCanvas contentArea = new JCanvas();
        contentArea.setBackground(Color.white);
        canvases.add(contentArea);
        curr = contentArea;
        frame.getContentPane().add(contentArea, BorderLayout.CENTER);

        // where to add the action listener??

        //make it scrollable
        JScrollPane scrollPane = new JScrollPane(contentArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //scrollPane.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);




        // Status bar
        JLabel statusBar = new JLabel();
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
        statusBar.setBackground(Color.darkGray);
        statusBar.setText(" Status");
        statusBar.setForeground(Color.white);
        statusBar.setOpaque(true);
        statusBar.setVisible(true);

        // Action listener for toolbar selection
        class MyActionListener implements ActionListener {
            public void actionPerformed(ActionEvent actionEvent) {
                selectedButton = actionEvent.getActionCommand();
                statusBar.setText(" " + actionEvent.getActionCommand() + " button clicked");
            }
        }



        //Menu
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);


        //File Menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // New
        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(new MyActionListener());
        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JCanvas newCanvas = new JCanvas();
                prev = curr;
                curr = newCanvas;
                currIndex += 1;
            }
        });

        // Delete
        JMenuItem deleteItem = new JMenuItem("Delete");
        if (canvases.size() < 2) {
            deleteItem.setEnabled(false);
        }
        deleteItem.addActionListener(new MyActionListener());
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (prev == null) {
                    canvases.remove(curr);
                    curr = canvases.getFirst();
                    next = canvases.get(1);
                } else {
                    canvases.remove(curr);
                    curr = prev;
                    prev = canvases.get(currIndex - 1);
                }
            }
        });

        // Quit
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(new MyActionListener());
        fileMenu.add(newItem);
        fileMenu.add(deleteItem);
        fileMenu.add(quitItem);
        quitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(1);
            }
        });

        //Edit menu
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        // View menu
        JMenu viewMenu = new JMenu("View");
        menuBar.add(viewMenu);
        // Next
        JMenuItem nextItem = new JMenuItem("Next");
        if (next == null) {
            nextItem.setEnabled(false);
        }
        nextItem.addActionListener(new MyActionListener());
        nextItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                prev = curr;
                curr = next;
                currIndex += 1;
                if (canvases.get(currIndex + 1) != null) {
                    next = canvases.get(currIndex - 1);
                } else {
                    next = null;
                }
            }
        });
        // Previous
        JMenuItem previousItem = new JMenuItem("Previous");
        if (prev == null) {
            previousItem.setEnabled(false);
        }
        previousItem.addActionListener(new MyActionListener());
        previousItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                next = curr;
                curr = prev;
                currIndex -= 1;
                if (canvases.get(currIndex - 1) != null) {
                    prev = canvases.get(currIndex - 1);
                } else {
                    prev = null;
                }
            }
        });
        viewMenu.add(nextItem);
        viewMenu.add(previousItem);



        // Tool palette
        JPanel toolPalette = new JPanel();
        Dimension toolPaletteDim = new Dimension(100,250);
        toolPalette.setSize(toolPaletteDim);
        toolPalette.setMaximumSize(toolPaletteDim);
        toolPalette.setBorder(BorderFactory.createLineBorder(Color.black));
        toolPalette.setLayout(new BoxLayout(toolPalette, BoxLayout.Y_AXIS));

        // Select button
        toolPalette.add(Box.createRigidArea(new Dimension(0,10)));
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(new MyActionListener());
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(selectButton);
        toolPalette.add(Box.createRigidArea(new Dimension(0,10)));

        // Color select button
        JButton colorSelectButton = new JButton("Color");
        colorSelectButton.addActionListener(new MyActionListener());
        colorSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Color chosenColor = JColorChooser.showDialog(frame, "Choose a Color", Color.black);
                // change JColorChooser as needed
                if (chosenColor != null) {
                    // change the color of the selected tool
                }
            }
        });
        colorSelectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(colorSelectButton);
        toolPalette.add(Box.createRigidArea(new Dimension(0,10)));

        // Line tool button
        JButton lineToolButton = new JButton("Line");
        lineToolButton.addActionListener(new MyActionListener());
        lineToolButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(lineToolButton);
        toolPalette.add(Box.createRigidArea(new Dimension(0,10)));

        // Line width button
        JButton lineWidthButton = new JButton("Line Width");
        lineWidthButton.addActionListener(new MyActionListener());
        lineWidthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                Object[] options = {"Super Skinny", "Skinny", "Average", "Thicc", "Dummy Thicc"};
                JOptionPane.showOptionDialog(frame, "", "Line Width", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
            }
        });
        lineWidthButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(lineWidthButton);
        toolPalette.add(Box.createRigidArea(new Dimension(0,10)));

        // Rectangle tool button
        JButton rectangleToolButton = new JButton("Rectangle");
        rectangleToolButton.addActionListener(new MyActionListener());
        rectangleToolButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(rectangleToolButton);
        toolPalette.add(Box.createRigidArea(new Dimension(0,10)));

        // Oval tool button
        JButton ovalToolButton = new JButton("Oval");
        ovalToolButton.addActionListener(new MyActionListener());
        ovalToolButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(ovalToolButton);
        toolPalette.add(Box.createRigidArea(new Dimension(0,10)));

        // Pen tool button
        JButton penToolButton = new JButton("Pen");
        penToolButton.addActionListener(new MyActionListener());
        penToolButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(penToolButton);
        toolPalette.add(Box.createRigidArea(new Dimension(0,10)));

        // Text tool button
        JButton textToolButton = new JButton("Text");
        textToolButton.addActionListener(new MyActionListener());
        textToolButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(textToolButton);
        toolPalette.add(Box.createRigidArea(new Dimension(0,10)));
        toolPalette.setVisible(true);


        frame.getContentPane().add(toolPalette, BorderLayout.WEST);


        // pack() causes the size of the frame to be set just large enough to contain its
        // children; setVisible(true) puts it on the screen
        //frame.pack();
        frame.setVisible(true);
    }




    // This is a common way of starting a swing application. Create a main() function that
    // uses SwingUtilities.invokeLater to call a function that creates the GUI.  This approach
    // is used for thread safety. Alternatively, you could change HelloWorld so that its
    // constructor creates the GUI; then, the code in main uses invokeLater to create a new
    // instance of HelloWorld
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}