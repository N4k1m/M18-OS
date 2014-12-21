package GUI;

import Threads.ThServer;
import Utils.PropertyLoader;
import Utils.TextAreaOutputStream;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author nakim
 */
public class MainFrame extends JFrame
                       implements ActionListener
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public MainFrame(String title) throws HeadlessException
    {
        super(title);
        this.initComponents();
        
        // Redirect the system output to a TextArea
        TextAreaOutputStream toas = TextAreaOutputStream.getInstance(
            this.textAreaOutput);
        
        // Load properties file        
        try
        {
            String path = System.getProperty("user.dir");
            path += System.getProperty("file.separator")
                    + "src"
                    + System.getProperty("file.separator")
                    + "GUI"
                    + System.getProperty("file.separator")
                    + "config.properties";
        
            this.prop = PropertyLoader.load(path);
            
            // Set the default port server
            this.spinnerPort.setValue(
                new Integer(this.prop.getProperty("port_server", DEFAULT_PORT)));
            
            System.out.println("[ OK ] Parametres de configuration charges");
            
            this.isRunning = false;
            System.out.println("[ OK ] Serveur a l'arret");
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }
    
    private void initComponents()
    {
        // Configure main frame
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(10, 10));
        
        Container mainFrameContainer = this.getContentPane();
        
        // Header widgets
        this.labelProt = new JLabel("Listening port : ");
        this.labelProt.setHorizontalAlignment(JLabel.CENTER);
        
        this.spinnerModel = new SpinnerNumberModel();
        this.spinnerModel.setMinimum(0);
        this.spinnerModel.setStepSize(1);
        
        this.spinnerPort = new JSpinner(this.spinnerModel);
        this.spinnerPort.setPreferredSize(new Dimension(100, 28));
        
        this.buttonStartStop = new JButton("Start");
        this.buttonStartStop.addActionListener(this);
        
        // Header panel
        this.panelHeader = new JPanel();
        this.panelHeader.add(this.labelProt);
        this.panelHeader.add(this.spinnerPort);
        this.panelHeader.add(this.buttonStartStop);
        
        // Body
        this.textAreaOutput = new JTextArea();
        this.scrollPaneBody = new JScrollPane(this.textAreaOutput);
        
        // Footer
        this.buttonClear = new JButton("Clear");
        this.buttonClear.addActionListener(this);
        
        // Populate main frame
        mainFrameContainer.add(this.panelHeader, BorderLayout.PAGE_START);
        mainFrameContainer.add(this.scrollPaneBody, BorderLayout.CENTER);
        mainFrameContainer.add(this.buttonClear, BorderLayout.PAGE_END);
        this.setSize(new Dimension(400, 450));
        //pack();
        
        // Center frame
        this.setLocationRelativeTo(null);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void startServer()
    {
        // Start thread
        int port = (int)this.spinnerPort.getValue();
        this.thServer = new ThServer(this, port);
        this.thServer.start();
        
        this.spinnerPort.setEnabled(false);
        this.buttonStartStop.setText("Stop");
        this.isRunning = true;
    }
    
    private void stopServer()
    {
        try
        {
            // Stop thread
            this.thServer.requestStop();
            this.thServer.join();
        }
        catch (IOException | InterruptedException ex)
        {
            System.err.println(ex);
        }
        
        this.spinnerPort.setEnabled(true);
        this.buttonStartStop.setText("Start");
        this.isRunning = false;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Events management">
    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (actionEvent.getSource() == this.buttonStartStop)
        {
            if (this.isRunning)
                this.stopServer();
            else
                this.startServer();
        }
        else if (actionEvent.getSource() == this.buttonClear)
        {
            this.textAreaOutput.setText(null);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private child Widgets">
    
    // Header Widgets
    private JLabel labelProt;
    private JSpinner spinnerPort;
    private SpinnerNumberModel spinnerModel;
    private JButton buttonStartStop;
    private JPanel panelHeader;
    
    // Body Widgets
    private JScrollPane scrollPaneBody;
    private JTextArea textAreaOutput;
    
    // Footer Widget
    private JButton buttonClear;
        
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Private variables ">
    private Properties prop;
    private boolean isRunning;
    private ThServer thServer;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Static variable ">
    private static final String DEFAULT_PORT;
    
    static
    {
        DEFAULT_PORT = "40000";
    }
    // </editor-fold>
}
