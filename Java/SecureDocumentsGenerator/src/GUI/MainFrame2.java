/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

/**
 *
 * @author Nakim
 */
public class MainFrame2 extends javax.swing.JFrame
{

    /**
     * Creates new form MainFrame2
     */
    public MainFrame2()
    {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
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

        buttonStartStop.setText("<state>");
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
        getContentPane().add(buttonClear, java.awt.BorderLayout.PAGE_END);

        splitPane.setDividerLocation(230);
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(MainFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(MainFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(MainFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(MainFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new MainFrame2().setVisible(true);
            }
        });
    }

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
}
