package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class MeasurementView extends JPanel {

    private JComboBox<Object[]> cmbClient   = new JComboBox<>();
    private JTextField txtDate      = new JTextField(12);
    private JTextField txtWeightKg  = new JTextField(7);
    private JTextField txtBodyFat   = new JTextField(7);
    private JTextField txtChestCm   = new JTextField(7);
    private JTextField txtWaistCm   = new JTextField(7);
    private JTextField txtHipCm     = new JTextField(7);
    private JTextField txtNotes     = new JTextField(30);

    private JTable  tableMeasurements = new JTable();
    private JButton btnSave           = new JButton("Save");
    private JButton btnUpdate         = new JButton("Update");
    private JButton btnDelete         = new JButton("Delete");

    private int selectedId = -1;

    public MeasurementView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Norte: Formulario ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Measurement Data"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Client:", "Date (YYYY-MM-DD):", "Weight (kg):", "Body Fat (%):",
                           "Chest (cm):", "Waist (cm):", "Hip (cm):", "Notes:"};
        Component[] comps = {cmbClient, txtDate, txtWeightKg, txtBodyFat,
                              txtChestCm, txtWaistCm, txtHipCm, txtNotes};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = (i % 2) * 2;
            gbc.gridy = i / 2;
            gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = (i % 2) * 2 + 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            formPanel.add(comps[i], gbc);
        }
        add(formPanel, BorderLayout.NORTH);

        // --- Panel Centro: Tabla ---
        tableMeasurements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableMeasurements.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableMeasurements.getSelectedRow() >= 0) {
                int row = tableMeasurements.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) tableMeasurements.getModel();
                selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
                txtDate.setText(model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "");
                txtWeightKg.setText(model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "");
                txtBodyFat.setText(model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "");
                txtChestCm.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
                txtWaistCm.setText(model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "");
                txtHipCm.setText(model.getValueAt(row, 6) != null ? model.getValueAt(row, 6).toString() : "");
                txtNotes.setText(model.getValueAt(row, 7) != null ? model.getValueAt(row, 7).toString() : "");
            }
        });
        add(new JScrollPane(tableMeasurements), BorderLayout.CENTER);

        // --- Panel Sur: Botones ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnPanel.add(btnSave);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // --- Carga de combos desde el Controller ---
    public void loadClientOptions(ResultSet rs) {
        cmbClient.removeAllItems();
        cmbClient.addItem(new Object[]{-1, "— All Clients —"});
        try {
            if (rs != null) {
                while (rs.next()) {
                    final int ci = rs.getInt("ci");
                    final String name = rs.getString("first_name") + " " + rs.getString("last_name");
                    cmbClient.addItem(new Object[]{ci, name});
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        cmbClient.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value instanceof Object[]) lbl.setText(((Object[]) value)[1].toString());
            if (isSelected) { lbl.setBackground(list.getSelectionBackground()); lbl.setOpaque(true); }
            return lbl;
        });
    }

    // --- Getters para el Controller ---
    public int    getClientCI() {
        Object[] sel = (Object[]) cmbClient.getSelectedItem();
        return (sel != null) ? (int) sel[0] : -1;
    }
    public String getMeasurementDate() { return txtDate.getText().trim(); }
    public double getWeightKg()  { return parseDouble(txtWeightKg.getText()); }
    public double getBodyFat()   { return parseDouble(txtBodyFat.getText()); }
    public double getChestCm()   { return parseDouble(txtChestCm.getText()); }
    public double getWaistCm()   { return parseDouble(txtWaistCm.getText()); }
    public double getHipCm()     { return parseDouble(txtHipCm.getText()); }
    public String getNotes()     { return txtNotes.getText().trim(); }
    public int    getSelectedId(){ return selectedId; }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }

    // --- Metodos que llama el Controller ---
    public void showList(ResultSet rs) {
        try {
            DefaultTableModel model = new DefaultTableModel();
            if (rs != null) {
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();
                String[] colNames = new String[cols];
                for (int i = 1; i <= cols; i++) colNames[i - 1] = meta.getColumnName(i);
                model.setColumnIdentifiers(colNames);
                while (rs.next()) {
                    Object[] row = new Object[cols];
                    for (int i = 1; i <= cols; i++) row[i - 1] = rs.getObject(i);
                    model.addRow(row);
                }
            }
            tableMeasurements.setModel(model);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void clearFields() {
        txtDate.setText("");
        txtWeightKg.setText("");
        txtBodyFat.setText("");
        txtChestCm.setText("");
        txtWaistCm.setText("");
        txtHipCm.setText("");
        txtNotes.setText("");
        selectedId = -1;
        tableMeasurements.clearSelection();
    }

    // --- Exponer botones y combo para el Controller ---
    public JButton getBtnSave()   { return btnSave; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JComboBox<Object[]> getCmbClient() { return cmbClient; }
}
