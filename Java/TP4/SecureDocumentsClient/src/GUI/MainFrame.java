package GUI;

import MyLittleCheapLibrary.CIAManager;
import SGDOCP.SGDOCPCommand;
import SGDOCP.SGDOCPRequest;
import SPF.Authentication.Authentication;
import SPF.Authentication.SymmetricKey;
import SPF.Cle;
import SPF.Crypto.Chiffrement;
import SPF.Integrity.Integrity;
import Utils.BytesConverter;
import Utils.MessageBoxes;
import Utils.PropertyLoader;
import Utils.TextAreaOutputStream;
import java.awt.Color;
import java.awt.Component;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author nakim
 */
public class MainFrame extends javax.swing.JFrame
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public MainFrame()
    {
        this.initComponents();
        this.createModels();

        // Redirect the system output to a TextArea
        TextAreaOutputStream toas =
            TextAreaOutputStream.getInstance(this.textAreaOutput);

        this.loadDefaultSettings();
        this.sock = null;

        // Center frame
        this.setLocationRelativeTo(null);

        this.showStatus();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void createModels()
    {
        this.chiffrementProvidersModel = new DefaultComboBoxModel<>(
            new String[] {"AlbertiFamily", "Triumvirat", "ProCrypto", "CryptoCBCAESProvider"});

        this.AuthenticationProvidersModels = new DefaultComboBoxModel<>(
            new String[]{"HMACSHA1MawetProvider"});

        this.IntegrityProvidersModel = new DefaultComboBoxModel<>(
            new String[]{"SHA1MawetProvider"});

        // Application des modèles aux widgets
        this.comboBoxCipherProviders.setModel(this.chiffrementProvidersModel);
        this.comboBoxAuthenticationProviders.setModel(this.AuthenticationProvidersModels);
        this.comboBoxIntegrityProviders.setModel(this.IntegrityProvidersModel);
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

    private void getCleFromFile(String keyname)
    {
        try
        {
            final FileInputStream fis = new FileInputStream(keysFolderPath + keyname);
            ObjectInputStream ois = new ObjectInputStream(fis);

            try
            {
                this.cle = (Cle) ois.readObject();
            }
            finally
            {
                // on ferme les flux
                try
                {
                    ois.close();
                }
                finally
                {
                    fis.close();
                }
            }
        }
        catch (IOException | ClassNotFoundException ex)
        {
            System.err.println(ex);
            this.cle = null;
        }
    }

    private void connectToServer()
    {
        if (this.sock != null)
            this.disconnectFromServer();

        int port = (int)this.spinnerPortServer.getValue();
        String ip = this.textFieldIPServer.getText();
        System.out.println("[ OK ] Start a new connection to server "
            + ip + ":" + port);

        try
        {
            this.sock = new Socket(ip, port);

            // Throws exception if an error occured
            this.loginProcedure();
        }
        catch (UnknownHostException ex)
        {
            System.out.println("[FAIL] Host unreachable. Invalid IP " + ip);
        }
        catch (IOException ex)
        {
            System.out.println("[FAIL] Failed to connect");
        }
        catch (Exception e)
        {
            System.out.println("[FAIL] Login failed : " + e.getMessage());
            MessageBoxes.ShowError(e.getMessage(), "Login failed");
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
            this.sock.close();
            this.sock = null;
        }
        catch (IOException ex)
        {
            System.out.println("[FAIL] An error occurred disconnecting the "
                + "system from the server : " + ex);
        }
        finally
        {
            this.showStatus();
        }
    }

    private void showStatus()
    {
        this.isConnected = this.sock != null && this.sock.isConnected();

        // Enable or disable widgets

        for (Component component : this.panelGetDocuments.getComponents())
            component.setEnabled(this.isConnected);

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

    private void loginProcedure() throws Exception
    {
        SGDOCPRequest query = new SGDOCPRequest(SGDOCPCommand.LOGIN);
        SGDOCPRequest reply;

        // Send LOGIN + username
        query.addArg(this.textFieldLogin.getText());
        System.out.println("[ OK ] Send login request");
        reply = query.sendAndRecv(this.sock);

        // If server closed the connection
        if (reply.is(SGDOCPCommand.NO_COMMAND) || reply.is(SGDOCPCommand.SOCK_ERROR))
            throw new Exception("Disconnected from server");

        // If LOGIN failed
        if (reply.is(SGDOCPCommand.FAIL))
            throw new Exception(reply.getStringArg(0)); // Arg 0 = cause

        // Invalid reply
        if (!reply.is(SGDOCPCommand.LOGIN_ACK))
            throw new Exception("Invalid reply");

        // Get public key from server (Arg 0)
        X509EncodedKeySpec x509KeySpec =
            new X509EncodedKeySpec(reply.getArg(0));
        KeyFactory keyFact = KeyFactory.getInstance("DH");
        PublicKey publicKey = keyFact.generatePublic(x509KeySpec);

        /* Gets the DH parameters associated with the server public key.
         * The client must use the same parameters when he generates his
         * own key pair. */
        DHParameterSpec dhSpec = ((DHPublicKey)publicKey).getParams();

        System.out.println("[ OK ] Get server params");

        // Creates his own DH key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
        keyGen.initialize(dhSpec);

        KeyPair keypair = keyGen.generateKeyPair();

        /* Prepare to generate the secret key with the client private key
         * and the server public key */
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(keypair.getPrivate());
        ka.doPhase(publicKey, true);

        // Generate new secret key for Authentication
        SymmetricKey symmetricKey = new SymmetricKey(ka.generateSecret("DES"), 1024);
        System.out.println("[ OK ] Secret key generated");

        // Get authentication
        this.authentication = CIAManager.getAuthentication(reply.getStringArg(1));
        this.authentication.init(symmetricKey);

        // hmac password
        byte[] hmac = this.authentication.makeAuthenticate(
            new String(this.passwordField.getPassword()));

        // Send LOGIN + Public Key + HMAC
        query.clearArgs();
        query.addArg(keypair.getPublic().getEncoded());
        query.addArg(hmac);

        System.out.println("[ OK ] Send public key and hmac password");
        reply = query.sendAndRecv(sock);

        // If LOGIN failed
        if (reply.is(SGDOCPCommand.FAIL))
            throw new Exception(reply.getStringArg(0)); // Arg 0 = cause

        // Invalid reply
        if (!reply.is(SGDOCPCommand.LOGIN_ACK))
            throw new Exception("Invalid reply");

        System.out.println("[ OK ] Successfully logged in");
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        panelHeader = new javax.swing.JPanel();
        labelIPServer = new javax.swing.JLabel();
        textFieldIPServer = new javax.swing.JTextField();
        labelPortServer = new javax.swing.JLabel();
        spinnerPortServer = new javax.swing.JSpinner();
        buttonConnect = new javax.swing.JButton();
        labelStatusInfo = new javax.swing.JLabel();
        labelStatus = new javax.swing.JLabel();
        labelLogin = new javax.swing.JLabel();
        textFieldLogin = new javax.swing.JTextField();
        labelPassword = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        buttonClear = new javax.swing.JButton();
        splitPane = new javax.swing.JSplitPane();
        tabbedPane = new javax.swing.JTabbedPane();
        panelGetDocuments = new javax.swing.JPanel();
        labelDocumentName = new javax.swing.JLabel();
        textFieldDocumentName = new javax.swing.JTextField();
        checkBoxEncrypt = new javax.swing.JCheckBox();
        labelCipherProvider = new javax.swing.JLabel();
        comboBoxCipherProviders = new javax.swing.JComboBox<String>();
        labelCipherKey = new javax.swing.JLabel();
        textFieldCipherKeyName = new javax.swing.JTextField();
        checkBoxAuthentication = new javax.swing.JCheckBox();
        labelAuthenticationProvider = new javax.swing.JLabel();
        comboBoxAuthenticationProviders = new javax.swing.JComboBox<String>();
        labelAuthenticationKey = new javax.swing.JLabel();
        textFieldAuthenticationKeyName = new javax.swing.JTextField();
        checkBoxIntegrity = new javax.swing.JCheckBox();
        labelIntegrityProvider = new javax.swing.JLabel();
        comboBoxIntegrityProviders = new javax.swing.JComboBox<String>();
        buttonGetDocument = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        textAreaOutput = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Secure Documents Client");

        panelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        java.awt.GridBagLayout panelHeaderLayout = new java.awt.GridBagLayout();
        panelHeaderLayout.columnWidths = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0};
        panelHeaderLayout.rowHeights = new int[] {0, 3, 0, 3, 0};
        panelHeader.setLayout(panelHeaderLayout);

        labelIPServer.setText("IP Address : ");
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

        labelLogin.setText("Login : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelHeader.add(labelLogin, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelHeader.add(textFieldLogin, gridBagConstraints);

        labelPassword.setText("Password :");
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

        buttonClear.setText("Clear");
        buttonClear.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonClearActionPerformed(evt);
            }
        });
        getContentPane().add(buttonClear, java.awt.BorderLayout.PAGE_END);

        splitPane.setDividerLocation(300);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setOneTouchExpandable(true);

        panelGetDocuments.setLayout(new java.awt.GridBagLayout());

        labelDocumentName.setText("Document name : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelDocumentName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.4;
        panelGetDocuments.add(textFieldDocumentName, gridBagConstraints);

        checkBoxEncrypt.setText("Encrypt file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(checkBoxEncrypt, gridBagConstraints);

        labelCipherProvider.setText("Cipher provider : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelCipherProvider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(comboBoxCipherProviders, gridBagConstraints);

        labelCipherKey.setText("Cipher key : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelCipherKey, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(textFieldCipherKeyName, gridBagConstraints);

        checkBoxAuthentication.setText("Authenticate file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(checkBoxAuthentication, gridBagConstraints);

        labelAuthenticationProvider.setText("Authentication provider : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelAuthenticationProvider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(comboBoxAuthenticationProviders, gridBagConstraints);

        labelAuthenticationKey.setText("Authentication key : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelAuthenticationKey, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(textFieldAuthenticationKeyName, gridBagConstraints);

        checkBoxIntegrity.setText("Check integrity");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        panelGetDocuments.add(checkBoxIntegrity, gridBagConstraints);

        labelIntegrityProvider.setText("Integrity provider : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelIntegrityProvider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(comboBoxIntegrityProviders, gridBagConstraints);

        buttonGetDocument.setText("Get document");
        buttonGetDocument.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonGetDocumentActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(buttonGetDocument, gridBagConstraints);

        tabbedPane.addTab("Get Documents", panelGetDocuments);

        splitPane.setTopComponent(tabbedPane);

        textAreaOutput.setEditable(false);
        scrollPane.setViewportView(textAreaOutput);

        splitPane.setRightComponent(scrollPane);

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

    private void buttonGetDocumentActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonGetDocumentActionPerformed
    {//GEN-HEADEREND:event_buttonGetDocumentActionPerformed
        try
        {
            // Check if client is connected
            if (this.sock == null || !this.sock.isConnected())
                throw new Exception("You are disconnected from server");

            if (this.textFieldDocumentName.getText().isEmpty())
                throw new Exception("Document name is missing");

            if (this.checkBoxEncrypt.isSelected() &&
                this.textFieldCipherKeyName.getText().isEmpty())
                throw new Exception("Cipher key name is missing");

            if (this.checkBoxAuthentication.isSelected() &&
                this.textFieldAuthenticationKeyName.getText().isEmpty())
                throw new Exception("Authentication key name is missing");
        }
        catch (Exception e)
        {
            System.out.println("[FAIL] " + e.getMessage());
            MessageBoxes.ShowError(e.getMessage(), "Error");
            return;
        }

        System.out.println("[ OK ] Build query ...");
        SGDOCPRequest reply = null;
        SGDOCPRequest requ  = new SGDOCPRequest(SGDOCPCommand.GET_DOCUMENT);

        // Ajout du nom du fichier
        requ.addArg(this.textFieldDocumentName.getText());

        // Ajout des flags des paramètres
        requ.addArg(BytesConverter.toByteArray(
            this.checkBoxEncrypt.isSelected()));
        requ.addArg(BytesConverter.toByteArray(
            this.checkBoxAuthentication.isSelected()));
        requ.addArg(BytesConverter.toByteArray(
            this.checkBoxIntegrity.isSelected()));

        // Ajout du provider de chiffrement et du nom de la clé
        if (this.checkBoxEncrypt.isSelected())
        {
            requ.addArg((String)this.comboBoxCipherProviders.getSelectedItem());
            requ.addArg(this.textFieldCipherKeyName.getText());
        }

        // Ajout du provider de Authentification et du nom de la clé
        if (this.checkBoxAuthentication.isSelected())
        {
            requ.addArg((String)this.comboBoxAuthenticationProviders.getSelectedItem());
            requ.addArg(this.textFieldAuthenticationKeyName.getText());
        }

        // Ajout du provider Integrity
        if (this.checkBoxIntegrity.isSelected())
        {
            requ.addArg((String)this.comboBoxIntegrityProviders.getSelectedItem());
        }

        reply = requ.sendAndRecv(this.sock);

        if (reply.is(SGDOCPCommand.GET_DOCUMENT_ACK))
        {
            int currentIndex = 0;
            String content = reply.getStringArg(currentIndex);
            System.out.println("[ RQ ] Received : " + content);

            // décryptage du text
            if (this.checkBoxEncrypt.isSelected())
            {
                this.chiffrement = CIAManager.getChiffrement(
                    (String)this.comboBoxCipherProviders.getSelectedItem());
                this.getCleFromFile(this.textFieldCipherKeyName.getText());
                this.chiffrement.init(this.cle);

                content = this.chiffrement.decrypte(content);
                System.out.println("[ RQ ] Decrypted text : " + content);
            }

            // Vérification de l'authentification
            if (this.checkBoxAuthentication.isSelected())
            {
                byte[] hmac = reply.getArg(++currentIndex);
                this.authentication = CIAManager.getAuthentication(
                    (String)this.comboBoxAuthenticationProviders.getSelectedItem());
                this.getCleFromFile(this.textFieldAuthenticationKeyName.getText());
                this.authentication.init(this.cle);

                if (this.authentication.verifyAuthenticate(content, hmac))
                    System.out.println("[ RQ ] Valid authentification");
                else
                    System.out.println("[FAIL] Invalid authentication");
            }

            if (this.checkBoxIntegrity.isSelected())
            {
                byte[] hash = reply.getArg(++currentIndex);
                this.integrity = CIAManager.getIntegrity(
                    (String)this.comboBoxIntegrityProviders.getSelectedItem());

                if (this.integrity.verifyCheck(content, hash))
                    System.out.println("[ RQ ] Valid integrity");
                else
                    System.out.println("[FAIL] Invalid integrity");
            }
        }
        else if (reply.is(SGDOCPCommand.FAIL))
        {
            String cause = reply.getStringArg(0);
            System.out.println("[FAIL] " + cause);
            MessageBoxes.ShowError(cause, "Request Error");
        }
        else if (reply.is(SGDOCPCommand.NO_COMMAND) ||
                 reply.is(SGDOCPCommand.SOCK_ERROR))
        {
            this.disconnectFromServer();
            MessageBoxes.ShowError("Disconnected from server",
                                   "Disconnected from server");
        }
    }//GEN-LAST:event_buttonGetDocumentActionPerformed

    private void buttonClearActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonClearActionPerformed
    {//GEN-HEADEREND:event_buttonClearActionPerformed
        this.textAreaOutput.setText(null);
    }//GEN-LAST:event_buttonClearActionPerformed
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Widgets">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonConnect;
    private javax.swing.JButton buttonGetDocument;
    private javax.swing.JCheckBox checkBoxAuthentication;
    private javax.swing.JCheckBox checkBoxEncrypt;
    private javax.swing.JCheckBox checkBoxIntegrity;
    private javax.swing.JComboBox<String> comboBoxAuthenticationProviders;
    private javax.swing.JComboBox<String> comboBoxCipherProviders;
    private javax.swing.JComboBox<String> comboBoxIntegrityProviders;
    private javax.swing.JLabel labelAuthenticationKey;
    private javax.swing.JLabel labelAuthenticationProvider;
    private javax.swing.JLabel labelCipherKey;
    private javax.swing.JLabel labelCipherProvider;
    private javax.swing.JLabel labelDocumentName;
    private javax.swing.JLabel labelIPServer;
    private javax.swing.JLabel labelIntegrityProvider;
    private javax.swing.JLabel labelLogin;
    private javax.swing.JLabel labelPassword;
    private javax.swing.JLabel labelPortServer;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel labelStatusInfo;
    private javax.swing.JPanel panelGetDocuments;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JSpinner spinnerPortServer;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextArea textAreaOutput;
    private javax.swing.JTextField textFieldAuthenticationKeyName;
    private javax.swing.JTextField textFieldCipherKeyName;
    private javax.swing.JTextField textFieldDocumentName;
    private javax.swing.JTextField textFieldIPServer;
    private javax.swing.JTextField textFieldLogin;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private variables">
    private Socket sock;
    private boolean isConnected;

    // Models
    private DefaultComboBoxModel<String> chiffrementProvidersModel;
    private DefaultComboBoxModel<String> IntegrityProvidersModel;
    private DefaultComboBoxModel<String> AuthenticationProvidersModels;

    private Cle cle;
    private Chiffrement chiffrement;
    private Integrity integrity;
    private Authentication authentication;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Static variables">
    private static final String DEFAULT_IP;
    private static final String DEFAULT_PORT;
    private static final String keysFolderPath;

    static
    {
        DEFAULT_IP   = "127.0.0.1";
        DEFAULT_PORT = "40000";
        keysFolderPath = "KEYS" + System.getProperty("file.separator");
    }
    // </editor-fold>
}
