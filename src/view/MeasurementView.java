package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MeasurementView extends JPanel {

    private JComboBox<String> cmbClient = new JComboBox<>();
    private ArrayList<Integer> clientCIs = new ArrayList<>();
    private ArrayList<Integer> rowClientCIs = new ArrayList<>();
    private boolean isUpdatingFromTable = false;
    
    private JTextField txtDate      = new JTextField();
    private JTextField txtWeightKg  = new JTextField();
    private JTextField txtBodyFat   = new JTextField();
    private JTextField txtChestCm   = new JTextField();
    private JTextField txtWaistCm   = new JTextField();
    private JTextField txtGlutes    = new JTextField();

    // PATRÓN STRATEGY: Componentes visuales para selección y ejecución de la estrategia
    private JComboBox<String> cmbEvaluationStrategy = new JComboBox<>(new String[]{
        "1. Diagnóstico Clínico OMS (IMC)",
        "2. Riesgo Cardiovascular (Cintura/Cadera)",
        "3. Composición Corporal (Masa Grasa / Magra)"
    });
    private JButton btnEvaluate = new JButton("Evaluar Medidas (Strategy)");
    private JTextArea txtDiagnosis = new JTextArea(3, 40);

    private JTable tableMeasurements;
    private DefaultTableModel tableModel;
    private JButton btnSave   = new JButton("Save");
    private JButton btnUpdate = new JButton("Update");
    private JButton btnDelete = new JButton("Delete");

    public MeasurementView() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(UITheme.CREAM);

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildFormPanel() {
        JPanel panel = UITheme.createSectionPanel("Measurement & Diagnostic Data");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        UITheme.styleComboBox(cmbClient);
        UITheme.styleComboBox(cmbEvaluationStrategy);

        // Dar tamaño preferido a los campos para que no colapsen
        Dimension fieldSize = new Dimension(120, 28);
        txtDate.setFont(UITheme.LABEL_FONT);     txtDate.setPreferredSize(fieldSize);
        txtWeightKg.setFont(UITheme.LABEL_FONT); txtWeightKg.setPreferredSize(fieldSize);
        txtBodyFat.setFont(UITheme.LABEL_FONT);  txtBodyFat.setPreferredSize(fieldSize);
        txtChestCm.setFont(UITheme.LABEL_FONT);  txtChestCm.setPreferredSize(fieldSize);
        txtWaistCm.setFont(UITheme.LABEL_FONT);  txtWaistCm.setPreferredSize(fieldSize);
        txtGlutes.setFont(UITheme.LABEL_FONT);   txtGlutes.setPreferredSize(fieldSize);

        // --- Fila 0: Client ---
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 1;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 10, 4, 6);
        panel.add(UITheme.styledLabel("Client:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 10);
        panel.add(cmbClient, gbc);

        gbc.gridwidth = 1;

        // --- Fila 1: Weight + Body Fat ---
        UITheme.addFormRow2(panel, gbc, 1, "Weight (kg):", txtWeightKg, "Body Fat (%):", txtBodyFat);
        // --- Fila 2: Chest + Glutes ---
        UITheme.addFormRow2(panel, gbc, 2, "Chest (cm):", txtChestCm, "Glutes (cm):", txtGlutes);
        // --- Fila 3: Waist + Date ---
        UITheme.addFormRow2(panel, gbc, 3, "Waist (cm):", txtWaistCm, "Date (yyyy-MM-dd):", txtDate);

        // --- Fila 4: PATRÓN STRATEGY (Selector de Estrategia + Botón Evaluar) ---
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 1;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 10, 4, 6);
        panel.add(UITheme.styledLabel("Estrategia:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 4, 6);
        panel.add(cmbEvaluationStrategy, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 0, 4, 10);
        btnEvaluate.setFont(UITheme.BUTTON_FONT);
        btnEvaluate.setBackground(new Color(40, 167, 69)); // Verde éxito
        btnEvaluate.setForeground(Color.WHITE);
        btnEvaluate.setFocusPainted(false);
        panel.add(btnEvaluate, gbc);

        // --- Fila 5: Área de Diagnóstico del Patrón Strategy ---
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 4;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 8, 10);
        txtDiagnosis.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtDiagnosis.setEditable(false);
        txtDiagnosis.setBackground(new Color(245, 247, 250));
        txtDiagnosis.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        txtDiagnosis.setText("Seleccione una medida o ingrese valores para ejecutar la estrategia de evaluación.");
        panel.add(txtDiagnosis, gbc);

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

                if (row < rowClientCIs.size()) {
                    setClientByCI(rowClientCIs.get(row));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tableMeasurements);
        scroll.getViewport().setBackground(UITheme.WHITE);
        scroll.setPreferredSize(new Dimension(0, 220));
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
        rowClientCIs.clear();
        try {
            while (data != null && data.next()) {
                rowClientCIs.add(data.getInt("ci_client"));
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

    public void setClientByCI(int ci) {
        isUpdatingFromTable = true;
        for (int i = 0; i < clientCIs.size(); i++) {
            if (clientCIs.get(i) == ci) {
                cmbClient.setSelectedIndex(i);
                break;
            }
        }
        isUpdatingFromTable = false;
    }

    public boolean isUpdatingFromTable() {
        return isUpdatingFromTable;
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Gym MVC", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showEvaluationResult(String text) {
        txtDiagnosis.setText(text);
    }

    public void clearFields() {
        txtDate.setText("");
        txtWeightKg.setText("");
        txtBodyFat.setText("");
        txtChestCm.setText("");
        txtWaistCm.setText("");
        txtGlutes.setText("");
        txtDiagnosis.setText("Seleccione una medida o ingrese valores para ejecutar la estrategia de evaluación.");
        tableMeasurements.clearSelection();
        if (cmbClient.getItemCount() > 0) {
            isUpdatingFromTable = true;
            cmbClient.setSelectedIndex(0);
            isUpdatingFromTable = false;
        }
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

    public int getSelectedEvaluationIndex() {
        return cmbEvaluationStrategy.getSelectedIndex();
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }

    public JButton getBtnSave()   { return btnSave; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnEvaluate() { return btnEvaluate; }
    public JComboBox<String> getCmbClient() { return cmbClient; }
    public JComboBox<String> getCmbEvaluationStrategy() { return cmbEvaluationStrategy; }
}
