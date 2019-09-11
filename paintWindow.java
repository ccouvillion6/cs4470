import javax.swing.*;

public class paintWindow {

    private static void createAndShowGUI() {
        // create a new JFrame, which is a top-level window
        JFrame frame = new JFrame("Main Window");
        // Tell the frame that the application should exit when we close it
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Create a new JLabel, which displays a text string

        //Menu
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        //File Menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem quitItem = new JMenuItem("Quit");
        fileMenu.add(newItem);
        fileMenu.add(deleteItem);
        fileMenu.add(quitItem);
        //Edit menu
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);
        // View menu
        JMenu viewMenu = new JMenu("View");
        menuBar.add(viewMenu);
        JMenuItem nextItem = new JMenuItem("Next");
        JMenuItem previousItem = new JMenuItem("Previous");
        viewMenu.add(nextItem);
        viewMenu.add(previousItem);


        JLabel label = new JLabel("Stuff goes here eventually");
        // Each frame has a JPanel called its contentPane—this is where the window contents
        // go. Get the content frame from the panel, and add the label as its child
        frame.getContentPane().add(label);

        // pack() causes the size of the frame to be set just large enough to contain its
        // children; setVisible(true) puts it on the screen
        frame.pack();
        frame.setSize(700,400);
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