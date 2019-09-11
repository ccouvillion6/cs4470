

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class paintWindow {

    private static void createAndShowGUI()  {

        // create a new JFrame, which is a top-level window
        JFrame frame = new JFrame("Main Window");
        // Tell the frame that the application should exit when we close it
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Create a new JLabel, which displays a text string
        frame.setLayout(new BorderLayout());
        frame.setSize(new Dimension(700,400));

        //Main content panel
        JPanel contentArea = new JPanel();
        frame.getContentPane().add(contentArea);

        // Status bar
        JLabel statusBar = new JLabel();
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
        statusBar.setOpaque(true);
        statusBar.setVisible(true);

        // Action listener for status bar
        class MyActionListener implements ActionListener {
            public void actionPerformed(ActionEvent actionEvent) {
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
        // Delete
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new MyActionListener());
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
        nextItem.addActionListener(new MyActionListener());
        // Previous
        JMenuItem previousItem = new JMenuItem("Previous");
        previousItem.addActionListener(new MyActionListener());
        viewMenu.add(nextItem);
        viewMenu.add(previousItem);



        // Tool palette
        JPanel toolPalette = new JPanel();
        Dimension toolPaletteDim = new Dimension(100,250);
        toolPalette.setSize(toolPaletteDim);
        toolPalette.setBorder(BorderFactory.createLineBorder(Color.black));
        toolPalette.setLayout(new BoxLayout(toolPalette, BoxLayout.Y_AXIS));

        // Select button
        toolPalette.add(Box.createVerticalGlue());
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(new MyActionListener());
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(selectButton);
        toolPalette.add(Box.createVerticalGlue());

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
        toolPalette.add(Box.createVerticalGlue());

        // Line tool button
        JButton lineToolButton = new JButton("Line");
        lineToolButton.addActionListener(new MyActionListener());
        lineToolButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(lineToolButton);
        toolPalette.add(Box.createVerticalGlue());

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
        toolPalette.add(Box.createVerticalGlue());

        // Rectangle tool button
        JButton rectangleToolButton = new JButton("Rectangle");
        rectangleToolButton.addActionListener(new MyActionListener());
        rectangleToolButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(rectangleToolButton);
        toolPalette.add(Box.createVerticalGlue());

        // Oval tool button
        JButton ovalToolButton = new JButton("Oval");
        ovalToolButton.addActionListener(new MyActionListener());
        ovalToolButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(ovalToolButton);
        toolPalette.add(Box.createVerticalGlue());

        // Pen tool button
        JButton penToolButton = new JButton("Pen");
        penToolButton.addActionListener(new MyActionListener());
        penToolButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(penToolButton);
        toolPalette.add(Box.createVerticalGlue());

        // Text tool button
        JButton textToolButton = new JButton("Text");
        textToolButton.addActionListener(new MyActionListener());
        textToolButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPalette.add(textToolButton);
        toolPalette.add(Box.createVerticalGlue());
        toolPalette.setVisible(true);


        frame.getContentPane().add(toolPalette);

        // Placeholder
        JLabel label = new JLabel("Stuff goes here eventually");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        // Each frame has a JPanel called its contentPane—this is where the window contents
        // go. Get the content frame from the panel, and add the label as its child
        frame.getContentPane().add(label);

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