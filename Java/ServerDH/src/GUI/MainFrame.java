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
        this.initComponents();
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

        buttonClear.setText("Clear");
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
}
