package GUI;

import DOCSAP.DOCSAPRequest;
import Utils.MessageBoxes;
import Utils.PropertyLoader;
import Utils.TextAreaOutputStream;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

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
        TextAreaOutputStream toas =
            TextAreaOutputStream.getInstance(this.textAreaOutput);

        this.loadDefaultSettings();
        this.sock = null;

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

            // Set the default ip server
            this.textFieldIPServer.setText(
                prop.getProperty("ip_server", DEFAULT_IP));
            // Set the default port server
            this.spinnerPortServer.setValue(
                new Integer(prop.getProperty("port_server", DEFAULT_PORT)));
            // Set default login
            this.textFieldLogin.setText(prop.getProperty("login"));
            // Set default password
            this.passwordField.setText(prop.getProperty("password"));

            System.out.println("[ OK ] Default settings loaded");
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    private void showStatus()
    {
        this.isConnected = this.sock != null && this.sock.isConnected();

        // Enable or disable widgets

        // Enable or disable all action buttons
        for (Component component : this.panelBodyLeft.getComponents())
            component.setEnabled(this.isConnected);

        this.table.setEnabled(this.isConnected);
        this.labelIPServer.setEnabled(!this.isConnected);
        this.textFieldIPServer.setEnabled(!this.isConnected);
        this.labelPortServer.setEnabled(!this.isConnected);
        this.spinnerPortServer.setEnabled(!this.isConnected);
        this.labelLogin.setEnabled(!this.isConnected);
        this.textFieldLogin.setEnabled(!this.isConnected);
        this.labelPassword.setEnabled(!this.isConnected);
        this.passwordField.setEnabled(!this.isConnected);

        if (this.isConnected)
        {
            this.labelStatus.setForeground(Color.GREEN);
            this.labelStatus.setText("Connected");
            this.buttonConnect.setText("Disconnect");
        }
        else
        {
            this.labelStatus.setForeground(Color.RED);
            this.labelStatus.setText("Disconnected");
            this.buttonConnect.setText("Connect");
        }
    }

    private void connecteToServer()
    {
        // Disconnect from server if connected
        if (this.sock != null)
            this.disconnectFromServer();

        int port = (int)this.spinnerPortServer.getValue();
        String ip = this.textFieldIPServer.getText();
        System.out.println("[ OK ] Start a new connection to server "
            + ip + ":" + port);

        try
        {
            this.sock = new Socket(ip, port);

            // TODO : login procedure
            DOCSAPRequest request = new DOCSAPRequest(DOCSAPRequest.LOGINA);
            request.addArg("xavier");
            request.addArg("m910719X");

            request.send(this.sock);
        }
        catch (UnknownHostException ex)
        {
            System.out.println("[FAIL] Host unreachable. Invalid IP " + ip);
        }
        catch (IOException ex)
        {
            System.out.println("[FAIL] Failed to connect");
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] Login failed : " + ex.getMessage());
            MessageBoxes.ShowError(ex.getMessage(), "Failed to connedct");
            this.disconnectFromServer();
        }
        finally
        {
            this.showStatus();
        }
    }

    private void disconnectFromServer()
    {
        if (this.sock == null)
        {
            System.out.println("[FAIL] You are not connected to the server");
            return;
        }

        try
        {
            // Send QUIT request
            DOCSAPRequest.quickSend(DOCSAPRequest.QUIT, this.sock);

            this.sock.close();
            this.sock = null;
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] An error occurred disconnecting the "
                + "system from the server : " + ex);
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
        java.awt.GridBagConstraints gridBagConstraints;

        panelHeader = new javax.swing.JPanel();
        labelStatusInfo = new javax.swing.JLabel();
        labelStatus = new javax.swing.JLabel();
        buttonConnect = new javax.swing.JButton();
        labelIPServer = new javax.swing.JLabel();
        textFieldIPServer = new javax.swing.JTextField();
        labelPortServer = new javax.swing.JLabel();
        spinnerPortServer = new javax.swing.JSpinner();
        labelLogin = new javax.swing.JLabel();
        textFieldLogin = new javax.swing.JTextField();
        labelPassword = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        splitPane = new javax.swing.JSplitPane();
        scrollPaneTextArea = new javax.swing.JScrollPane();
        textAreaOutput = new javax.swing.JTextArea();
        panelBody = new javax.swing.JPanel();
        panelBodyLeft = new javax.swing.JPanel();
        buttonListClients = new javax.swing.JButton();
        buttonPause = new javax.swing.JButton();
        buttonResume = new javax.swing.JButton();
        buttonStop = new javax.swing.JButton();
        scrollPaneTable = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        this.table.getTableHeader().setReorderingAllowed(false);
        buttonClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(500, 400));

        panelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        java.awt.GridBagLayout panelHeaderLayout1 = new java.awt.GridBagLayout();
        panelHeaderLayout1.columnWidths = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0};
        panelHeaderLayout1.rowHeights = new int[] {0, 3, 0, 3, 0};
        panelHeader.setLayout(panelHeaderLayout1);

        labelStatusInfo.setText("Status :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelHeader.add(labelStatusInfo, gridBagConstraints);

        labelStatus.setText("<status>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        panelHeader.add(labelStatus, gridBagConstraints);

        buttonConnect.setText("<state>");
        buttonConnect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonConnectActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panelHeader.add(buttonConnect, gridBagConstraints);

        labelIPServer.setText("IP address :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelHeader.add(labelIPServer, gridBagConstraints);

        textFieldIPServer.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelHeader.add(textFieldIPServer, gridBagConstraints);

        labelPortServer.setText("Port :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelHeader.add(labelPortServer, gridBagConstraints);

        spinnerPortServer.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        spinnerPortServer.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelHeader.add(spinnerPortServer, gridBagConstraints);

        labelLogin.setText("Login :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelHeader.add(labelLogin, gridBagConstraints);

        textFieldLogin.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelHeader.add(textFieldLogin, gridBagConstraints);

        labelPassword.setText("Password : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelHeader.add(labelPassword, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelHeader.add(passwordField, gridBagConstraints);

        getContentPane().add(panelHeader, java.awt.BorderLayout.PAGE_START);

        splitPane.setDividerLocation(150);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setOneTouchExpandable(true);

        textAreaOutput.setEditable(false);
        scrollPaneTextArea.setViewportView(textAreaOutput);

        splitPane.setBottomComponent(scrollPaneTextArea);

        panelBody.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelBody.setLayout(new java.awt.BorderLayout());

        java.awt.GridBagLayout panelBodyLeftLayout = new java.awt.GridBagLayout();
        panelBodyLeftLayout.columnWidths = new int[] {0};
        panelBodyLeftLayout.rowHeights = new int[] {0, 3, 0, 3, 0, 3, 0};
        panelBodyLeft.setLayout(panelBodyLeftLayout);

        buttonListClients.setText("List clients");
        buttonListClients.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonListClientsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelBodyLeft.add(buttonListClients, gridBagConstraints);

        buttonPause.setText("Pause");
        buttonPause.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonPauseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelBodyLeft.add(buttonPause, gridBagConstraints);

        buttonResume.setText("Resume");
        buttonResume.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonResumeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelBodyLeft.add(buttonResume, gridBagConstraints);

        buttonStop.setText("Stop");
        buttonStop.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonStopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelBodyLeft.add(buttonStop, gridBagConstraints);

        panelBody.add(panelBodyLeft, java.awt.BorderLayout.LINE_START);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {
                "IPv4 address"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean []
            {
                false
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit [columnIndex];
            }
        });
        scrollPaneTable.setViewportView(table);

        panelBody.add(scrollPaneTable, java.awt.BorderLayout.CENTER);

        splitPane.setLeftComponent(panelBody);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        buttonClear.setText("Clear");
        buttonClear.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonClearActionPerformed(evt);
            }
        });
        getContentPane().add(buttonClear, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //<editor-fold defaultstate="collapsed" desc="Events management">
    private void buttonConnectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonConnectActionPerformed
    {//GEN-HEADEREND:event_buttonConnectActionPerformed
        if (this.isConnected)
            this.disconnectFromServer();
        else
            this.connecteToServer();
    }//GEN-LAST:event_buttonConnectActionPerformed

    private void buttonClearActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonClearActionPerformed
    {//GEN-HEADEREND:event_buttonClearActionPerformed
        this.textAreaOutput.setText(null);
    }//GEN-LAST:event_buttonClearActionPerformed

    private void buttonListClientsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonListClientsActionPerformed
    {//GEN-HEADEREND:event_buttonListClientsActionPerformed
        try
        {
            if (!this.isConnected)
                throw new Exception("you must be logged in");

            DOCSAPRequest query = new DOCSAPRequest(DOCSAPRequest.LCLIENTS);
            DOCSAPRequest reply = query.sendAndRecv(this.sock);

            // If server closed the connection
            if (reply.is(DOCSAPRequest.NO_COMMAND) || reply.is(DOCSAPRequest.SOCK_ERROR))
                throw new Exception("Disconnected from server");

            // If LCLIENTS failed
            if (reply.is(DOCSAPRequest.FAIL))
                throw new Exception(reply.getArg(0)); // arg 0 = cause

            // Invalid reply
            if (!reply.is(DOCSAPRequest.ACK))
                throw new Exception("Invalid reply");

            // Remove all previous clients from table
            DefaultTableModel IPTableModel = (DefaultTableModel)this.table.getModel();
            IPTableModel.getDataVector().removeAllElements();

            // If there is no client connected
            if (reply.getArgsCount() <= 0)
                throw new Exception("No client connected");

            System.out.println("[ OK ] " + reply.getArgsCount() + " clients connected");

            // Add each client IPv4 address in table
            for(int i = 0; i < reply.getArgsCount(); ++i)
            {
                ArrayList<String> rowClient = new ArrayList<>();
                rowClient.add("Client " + i);
                rowClient.add(reply.getArg(i));
                IPTableModel.addRow(rowClient.toArray());
            }
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] Unable to list clients : " + ex.getMessage());
            MessageBoxes.ShowError(ex.getMessage(), "Unable to list clients");
        }
    }//GEN-LAST:event_buttonListClientsActionPerformed

    private void buttonPauseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonPauseActionPerformed
    {//GEN-HEADEREND:event_buttonPauseActionPerformed
        try
        {
            if (!this.isConnected)
                throw new Exception("you must be logged in");

            DOCSAPRequest query = new DOCSAPRequest(DOCSAPRequest.PAUSE);
            DOCSAPRequest reply = query.sendAndRecv(this.sock);

            // If server closed the connection
            if (reply.is(DOCSAPRequest.NO_COMMAND) || reply.is(DOCSAPRequest.SOCK_ERROR))
                throw new Exception("Disconnected from server");

            // If PAUSE failed
            if (reply.is(DOCSAPRequest.FAIL))
                throw new Exception(reply.getArg(0)); // arg 0 = cause

            // Invalid reply
            if (!reply.is(DOCSAPRequest.ACK))
                throw new Exception("Invalid reply");

            System.out.println("[ OK ] Server suspended");
            MessageBoxes.ShowInfo("Server suspended", "Server suspended");
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] Unable to suspend server : " + ex.getMessage());
            MessageBoxes.ShowError(ex.getMessage(), "Unable to suspend server");
        }
    }//GEN-LAST:event_buttonPauseActionPerformed

    private void buttonResumeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonResumeActionPerformed
    {//GEN-HEADEREND:event_buttonResumeActionPerformed
        try
        {
            if (!this.isConnected)
                throw new Exception("you must be logged in");

            DOCSAPRequest query = new DOCSAPRequest(DOCSAPRequest.RESUME);
            DOCSAPRequest reply = query.sendAndRecv(this.sock);

            // If server closed the connection
            if (reply.is(DOCSAPRequest.NO_COMMAND) || reply.is(DOCSAPRequest.SOCK_ERROR))
                throw new Exception("Disconnected from server");

            // If RESUME failed
            if (reply.is(DOCSAPRequest.FAIL))
                throw new Exception(reply.getArg(0)); // arg 0 = cause

            // Invalid reply
            if (!reply.is(DOCSAPRequest.ACK))
                throw new Exception("Invalid reply");

            System.out.println("[ OK ] Server resumed");
            MessageBoxes.ShowInfo("Server resumed", "Server resumed");
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] Unable to resume server : " + ex.getMessage());
            MessageBoxes.ShowError(ex.getMessage(), "Unable to resume server");
        }
    }//GEN-LAST:event_buttonResumeActionPerformed

    private void buttonStopActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonStopActionPerformed
    {//GEN-HEADEREND:event_buttonStopActionPerformed
        try
        {
            String delay;

            PanelNumberOfSeconds pnos = new PanelNumberOfSeconds();
            int option = JOptionPane.showOptionDialog(
                null, pnos, "Number of second(s) before shutdown",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, null, null);

            if (option == JOptionPane.CANCEL_OPTION)
            {
                System.out.println("[FAIL] Stop action aborted");
                return;
            }

            if (option != JOptionPane.OK_OPTION)
                throw new Exception("Invalid action");

            delay = String.valueOf(pnos.getDelay());

            DOCSAPRequest query = new DOCSAPRequest(DOCSAPRequest.STOP);
            query.addArg(delay);

            DOCSAPRequest reply = query.sendAndRecv(this.sock);

            // If server closed the connection
            if (reply.is(DOCSAPRequest.NO_COMMAND) || reply.is(DOCSAPRequest.SOCK_ERROR))
                throw new Exception("Disconnected from server");

            // If STOP failed
            if (reply.is(DOCSAPRequest.FAIL))
                throw new Exception(reply.getArg(0)); // arg 0 = cause

            // Invalid reply
            if (!reply.is(DOCSAPRequest.ACK))
                throw new Exception("Invalid reply");

            System.out.println("[ OK ] Server will stop in " + delay + " seconds");
            MessageBoxes.ShowInfo("Server will stop in " + delay + " seconds", "Server will stop");
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] Unable to stop server : " + ex.getMessage());
            MessageBoxes.ShowError(ex.getMessage(), "Unable to stop server");
        }
    }//GEN-LAST:event_buttonStopActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Widgets">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonConnect;
    private javax.swing.JButton buttonListClients;
    private javax.swing.JButton buttonPause;
    private javax.swing.JButton buttonResume;
    private javax.swing.JButton buttonStop;
    private javax.swing.JLabel labelIPServer;
    private javax.swing.JLabel labelLogin;
    private javax.swing.JLabel labelPassword;
    private javax.swing.JLabel labelPortServer;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel labelStatusInfo;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelBodyLeft;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JScrollPane scrollPaneTable;
    private javax.swing.JScrollPane scrollPaneTextArea;
    private javax.swing.JSpinner spinnerPortServer;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTable table;
    private javax.swing.JTextArea textAreaOutput;
    private javax.swing.JTextField textFieldIPServer;
    private javax.swing.JTextField textFieldLogin;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private Socket sock;
    private boolean isConnected;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Static variables">
    private static final String DEFAULT_IP;
    private static final String DEFAULT_PORT;
    private static final String keysFolderPath;

    static
    {
        DEFAULT_IP   = "127.0.0.1";
        DEFAULT_PORT = "50000";
        keysFolderPath = "KEYS" + System.getProperty("file.separator");
    }
    //</editor-fold>
}
