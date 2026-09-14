package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MeasurementView extends JPanel {

    private JComboBox<String> cmbClient = new JComboBox<>();
    private ArrayList<Integer> clientCIs = new ArrayList<>();
    
    private JTextField txtDate      = new JTextField();
    private JTextField txtWeightKg  = new JTextField();
    private JTextField txtBodyFat   = new JTextField();
    private JTextField txtChestCm   = new JTextField();
    private JTextField txtWaistCm   = new JTextField();
    private JTextField txtGlutes    = new JTextField();

    private JTable tableMeasurements;
    private DefaultTableModel tableModel;
    private JButton btnSave   = new JButton("Save");
    private JButton btnUpdate = new JButton("Update");
    private JButton btnDelete = new JButton("Delete");

    public MeasurementView() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(UITheme.CREAM);

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildFormPanel() {
        JPanel panel = UITheme.createSectionPanel("Measurement Data");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        UITheme.styleComboBox(cmbClient);

        // Dar tamaño preferido a los campos para que no colapsen
        Dimension fieldSize = new Dimension(120, 28);
        txtDate.setFont(UITheme.LABEL_FONT);     txtDate.setPreferredSize(fieldSize);
        txtWeightKg.setFont(UITheme.LABEL_FONT); txtWeightKg.setPreferredSize(fieldSize);
        txtBodyFat.setFont(UITheme.LABEL_FONT);  txtBodyFat.setPreferredSize(fieldSize);
        txtChestCm.setFont(UITheme.LABEL_FONT);  txtChestCm.setPreferredSize(fieldSize);
        txtWaistCm.setFont(UITheme.LABEL_FONT);  txtWaistCm.setPreferredSize(fieldSize);
        txtGlutes.setFont(UITheme.LABEL_FONT);   txtGlutes.setPreferredSize(fieldSize);

        // --- Fila 0: Client (ancho completo, 4 columnas) ---
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 1;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 12, 6, 6);
        panel.add(UITheme.styledLabel("Client:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3;   // ocupa col 1,2,3
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 12);
        panel.add(cmbClient, gbc);

        // IMPORTANTE: resetear gridwidth antes de las filas de dos pares
        gbc.gridwidth = 1;

        // --- Fila 1: Weight + Body Fat ---
        UITheme.addFormRow2(panel, gbc, 1, "Weight (kg):", txtWeightKg, "Body Fat (%):", txtBodyFat);
        // --- Fila 2: Chest + Glutes ---
        UITheme.addFormRow2(panel, gbc, 2, "Chest (cm):", txtChestCm, "Glutes (cm):", txtGlutes);
        // --- Fila 3: Waist + Date ---
        UITheme.addFormRow2(panel, gbc, 3, "Waist (cm):", txtWaistCm, "Date (yyyy-MM-dd):", txtDate);

        return panel;
    }

    private JScrollPane buildTablePanel() {
        String[] cols = {"ID", "Date", "Weight(kg)", "Body Fat(%)", "Chest", "Waist", "Glutes", "Client"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableMeasurements = new JTable(tableModel);
        UITheme.styleTable(tableMeasurements);
        tableMeasurements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableMeasurements.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableMeasurements.getSelectedRow() >= 0) {
                int row = tableMeasurements.getSelectedRow();
                txtDate.setText(tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "");
                txtWeightKg.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
                txtBodyFat.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
                txtChestCm.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
                txtWaistCm.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
                txtGlutes.setText(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "");
            }
        });

        JScrollPane scroll = new JScrollPane(tableMeasurements);
        scroll.getViewport().setBackground(UITheme.WHITE);
        scroll.setPreferredSize(new Dimension(0, 280));
        return scroll;
    }

    private JPanel buildButtonPanel() {
        UITheme.styleSaveButton(btnSave);
        UITheme.styleUpdateButton(btnUpdate);
        UITheme.styleDeleteButton(btnDelete);
        return UITheme.createButtonPanel(btnSave, btnUpdate, btnDelete);
    }

    public void loadClientOptions(ResultSet rs) {
        cmbClient.removeAllItems();
        clientCIs.clear();
        cmbClient.addItem("— All Clients —");
        clientCIs.add(-1);
        try {
            if (rs != null) {
                while (rs.next()) {
                    int ci = rs.getInt("ci");
                    String name = ci + " - " + rs.getString("first_name") + " " + rs.getString("last_name");
                    clientCIs.add(ci);
                    cmbClient.addItem(name);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showList(ResultSet data) {
        tableModel.setRowCount(0);
        try {
            while (data != null && data.next()) {
                tableModel.addRow(new Object[]{
                    data.getInt("id_measurement"),
                    data.getString("date"),
                    data.getDouble("weight"),
                    data.getDouble("body_fat"),
                    data.getDouble("chest"),
                    data.getDouble("waist"),
                    data.getDouble("glutes"),
                    data.getString("client_name")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Gym MVC", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearFields() {
        txtDate.setText("");
        txtWeightKg.setText("");
        txtBodyFat.setText("");
        txtChestCm.setText("");
        txtWaistCm.setText("");
        txtGlutes.setText("");
        tableMeasurements.clearSelection();
    }

    public int getClientCI() {
        int idx = cmbClient.getSelectedIndex();
        if (idx < 0 || idx >= clientCIs.size()) return -1;
        return clientCIs.get(idx);
    }
    public String getMeasurementDate() { return txtDate.getText().trim(); }
    public double getWeightKg()  { return parseDouble(txtWeightKg.getText()); }
    public double getBodyFat()   { return parseDouble(txtBodyFat.getText()); }
    public double getChestCm()   { return parseDouble(txtChestCm.getText()); }
    public double getWaistCm()   { return parseDouble(txtWaistCm.getText()); }
    public double getGlutes()    { return parseDouble(txtGlutes.getText()); }

    public int getSelectedId() {
        int row = tableMeasurements.getSelectedRow();
        if (row < 0) return 0;
        return Integer.parseInt(tableModel.getValueAt(row, 0).toString());
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }

    public JButton getBtnSave()   { return btnSave; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JComboBox<String> getCmbClient() { return cmbClient; }
}
