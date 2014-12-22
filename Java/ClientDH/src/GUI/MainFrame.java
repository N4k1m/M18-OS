package GUI;

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
        initComponents();
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
        panelHeaderV1 = new javax.swing.JPanel();
        buttonConnectV1 = new javax.swing.JButton();
        panelHeaderBodyV1 = new javax.swing.JPanel();
        labelStatusInfoV1 = new javax.swing.JLabel();
        labelStatusV1 = new javax.swing.JLabel();
        labelIPV1 = new javax.swing.JLabel();
        textFieldIPV1 = new javax.swing.JTextField();
        labelPortV1 = new javax.swing.JLabel();
        spinnerPortV1 = new javax.swing.JSpinner();
        panelBodyV1 = new javax.swing.JPanel();
        textFieldMessageV1 = new javax.swing.JTextField();
        buttonGenerateKeyV1 = new javax.swing.JButton();
        buttonSendMessageV1 = new javax.swing.JButton();
        panelV2 = new javax.swing.JPanel();
        panelHeaderV2 = new javax.swing.JPanel();
        buttonConnectV2 = new javax.swing.JButton();
        panelHeaderBodyV2 = new javax.swing.JPanel();
        labelStatusInfoV2 = new javax.swing.JLabel();
        labelStatusV2 = new javax.swing.JLabel();
        labelIPV2 = new javax.swing.JLabel();
        textFieldIPV2 = new javax.swing.JTextField();
        labelPortV2 = new javax.swing.JLabel();
        spinnerPortV2 = new javax.swing.JSpinner();
        panelBodyV2 = new javax.swing.JPanel();
        textFieldMessageV2 = new javax.swing.JTextField();
        buttonGenerateKeyV2 = new javax.swing.JButton();
        buttonSendMessageV2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        buttonClear.setText("Clear");
        getContentPane().add(buttonClear, java.awt.BorderLayout.PAGE_END);

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        textAreaOutput.setColumns(20);
        textAreaOutput.setRows(5);
        scrollPane.setViewportView(textAreaOutput);

        splitPane.setBottomComponent(scrollPane);

        panelV1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelV1.setLayout(new java.awt.BorderLayout());

        panelHeaderV1.setLayout(new java.awt.BorderLayout());

        buttonConnectV1.setText("Connect");
        panelHeaderV1.add(buttonConnectV1, java.awt.BorderLayout.LINE_END);

        panelHeaderBodyV1.setLayout(new java.awt.GridLayout(3, 2));

        labelStatusInfoV1.setText("Status :");
        panelHeaderBodyV1.add(labelStatusInfoV1);

        labelStatusV1.setText("<status>");
        panelHeaderBodyV1.add(labelStatusV1);

        labelIPV1.setText("IP address :");
        panelHeaderBodyV1.add(labelIPV1);
        panelHeaderBodyV1.add(textFieldIPV1);

        labelPortV1.setText("Port : ");
        panelHeaderBodyV1.add(labelPortV1);

        spinnerPortV1.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        panelHeaderBodyV1.add(spinnerPortV1);

        panelHeaderV1.add(panelHeaderBodyV1, java.awt.BorderLayout.CENTER);

        panelV1.add(panelHeaderV1, java.awt.BorderLayout.PAGE_START);

        panelBodyV1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        panelBodyV1.add(textFieldMessageV1, gridBagConstraints);

        buttonGenerateKeyV1.setText("Generate new key");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelBodyV1.add(buttonGenerateKeyV1, gridBagConstraints);

        buttonSendMessageV1.setText("Send message");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelBodyV1.add(buttonSendMessageV1, gridBagConstraints);

        panelV1.add(panelBodyV1, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Version 1 - Secret word", panelV1);

        panelV2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelV2.setLayout(new java.awt.BorderLayout());

        panelHeaderV2.setLayout(new java.awt.BorderLayout());

        buttonConnectV2.setText("Connect");
        panelHeaderV2.add(buttonConnectV2, java.awt.BorderLayout.LINE_END);

        panelHeaderBodyV2.setLayout(new java.awt.GridLayout(3, 2));

        labelStatusInfoV2.setText("Status :");
        panelHeaderBodyV2.add(labelStatusInfoV2);

        labelStatusV2.setText("<status>");
        panelHeaderBodyV2.add(labelStatusV2);

        labelIPV2.setText("IP address :");
        panelHeaderBodyV2.add(labelIPV2);
        panelHeaderBodyV2.add(textFieldIPV2);

        labelPortV2.setText("Port : ");
        panelHeaderBodyV2.add(labelPortV2);

        spinnerPortV2.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        panelHeaderBodyV2.add(spinnerPortV2);

        panelHeaderV2.add(panelHeaderBodyV2, java.awt.BorderLayout.CENTER);

        panelV2.add(panelHeaderV2, java.awt.BorderLayout.PAGE_START);

        panelBodyV2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        panelBodyV2.add(textFieldMessageV2, gridBagConstraints);

        buttonGenerateKeyV2.setText("Generate new key");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelBodyV2.add(buttonGenerateKeyV2, gridBagConstraints);

        buttonSendMessageV2.setText("Send message");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelBodyV2.add(buttonSendMessageV2, gridBagConstraints);

        panelV2.add(panelBodyV2, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Version 2 - Diffie Hellman", panelV2);

        splitPane.setLeftComponent(tabbedPane);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JButton buttonConnectV1;
    private javax.swing.JButton buttonConnectV2;
    private javax.swing.JButton buttonGenerateKeyV1;
    private javax.swing.JButton buttonGenerateKeyV2;
    private javax.swing.JButton buttonSendMessageV1;
    private javax.swing.JButton buttonSendMessageV2;
    private javax.swing.JLabel labelIPV1;
    private javax.swing.JLabel labelIPV2;
    private javax.swing.JLabel labelPortV1;
    private javax.swing.JLabel labelPortV2;
    private javax.swing.JLabel labelStatusInfoV1;
    private javax.swing.JLabel labelStatusInfoV2;
    private javax.swing.JLabel labelStatusV1;
    private javax.swing.JLabel labelStatusV2;
    private javax.swing.JPanel panelBodyV1;
    private javax.swing.JPanel panelBodyV2;
    private javax.swing.JPanel panelHeaderBodyV1;
    private javax.swing.JPanel panelHeaderBodyV2;
    private javax.swing.JPanel panelHeaderV1;
    private javax.swing.JPanel panelHeaderV2;
    private javax.swing.JPanel panelV1;
    private javax.swing.JPanel panelV2;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JSpinner spinnerPortV1;
    private javax.swing.JSpinner spinnerPortV2;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextArea textAreaOutput;
    private javax.swing.JTextField textFieldIPV1;
    private javax.swing.JTextField textFieldIPV2;
    private javax.swing.JTextField textFieldMessageV1;
    private javax.swing.JTextField textFieldMessageV2;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}
