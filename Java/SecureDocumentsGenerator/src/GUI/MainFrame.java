package GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
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
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public MainFrame(String title) throws HeadlessException
    {
        super(title);
        
        this.initComponents();
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
    
    //<editor-fold defaultstate="collapsed" desc="Private variables">
    
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
}
