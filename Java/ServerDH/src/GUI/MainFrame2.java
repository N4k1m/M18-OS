package GUI;

import Threads.ThreadServer;
import Utils.PropertyLoader;
import Utils.TextAreaOutputStream;
import java.awt.Color;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Nakim
 */
public class MainFrame2 extends javax.swing.JFrame
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public MainFrame2()
    {
        this.initComponents();

        // Redirect the system output to a TextArea
        TextAreaOutputStream toas = TextAreaOutputStream.getInstance(
            this.textAreaOutput);

        this.loadDefaultSettings();
        this.threadServer = null;

        this.showStatus();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void loadDefaultSettings()
    {
        // Load properties file
        try
        {
            String path = System.getProperty("user.dir");
            path += System.getProperty("file.separator") + "src"
                 + System.getProperty("file.separator")  + "GUI"
                 + System.getProperty("file.separator")  + "config.properties";

            Properties prop = PropertyLoader.load(path);

            // Set the default port servers
            this.spinnerPort.setValue(
                new Integer(prop.getProperty(
                    "port_server", DEFAULT_PORT)));

            System.out.println("[ OK ] Default settings loaded");
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    private void showStatus()
    {
        this.isRunning = this.threadServer != null &&
                         this.threadServer.isAlive();

        this.spinnerPort.setEnabled(!this.isRunning);

        if (this.isRunning)
        {
            this.labelStatus.setForeground(Color.GREEN);
            this.labelStatus.setText("Server is running");
            this.buttonStartStop.setText("Stop");
        }
        else
        {
            this.labelStatus.setForeground(Color.RED);
            this.labelStatus.setText("Server stopped");
            this.buttonStartStop.setText("Start");
        }
    }

    private void startServer()
    {
        if (this.threadServer != null)
            this.stopServer();

        // Start thread
        int port = (int)this.spinnerPort.getValue();
        this.threadServer = new ThreadServer(port);
        this.threadServer.start();
        this.showStatus();
    }

    private void stopServer()
    {
        if (this.threadServer == null)
            return;

        try
        {
            // Stop thread
            this.threadServer.requestStop();
            this.threadServer.join();
            this.threadServer = null;
        }
        catch (IOException | InterruptedException ex)
        {
            System.err.println(ex);
        }
        finally
        {
            this.showStatus();
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        buttonClear = new javax.swing.JButton();
        panelHeader = new javax.swing.JPanel();
        buttonStartStop = new javax.swing.JButton();
        panelHeaderBody = new javax.swing.JPanel();
        labelStatusInfo = new javax.swing.JLabel();
        labelStatus = new javax.swing.JLabel();
        labelPort = new javax.swing.JLabel();
        spinnerPort = new javax.swing.JSpinner();
        scrollPane = new javax.swing.JScrollPane();
        textAreaOutput = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server Diffie Hellman");

        buttonClear.setText("Clear");
        buttonClear.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonClearActionPerformed(evt);
            }
        });
        getContentPane().add(buttonClear, java.awt.BorderLayout.PAGE_END);

        panelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelHeader.setLayout(new java.awt.BorderLayout());

        buttonStartStop.setText("<state>");
        buttonStartStop.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonStartStopActionPerformed(evt);
            }
        });
        panelHeader.add(buttonStartStop, java.awt.BorderLayout.LINE_END);

        panelHeaderBody.setLayout(new java.awt.GridLayout(2, 2, 0, 5));

        labelStatusInfo.setText("Status:");
        panelHeaderBody.add(labelStatusInfo);

        labelStatus.setText("<status>");
        panelHeaderBody.add(labelStatus);

        labelPort.setText("Listening port :");
        panelHeaderBody.add(labelPort);

        spinnerPort.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        panelHeaderBody.add(spinnerPort);

        panelHeader.add(panelHeaderBody, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelHeader, java.awt.BorderLayout.PAGE_START);

        scrollPane.setViewportView(textAreaOutput);

        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //<editor-fold defaultstate="collapsed" desc="Events management">
    private void buttonStartStopActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonStartStopActionPerformed
    {//GEN-HEADEREND:event_buttonStartStopActionPerformed
        if (this.isRunning)
            this.stopServer();
        else
            this.startServer();
    }//GEN-LAST:event_buttonStartStopActionPerformed

    private void buttonClearActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonClearActionPerformed
    {//GEN-HEADEREND:event_buttonClearActionPerformed
        this.textAreaOutput.setText(null);
    }//GEN-LAST:event_buttonClearActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Main">
    public static void main(String args[])
    {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException |
            javax.swing.UnsupportedLookAndFeelException ex)
        {
            System.err.println(ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() ->
        {
            new MainFrame2().setVisible(true);
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Generated Widgets">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonStartStop;
    private javax.swing.JLabel labelPort;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel labelStatusInfo;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelHeaderBody;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JSpinner spinnerPort;
    private javax.swing.JTextArea textAreaOutput;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private variables">
    private boolean isRunning;
    private ThreadServer threadServer;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Static variables">
    private static final String DEFAULT_PORT;

    static
    {
        DEFAULT_PORT = "40000";
    }
    // </editor-fold>
}
