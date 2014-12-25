package GUI;

import Utils.MessageBoxes;
import Utils.PropertyLoader;
import Utils.Request;
import Utils.SymmetricCrypter;
import Utils.TextAreaOutputStream;
import java.awt.Color;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

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
        this.createModels();
        this.createCrypter();

        // Redirect the system output to a TextArea
        TextAreaOutputStream toas = TextAreaOutputStream.getInstance(
            this.textAreaOutput);

        this.loadDefaultSettings();
        this.openBible();

        this.sock = null;
        this.isConnected = false;

        this.showStatus();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void createModels()
    {
        // Load properties file
        try
        {
            String path = System.getProperty("user.dir");
            path += System.getProperty("file.separator")
                    + "src"
                    + System.getProperty("file.separator")
                    + "GUI"
                    + System.getProperty("file.separator")
                    + "interpreter.properties";

            this.interpreterProperties = PropertyLoader.load(path);

            // Get all messages
            this.messagesModel = new DefaultComboBoxModel<>();
            this.interpreterProperties.keySet().stream().forEach((message) ->
            {
                this.messagesModel.addElement((String)message);
            });

            this.comboBoxMessages.setModel(this.messagesModel);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    private void createCrypter()
    {
        try
        {
            this.symmetricCrypter = new SymmetricCrypter(
                algorithm + "/" + cipherMode + "/" + padding);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException ex)
        {
            this.symmetricCrypter = null;
            System.out.println("[FAIL] Unable to create ciphers : "
                + ex.getMessage());
        }
    }

    private void loadDefaultSettings()
    {
        // Load properties file
        try
        {
            String path = System.getProperty("user.dir");
            path += System.getProperty("file.separator") + "src"
                 + System.getProperty("file.separator")  + "GUI"
                 + System.getProperty("file.separator")  + "config.properties";

            Properties propConfig = PropertyLoader.load(path);

            // Set the default server ip and port
            this.textFieldIP.setText(propConfig.getProperty(
                "ip_server", DEFAULT_IP));
            this.spinnerPort.setValue(new Integer(propConfig.getProperty(
                "port_server", DEFAULT_PORT)));

            System.out.println("[ OK ] Default settings loaded");
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    private void openBible()
    {
        // Load properties file
        try
        {
            String path = System.getProperty("user.dir");
            path += System.getProperty("file.separator")
                    + "src"
                    + System.getProperty("file.separator")
                    + "GUI"
                    + System.getProperty("file.separator")
                    + "bible.properties";

            this.bibleProperties = PropertyLoader.load(path);
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
        this.spinnerPort.setEnabled(!this.isConnected);
        this.textFieldIP.setEnabled(!this.isConnected);
        this.comboBoxMessages.setEnabled(this.isConnected);
        this.buttonGenerateNewSecretKey.setEnabled(this.isConnected);
        this.buttonSendMessage.setEnabled(this.isConnected);

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

    private void connectToServer()
    {
        if (this.sock != null)
            this.disconnectFromServer();

        int port = (int)this.spinnerPort.getValue();
        String ip = this.textFieldIP.getText();
        System.out.println("[ OK ] Start a new connection to server "
            + ip + ":" + port);

        try
        {
            this.sock = new Socket(ip, port);
        }
        catch (UnknownHostException ex)
        {
            System.out.println("[FAIL] Host unreachable. Invalid IP " + ip);
        }
        catch (IOException ex)
        {
            System.out.println("[FAIL] Failed to connect");
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
            this.sock.close();
            this.sock = null;
            this.symmetricCrypter.invalidate();
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

    private void generateSecretKeyV1()
    {
        System.out.println("[ V1 ] Generate new secret key");

        try
        {
            // check if client is connected
            if (this.sock == null || !this.sock.isConnected())
                throw new Exception("You are disconnected from server");

            // Check if crypter exists
            if (this.symmetricCrypter == null)
                throw new Exception("No crypter object available");

            /* Get bible line number before sending a query to be shure that
             * the line number is the same for the server and the client if
             * the network communication is too slow */
            String minute = new SimpleDateFormat("mm").format(new Date());
            int bibleLineNumber = Integer.parseInt(minute) % 10;

            // Send a GENERATE_KEY_V1 query
            Request query = new Request("GENERATE_KEY_V1");
            Request reply = query.sendAndRecv(this.sock);

            // If client has been disconnected
            if (reply.is(Request.NO_COMMAND) || reply.is(Request.SOCK_ERROR))
            {
                this.disconnectFromServer();
                throw new Exception("Disconnected from server");
            }

            // If query failed
            if (reply.is("GENERATE_KEY_V1_FAIL"))
                throw new Exception("Server error : " + reply.getStringArg(0));

            // Unknown command
            if (!reply.is("GENERATE_KEY_V1_ACK"))
                throw new Exception("Invalid reply : " + reply.getCommand());

            // Get bible line at "bibleLineNumber"
            String bibleLine = this.bibleProperties.getProperty(
                Integer.toString(bibleLineNumber));
            System.out.println("[ V1 ] bible line " + bibleLineNumber
                + " = " + bibleLine);

            // Generate new secret key
            SecretKey newSecretKey = new SecretKeySpec(
                bibleLine.substring(bibleLineNumber,bibleLineNumber + 8)
                    .getBytes(), algorithm);

            // Initialize crypter
            this.symmetricCrypter.init(newSecretKey);

            System.out.println("[ V1 ] New secret key generated");
        }
        catch (Exception e)
        {
            System.out.println("[ V1 ] " + e.getMessage());
            MessageBoxes.ShowError(e.getMessage(),"Error generating secret key");
        }
    }

    private void generateSecretKeyV2()
    {
        System.out.println("[ V2 ] Generate new secret key");

        try
        {
            // check if client is connected
            if (this.sock == null || !this.sock.isConnected())
                throw new Exception("You are disconnected from server");

            // Check if crypter exists
            if (this.symmetricCrypter == null)
                throw new Exception("No crypter object available");

            // Create the parameter generator for a 1024-bit DH key pair
            AlgorithmParameterGenerator paramGen =
                AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(1024);

            System.out.println("[ V2 ] Generating params");

            // Generate the parameters
            AlgorithmParameters params = paramGen.generateParameters();
            //Specify parameters to use for the algorithm
            DHParameterSpec dhSpec = (DHParameterSpec)params.getParameterSpec(
                DHParameterSpec.class);

            System.out.println("[ V2 ] Params generated");

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
            keyGen.initialize(dhSpec);

            KeyPair keypair = keyGen.generateKeyPair();

            // Send a GENERATE_KEY_V2 query to the server with the public key
            Request query = new Request("GENERATE_KEY_V2");
            query.addArg(keypair.getPublic().getEncoded());

            System.out.println("[ V2 ] Sending public key");
            Request reply = query.sendAndRecv(this.sock);

            // If client has been disconnected
            if (reply.is(Request.NO_COMMAND) || reply.is(Request.SOCK_ERROR))
            {
                this.disconnectFromServer();
                throw new Exception("Disconnected from server");
            }

            // If query failed
            if (reply.is("GENERATE_KEY_V2_FAIL"))
                throw new Exception("Server error : " + reply.getStringArg(0));

            // Unknown command
            if (!reply.is("GENERATE_KEY_V2_ACK"))
                throw new Exception("Invalid reply : " + reply.getCommand());


            // Get server public key
            X509EncodedKeySpec x509KeySpec =
                new X509EncodedKeySpec(reply.getArg(0));
            KeyFactory keyFact = KeyFactory.getInstance("DH");
            PublicKey publicKey = keyFact.generatePublic(x509KeySpec);

            /* Prepare the "secret key generator" with the private key
             * and the server public key */
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(keypair.getPrivate());
            ka.doPhase(publicKey, true);

            // Generate new secret key and initialize crypter
            this.symmetricCrypter.init(ka.generateSecret(algorithm));

            System.out.println("[ V2 ] New secret key generated");
        }
        catch (Exception e)
        {
            System.out.println("[ V2 ] " + e.getMessage());
            MessageBoxes.ShowError(e.getMessage(), "Error generating secret key");
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonClear = new javax.swing.JButton();
        panelHeader = new javax.swing.JPanel();
        buttonConnect = new javax.swing.JButton();
        panelHeaderBody = new javax.swing.JPanel();
        labelStatusInfo = new javax.swing.JLabel();
        labelStatus = new javax.swing.JLabel();
        labelIPServer = new javax.swing.JLabel();
        textFieldIP = new javax.swing.JTextField();
        labelPort = new javax.swing.JLabel();
        spinnerPort = new javax.swing.JSpinner();
        splitPane = new javax.swing.JSplitPane();
        scrollPane = new javax.swing.JScrollPane();
        textAreaOutput = new javax.swing.JTextArea();
        panelBody = new javax.swing.JPanel();
        labelMessage = new javax.swing.JLabel();
        comboBoxMessages = new javax.swing.JComboBox<String>();
        buttonGenerateNewSecretKey = new javax.swing.JButton();
        buttonSendMessage = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client Diffie Hellman");

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

        buttonConnect.setText("<state>");
        buttonConnect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonConnectActionPerformed(evt);
            }
        });
        panelHeader.add(buttonConnect, java.awt.BorderLayout.LINE_END);

        panelHeaderBody.setLayout(new java.awt.GridLayout(3, 2));

        labelStatusInfo.setText("Status :");
        panelHeaderBody.add(labelStatusInfo);

        labelStatus.setText("<status>");
        panelHeaderBody.add(labelStatus);

        labelIPServer.setText("IP address :");
        panelHeaderBody.add(labelIPServer);

        textFieldIP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelHeaderBody.add(textFieldIP);

        labelPort.setText("Port :");
        panelHeaderBody.add(labelPort);

        spinnerPort.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        panelHeaderBody.add(spinnerPort);

        panelHeader.add(panelHeaderBody, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelHeader, java.awt.BorderLayout.PAGE_START);

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        scrollPane.setViewportView(textAreaOutput);

        splitPane.setBottomComponent(scrollPane);

        panelBody.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelBody.setLayout(new java.awt.GridBagLayout());

        labelMessage.setText("Message :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        panelBody.add(labelMessage, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        panelBody.add(comboBoxMessages, gridBagConstraints);

        buttonGenerateNewSecretKey.setText("Generate new secret key");
        buttonGenerateNewSecretKey.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonGenerateNewSecretKeyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelBody.add(buttonGenerateNewSecretKey, gridBagConstraints);

        buttonSendMessage.setText("Send message");
        buttonSendMessage.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonSendMessageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelBody.add(buttonSendMessage, gridBagConstraints);

        splitPane.setLeftComponent(panelBody);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //<editor-fold defaultstate="collapsed" desc="Events management">
    private void buttonConnectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonConnectActionPerformed
    {//GEN-HEADEREND:event_buttonConnectActionPerformed
        if (this.isConnected)
            this.disconnectFromServer();
        else
            this.connectToServer();
    }//GEN-LAST:event_buttonConnectActionPerformed

    private void buttonGenerateNewSecretKeyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonGenerateNewSecretKeyActionPerformed
    {//GEN-HEADEREND:event_buttonGenerateNewSecretKeyActionPerformed
        Object[] options = {"Version 1 - Secret Message",
                            "Version 2 - Difie Hellman",
                            "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
            "Select the key exchange method :",
            "Key exchange choice",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,        // do not use a custom icon
            options,     // the title of buttons
            options[0]); // default button title

        switch(choice)
        {
            case 0:     // Version 1
                this.generateSecretKeyV1();
                break;
            case 1:     // Version 2
                this.generateSecretKeyV2();
                break;
            case 2:     // Cancel or other
            default:
        }
    }//GEN-LAST:event_buttonGenerateNewSecretKeyActionPerformed

    private void buttonSendMessageActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonSendMessageActionPerformed
    {//GEN-HEADEREND:event_buttonSendMessageActionPerformed
        try
        {
            // check if client is connected
            if (this.sock == null || !this.sock.isConnected())
                throw new Exception("You are disconnected from server");

            // Check if crypter exists
            if (this.symmetricCrypter == null)
                throw new Exception("No crypter object available");

            // Check if crypter is valid (has valid secret key)
            if (!this.symmetricCrypter.isValid())
                throw new Exception("A new secret key must be generated");

            // Get coded message
            String codedMessage = (String)comboBoxMessages.getSelectedItem();
            if (codedMessage == null || codedMessage.isEmpty())
                throw new Exception("Empty message can' be sent");

            // Send a new MESSAGE query with the coded message encrypted
            Request query = new Request("MESSAGE");
            query.addArg(this.symmetricCrypter.encrypt(codedMessage));

            System.out.println("[ OK ] Coded message encrypted sent");
            Request reply = query.sendAndRecv(this.sock);

            // If client has been disconnected
            if (reply.is(Request.NO_COMMAND) || reply.is(Request.SOCK_ERROR))
            {
                this.disconnectFromServer();
                throw new Exception("Disconnected from server");
            }

            // If query failed
            if (reply.is("MESSAGE_FAIL"))
                throw new Exception("Server error : " + reply.getStringArg(0));

            // Unknown command
            if (!reply.is("MESSAGE_ACK"))
                throw new Exception("Invalid reply : " + reply.getCommand());

            System.out.println("[ OK ] Coded message encrypted received");

            // Get encoded message from the reply
            codedMessage = this.symmetricCrypter.decryptString(reply.getArg(0));
            System.out.println("[ OK ] Coded message : " + codedMessage);
            System.out.println("[ OK ] Real meaning  : " +
               this.interpreterProperties.getProperty(codedMessage, "UNKNOWN"));
        }
        catch (Exception exception)
        {
            System.out.println("[FAIL] " + exception.getMessage());
            MessageBoxes.ShowError(exception.getMessage(),
                                   "Error sending message");
        }
    }//GEN-LAST:event_buttonSendMessageActionPerformed

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
            for (javax.swing.UIManager.LookAndFeelInfo info :
                    javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex)
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
    private javax.swing.JButton buttonConnect;
    private javax.swing.JButton buttonGenerateNewSecretKey;
    private javax.swing.JButton buttonSendMessage;
    private javax.swing.JComboBox<String> comboBoxMessages;
    private javax.swing.JLabel labelIPServer;
    private javax.swing.JLabel labelMessage;
    private javax.swing.JLabel labelPort;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel labelStatusInfo;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelHeaderBody;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JSpinner spinnerPort;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTextArea textAreaOutput;
    private javax.swing.JTextField textFieldIP;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private Properties interpreterProperties;
    private Properties bibleProperties;

    private Socket sock;
    private boolean isConnected;
    private SymmetricCrypter symmetricCrypter;

    private DefaultComboBoxModel<String> messagesModel;
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Static variables">
    private static final String DEFAULT_IP;
    private static final String DEFAULT_PORT;

    private static final String algorithm;
    private static final String cipherMode;
    private static final String padding;

    static
    {
        DEFAULT_IP   = "127.0.0.1";
        DEFAULT_PORT = "40000";

        algorithm  = "DES";
        cipherMode = "ECB";
        padding    = "PKCS5Padding";
    }
    // </editor-fold>
}
