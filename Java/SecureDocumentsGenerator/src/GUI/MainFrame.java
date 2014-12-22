package GUI;

import MyLittleCheapLibrary.CIAManager;
import SPF.Authentication.Authentication;
import SPF.Cle;
import SPF.Crypto.Chiffrement;
import SPF.Integrity.Integrity;
import Threads.ThServer;
import Utils.PropertyLoader;
import Utils.TextAreaOutputStream;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

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

        // Create ComboBoxes and Spinners models
        this.createModels();

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
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }

        this.isRunning = false;
        this.showStatus();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void createModels()
    {
        // Create spinner models to limit key length
        this.defaultSpinnerModel = new SpinnerNumberModel(5, 0, 100, 1);

        // Create spinner model for DES key length
        this.DESkeyLengthSpinnerModel = new SpinnerListModel(new Integer[]{64});

        // Create spinner model for AES key length
        this.AESKeyLengthSpinnerModel = new SpinnerListModel(
            new Integer[]{128, 192, 256});

        this.cipherProvidersModel = new DefaultComboBoxModel(
            new String[] {"AlbertiFamily", "Triumvirat",
                          "ProCrypto", "CryptoCBCAESProvider"});

        this.authenticationProvidersModels = new DefaultComboBoxModel(
            new String[]{"HMACSHA1MawetProvider"});

        this.algorithmModel = new DefaultComboBoxModel(
            new String[] {"DES", "AES"});

        // Apply models to widgets
        this.comboBoxCipherProviders.setModel(this.cipherProvidersModel);
        this.comboBoxAuthenticationProviders.setModel(
            this.authenticationProvidersModels);
        this.comboBoxAlgorithms.setModel(this.algorithmModel);
        this.spinnerCipherKeyLength.setModel(this.defaultSpinnerModel);
    }

    private void startServer()
    {
        // Start thread
        int port = (int)this.spinnerPort.getValue();
        this.thServer = new ThServer(this, port);
        this.thServer.start();

        this.isRunning = !(this.thServer == null || !this.thServer.isAlive());
        this.showStatus();
    }

    private void stopServer()
    {
        if (this.thServer == null)
            return;

        try
        {
            // Stop thread
            this.thServer.requestStop();
            this.thServer.join();
            this.thServer = null;
        }
        catch (IOException | InterruptedException ex)
        {
            System.err.println(ex);
        }

        this.isRunning = !(this.thServer == null || !this.thServer.isAlive());
        this.showStatus();
    }

    private void showStatus()
    {
        this.spinnerPort.setEnabled(!this.isRunning);

        if (!this.isRunning)
        {
            this.labelStatus.setForeground(Color.RED);
            this.labelStatus.setText("Server stopped");
            this.buttonStartStop.setText("Start");
        }
        else
        {
            this.labelStatus.setForeground(Color.GREEN);
            this.labelStatus.setText("Server is running");
            this.buttonStartStop.setText("Stop");
        }
    }

    private synchronized void generateCipherKey(String provider)
    {
        // Get new cipher if provider is different
        if (this.chiffrement == null ||
            !this.chiffrement.getProvider().equals(provider))
        {
            System.out.println("[ OK ] Chiffrement " + provider + " recupere");
            this.chiffrement = CIAManager.getChiffrement(provider);
        }

        // Generate key
        this.cle = this.chiffrement.genererCle(
            (int)this.spinnerCipherKeyLength.getValue());
        System.out.println("[ OK ] Cle " + provider + " generee");

        this.serializeKey();
    }

    private synchronized void serializeKey()
    {
        if (this.cle == null)
            return;

        // Demande de la destination où sauvegarder la clé
        JFileChooser chooser = new JFileChooser(System.getProperty("user.documents"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("SER FILES", "ser");
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileFilter(filter);

        int option = chooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION)
        {
            System.out.println("[FAIL] Sauvegarde annulee, cle non enregistree.");
            return;
        }

        String fileName = chooser.getSelectedFile().getAbsolutePath();
        if (!fileName.endsWith(".ser"))
            fileName += ".ser";

        System.out.println("[ OK ] Destination : " + fileName);

        try
        {
            // ouverture d'un flux de sortie vers le fichier
            final FileOutputStream fos = new FileOutputStream(fileName);
            // création d'un "flux objet" avec le flux fichier
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            try
            {
                // sérialisation : écriture de l'objet dans le flux de sortie
                oos.writeObject(this.cle);
                oos.flush();

                System.out.println("[ OK ] Cle serialisee ...");
            }
            finally
            {
                try
                {
                    oos.close();
                }
                finally
                {
                    fos.close();
                }
            }
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        panelHeader = new javax.swing.JPanel();
        buttonStartStop = new javax.swing.JButton();
        panelHeaderBody = new javax.swing.JPanel();
        labelStatusTitle = new javax.swing.JLabel();
        labelStatus = new javax.swing.JLabel();
        labelPort = new javax.swing.JLabel();
        spinnerPort = new javax.swing.JSpinner();
        buttonClear = new javax.swing.JButton();
        splitPane = new javax.swing.JSplitPane();
        scrollPane = new javax.swing.JScrollPane();
        textAreaOutput = new javax.swing.JTextArea();
        tabbedPane = new javax.swing.JTabbedPane();
        panelGenerateCipherKey = new javax.swing.JPanel();
        labelCipherProvider = new javax.swing.JLabel();
        comboBoxCipherProviders = new javax.swing.JComboBox();
        labelCipherKeyLength = new javax.swing.JLabel();
        spinnerCipherKeyLength = new javax.swing.JSpinner();
        buttonGenerateCipherKey = new javax.swing.JButton();
        panelGenerateAuthenticationKey = new javax.swing.JPanel();
        labelAuthenticationProvider = new javax.swing.JLabel();
        comboBoxAuthenticationProviders = new javax.swing.JComboBox();
        labelAlgorithm = new javax.swing.JLabel();
        comboBoxAlgorithms = new javax.swing.JComboBox();
        labelSecretMessage = new javax.swing.JLabel();
        textFieldSecretMessage = new javax.swing.JTextField();
        buttonGenerateAuthenticationKey = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Secure Documents Generator");

        panelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelHeader.setLayout(new java.awt.BorderLayout(10, 0));

        buttonStartStop.setText("Start");
        buttonStartStop.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonStartStopActionPerformed(evt);
            }
        });
        panelHeader.add(buttonStartStop, java.awt.BorderLayout.LINE_END);

        panelHeaderBody.setLayout(new java.awt.GridLayout(2, 2));

        labelStatusTitle.setText("Status :");
        panelHeaderBody.add(labelStatusTitle);

        labelStatus.setText("<status>");
        panelHeaderBody.add(labelStatus);

        labelPort.setText("Listening port :");
        panelHeaderBody.add(labelPort);
        panelHeaderBody.add(spinnerPort);

        panelHeader.add(panelHeaderBody, java.awt.BorderLayout.CENTER);

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

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        scrollPane.setViewportView(textAreaOutput);

        splitPane.setBottomComponent(scrollPane);

        panelGenerateCipherKey.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelGenerateCipherKey.setLayout(new java.awt.GridBagLayout());

        labelCipherProvider.setText("Cipher provider :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelGenerateCipherKey.add(labelCipherProvider, gridBagConstraints);

        comboBoxCipherProviders.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                comboBoxCipherProvidersItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        panelGenerateCipherKey.add(comboBoxCipherProviders, gridBagConstraints);

        labelCipherKeyLength.setText("Cipher key length :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGenerateCipherKey.add(labelCipherKeyLength, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGenerateCipherKey.add(spinnerCipherKeyLength, gridBagConstraints);

        buttonGenerateCipherKey.setText("Generate and save");
        buttonGenerateCipherKey.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonGenerateCipherKeyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGenerateCipherKey.add(buttonGenerateCipherKey, gridBagConstraints);

        tabbedPane.addTab("Generate cipher key", panelGenerateCipherKey);

        panelGenerateAuthenticationKey.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelGenerateAuthenticationKey.setLayout(new java.awt.GridBagLayout());

        labelAuthenticationProvider.setText("Authentication provider :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelGenerateAuthenticationKey.add(labelAuthenticationProvider, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        panelGenerateAuthenticationKey.add(comboBoxAuthenticationProviders, gridBagConstraints);

        labelAlgorithm.setText("Algorithm :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGenerateAuthenticationKey.add(labelAlgorithm, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGenerateAuthenticationKey.add(comboBoxAlgorithms, gridBagConstraints);

        labelSecretMessage.setText("Secret message :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGenerateAuthenticationKey.add(labelSecretMessage, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGenerateAuthenticationKey.add(textFieldSecretMessage, gridBagConstraints);

        buttonGenerateAuthenticationKey.setText("Generate and save");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGenerateAuthenticationKey.add(buttonGenerateAuthenticationKey, gridBagConstraints);

        tabbedPane.addTab("Generate authentication key", panelGenerateAuthenticationKey);

        splitPane.setLeftComponent(tabbedPane);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //<editor-fold defaultstate="collapsed" desc="Events management">
    private void buttonStartStopActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonStartStopActionPerformed
    {//GEN-HEADEREND:event_buttonStartStopActionPerformed
        if(this.isRunning)
            this.stopServer();
        else
            this.startServer();
    }//GEN-LAST:event_buttonStartStopActionPerformed

    private void buttonClearActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonClearActionPerformed
    {//GEN-HEADEREND:event_buttonClearActionPerformed
        this.textAreaOutput.setText(null);
    }//GEN-LAST:event_buttonClearActionPerformed

    private void comboBoxCipherProvidersItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_comboBoxCipherProvidersItemStateChanged
    {//GEN-HEADEREND:event_comboBoxCipherProvidersItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED)
        {
            int index = this.comboBoxCipherProviders.getSelectedIndex();

            switch(index)
            {
                case 0: // Alberti
                case 1: // Cesar
                    this.spinnerCipherKeyLength.setModel(
                        this.defaultSpinnerModel);
                    break;
                case 2: // DES
                    this.spinnerCipherKeyLength.setModel(
                        this.DESkeyLengthSpinnerModel);
                    break;
                case 3: // AES
                    this.spinnerCipherKeyLength.setModel(
                        this.AESKeyLengthSpinnerModel);
                    break;
            }
        }
    }//GEN-LAST:event_comboBoxCipherProvidersItemStateChanged

    private void buttonGenerateCipherKeyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonGenerateCipherKeyActionPerformed
    {//GEN-HEADEREND:event_buttonGenerateCipherKeyActionPerformed
        System.out.println("[ OK ] Debut d'une nouvelle generation de cle de cryptage ...");

        String provider = (String)this.comboBoxCipherProviders.getSelectedItem();

        if (provider == null || provider.isEmpty())
            return;

        this.generateCipherKey(provider); // Synchronized
    }//GEN-LAST:event_buttonGenerateCipherKeyActionPerformed
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Widgets">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonGenerateAuthenticationKey;
    private javax.swing.JButton buttonGenerateCipherKey;
    private javax.swing.JButton buttonStartStop;
    private javax.swing.JComboBox comboBoxAlgorithms;
    private javax.swing.JComboBox comboBoxAuthenticationProviders;
    private javax.swing.JComboBox comboBoxCipherProviders;
    private javax.swing.JLabel labelAlgorithm;
    private javax.swing.JLabel labelAuthenticationProvider;
    private javax.swing.JLabel labelCipherKeyLength;
    private javax.swing.JLabel labelCipherProvider;
    private javax.swing.JLabel labelPort;
    private javax.swing.JLabel labelSecretMessage;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel labelStatusTitle;
    private javax.swing.JPanel panelGenerateAuthenticationKey;
    private javax.swing.JPanel panelGenerateCipherKey;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelHeaderBody;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JSpinner spinnerCipherKeyLength;
    private javax.swing.JSpinner spinnerPort;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextArea textAreaOutput;
    private javax.swing.JTextField textFieldSecretMessage;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private variables">
    private Properties prop;
    private boolean isRunning;
    private ThServer thServer;

    // Models
    private SpinnerNumberModel defaultSpinnerModel;
    private SpinnerListModel DESkeyLengthSpinnerModel;
    private SpinnerListModel AESKeyLengthSpinnerModel;

    private ComboBoxModel cipherProvidersModel;
    private ComboBoxModel authenticationProvidersModels;
    private ComboBoxModel algorithmModel;

    private Chiffrement chiffrement;
    private Cle cle;
    private Integrity integrity;
    private Authentication authentication;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Static variable">
    private static final String DEFAULT_PORT;

    static
    {
        DEFAULT_PORT = "40000";
    }
    // </editor-fold>
}
