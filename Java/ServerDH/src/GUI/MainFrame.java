package GUI;

import Threads.ThreadServerV1;
import Utils.PropertyLoader;
import Utils.TextAreaOutputStream;
import java.awt.Color;
import java.io.IOException;
import java.util.Properties;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Nakim
 */
public class MainFrame extends javax.swing.JFrame
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public MainFrame()
    {
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

            // Set the default port servers
            this.spinnerPortV1.setValue(
                new Integer(this.prop.getProperty(
                    "port_server_v1", DEFAULT_PORT_V1)));
            this.spinnerPortV2.setValue(
                new Integer(this.prop.getProperty(
                    "port_server_v2", DEFAULT_PORT_V2)));

            System.out.println("[ OK ] Configuration settings : loaded");
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
        finally
        {
            this.showStatusV1();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void startServerV1()
    {
        if (this.threadServerV1 != null)
            this.stopServerV1();

        // Start thread
        int port = (int)this.spinnerPortV1.getValue();
        this.threadServerV1 = new ThreadServerV1(port);
        this.threadServerV1.start();
        this.showStatusV1();
    }

    private void stopServerV1()
    {
        if (this.threadServerV1 == null)
            return;

        try
        {
            // Stop thread
            this.threadServerV1.requestStop();
            this.threadServerV1.join();
            this.threadServerV1 = null;
        }
        catch (IOException | InterruptedException ex)
        {
            System.err.println(ex);
        }
        finally
        {
            this.showStatusV1();
        }
    }

    private void showStatusV1()
    {
        this.isRunningV1 = this.threadServerV1 != null &&
                           this.threadServerV1.isAlive();

        this.spinnerPortV1.setEnabled(!this.isRunningV1);

        if (this.isRunningV1)
        {
            this.labelStatusV1.setForeground(Color.GREEN);
            this.labelStatusV1.setText("Server is running");
            this.buttonStartStopV1.setText("Stop");
        }
        else
        {
            this.labelStatusV1.setForeground(Color.RED);
            this.labelStatusV1.setText("Server stopped");
            this.buttonStartStopV1.setText("Start");
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonClear = new javax.swing.JButton();
        splitPane = new javax.swing.JSplitPane();
        scrollPane = new javax.swing.JScrollPane();
        textAreaOutput = new javax.swing.JTextArea();
        tabbedPane = new javax.swing.JTabbedPane();
        panelV1 = new javax.swing.JPanel();
        labelStatusInfoV1 = new javax.swing.JLabel();
        labelStatusV1 = new javax.swing.JLabel();
        labelPortV1 = new javax.swing.JLabel();
        spinnerPortV1 = new javax.swing.JSpinner();
        buttonStartStopV1 = new javax.swing.JButton();
        panelV2 = new javax.swing.JPanel();
        labelStatusInfoV2 = new javax.swing.JLabel();
        labelStatusV2 = new javax.swing.JLabel();
        labelPortV2 = new javax.swing.JLabel();
        spinnerPortV2 = new javax.swing.JSpinner();
        buttonStartStopV2 = new javax.swing.JButton();

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

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        textAreaOutput.setColumns(20);
        textAreaOutput.setRows(5);
        scrollPane.setViewportView(textAreaOutput);

        splitPane.setBottomComponent(scrollPane);

        panelV1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        java.awt.GridBagLayout panelV1Layout = new java.awt.GridBagLayout();
        panelV1Layout.columnWidths = new int[] {0, 5, 0, 5, 0};
        panelV1Layout.rowHeights = new int[] {0, 5, 0};
        panelV1.setLayout(panelV1Layout);

        labelStatusInfoV1.setText("Status :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        panelV1.add(labelStatusInfoV1, gridBagConstraints);

        labelStatusV1.setText("<status>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        panelV1.add(labelStatusV1, gridBagConstraints);

        labelPortV1.setText("Listening port :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelV1.add(labelPortV1, gridBagConstraints);

        spinnerPortV1.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelV1.add(spinnerPortV1, gridBagConstraints);

        buttonStartStopV1.setText("Start");
        buttonStartStopV1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonStartStopV1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        panelV1.add(buttonStartStopV1, gridBagConstraints);

        tabbedPane.addTab("Version 1 - Secret word", panelV1);

        panelV2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        java.awt.GridBagLayout panelV2Layout = new java.awt.GridBagLayout();
        panelV2Layout.columnWidths = new int[] {0, 5, 0, 5, 0};
        panelV2Layout.rowHeights = new int[] {0, 5, 0};
        panelV2.setLayout(panelV2Layout);

        labelStatusInfoV2.setText("Status :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        panelV2.add(labelStatusInfoV2, gridBagConstraints);

        labelStatusV2.setText("<status>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        panelV2.add(labelStatusV2, gridBagConstraints);

        labelPortV2.setText("Listening port :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelV2.add(labelPortV2, gridBagConstraints);

        spinnerPortV2.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelV2.add(spinnerPortV2, gridBagConstraints);

        buttonStartStopV2.setText("Start");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        panelV2.add(buttonStartStopV2, gridBagConstraints);

        tabbedPane.addTab("Version 2 - Diffie Hellman", panelV2);

        splitPane.setLeftComponent(tabbedPane);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //<editor-fold defaultstate="collapsed" desc="Events management">
    private void buttonStartStopV1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonStartStopV1ActionPerformed
    {//GEN-HEADEREND:event_buttonStartStopV1ActionPerformed
        if (this.isRunningV1)
            this.stopServerV1();
        else
            this.startServerV1();
    }//GEN-LAST:event_buttonStartStopV1ActionPerformed

    private void buttonClearActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonClearActionPerformed
    {//GEN-HEADEREND:event_buttonClearActionPerformed
        this.textAreaOutput.setText(null);
    }//GEN-LAST:event_buttonClearActionPerformed
    // </editor-fold>

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
                IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            System.err.println(ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() ->
        {
            new MainFrame().setVisible(true);
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Generated Widgets">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonStartStopV1;
    private javax.swing.JButton buttonStartStopV2;
    private javax.swing.JLabel labelPortV1;
    private javax.swing.JLabel labelPortV2;
    private javax.swing.JLabel labelStatusInfoV1;
    private javax.swing.JLabel labelStatusInfoV2;
    private javax.swing.JLabel labelStatusV1;
    private javax.swing.JLabel labelStatusV2;
    private javax.swing.JPanel panelV1;
    private javax.swing.JPanel panelV2;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JSpinner spinnerPortV1;
    private javax.swing.JSpinner spinnerPortV2;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextArea textAreaOutput;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private variables">
    private Properties prop;
    private boolean isRunningV1;
    private ThreadServerV1 threadServerV1;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Static variables">
    private static final String DEFAULT_PORT_V1;
    private static final String DEFAULT_PORT_V2;

    static
    {
        DEFAULT_PORT_V1 = "40000";
        DEFAULT_PORT_V2 = "40001";
    }
    // </editor-fold>
}
