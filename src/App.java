import javax.swing.*;
import java.awt.event.*;
import javax.swing.filechooser.*;

public class App extends JFrame implements ActionListener {
    // Jlabel to show the files user selects
    static JLabel l;
 
    // a default constructor
    App()
    {
    }
 
    public static void main(String args[])
    {
        // frame to contains GUI elements
        JFrame f = new JFrame("JFLAP a Texto");
 
        // set the size of the frame
        f.setSize(300, 100);
 
        // set the frame's visibility
        f.setVisible(true);
 
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        // button to open save dialog
        JButton buttonConvert = new JButton("Convertir");
        JButton buttonInfo = new JButton("INFO");
 
        // make an object of the class filechooser
        App app = new App();
 
        // set the label to its initial value
        l = new JLabel("Conversor de archivos JFLAP a archivos");

        // add action listener to the button to capture user
        // response on buttons
        buttonConvert.addActionListener(app);
        buttonInfo.addActionListener(app);
 
        // make a panel to add the buttons and labels
        JPanel p = new JPanel();
 
        // add panel to the frame
        p.add(l);

        // add buttons to the frame
        p.add(buttonConvert);
        p.add(buttonInfo);
 
        f.add(p);
 
        f.setVisible(true);
    }
    public void actionPerformed(ActionEvent evt)
    {
        // if the user presses the save button show the save dialog
        String com = evt.getActionCommand();
 
        if (com.equals("Convertir")) {
            // create an object of JFileChooser class
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
 
            // restrict the user to select files of all types
            j.setAcceptAllFileFilterUsed(false);

            // set the selection mode to directories only
            j.setFileSelectionMode(JFileChooser.FILES_ONLY);

            // only allow files of .txt extension
            FileNameExtensionFilter restrict = new FileNameExtensionFilter("Archivos JFLAP", "jff");
            j.addChoosableFileFilter(restrict);
 
            // invoke the showsOpenDialog function to show the save dialog
            int r = j.showOpenDialog(null);
 
            if (r == JFileChooser.APPROVE_OPTION) {

                try {
                    if(JFLAPtoTuple.convert(j.getSelectedFile().getAbsolutePath())) {
                        JOptionPane.showMessageDialog(j, "Se ha realizado la conversion exitosamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(j, "No se pudo realizar la conversion.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(j, "No se pudo realizar la conversion.\nExcepcion: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "1. La numeracion de los estados en el diagrama debe de continuar desde 0 hasta n.\n2. No debe contener un estado trampa (el conversor lo añadira por ti).\n3. El archivo texto sera guardado en el mismo directorio donde se encontraba el archivo JFLAP.", "Informacion del programa", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
