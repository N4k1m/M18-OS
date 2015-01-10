package GUI;

/**
 *
 * @author Nakim
 */
public class PanelNumberOfSeconds extends javax.swing.JPanel
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public PanelNumberOfSeconds()
    {
        this.initComponents();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Public methods">
    public int getDelay()
    {
        return (int)this.spinner.getValue();
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        label = new javax.swing.JLabel();
        spinner = new javax.swing.JSpinner();

        setLayout(new java.awt.GridLayout(2, 1));

        label.setText("Number of second(s) before shutdown ?");
        add(label);

        spinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        spinner.setEditor(new javax.swing.JSpinner.NumberEditor(spinner, "# second(s)"));
        add(spinner);
    }// </editor-fold>//GEN-END:initComponents

    //<editor-fold defaultstate="collapsed" desc="Widgets">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel label;
    private javax.swing.JSpinner spinner;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}
