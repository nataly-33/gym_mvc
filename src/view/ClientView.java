package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;

public class ClientView extends JPanel {

    // IMPORTANTE: inicializar con columnas para que tengan tamaño visible
    private JTextField txtCI        = new JTextField(10);
    private JTextField txtFirstName = new JTextField(18);
    private JTextField txtLastName  = new JTextField(18);
    private JTextField txtPhone     = new JTextField(14);
    private JTextField txtAddress   = new JTextField(35);

    private DefaultTableModel tableModel;
    private JTable            tableClients;

    private JButton btnSave   = new JButton("Save");
    private JButton btnUpdate = new JButton("Update");
    private JButton btnDelete = new JButton("Delete");

    public ClientView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.CREAM);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        add(buildFormPanel(),   BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    // --- FORMULARIO ---
    private JPanel buildFormPanel() {
        JPanel panel = UITheme.createSectionPanel("Client Data");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Fuente para los campos
        Font fieldFont = UITheme.LABEL_FONT;
        txtCI.setFont(fieldFont);
        txtFirstName.setFont(fieldFont);
        txtLastName.setFont(fieldFont);
        txtPhone.setFont(fieldFont);
        txtAddress.setFont(fieldFont);

        // Fila 0: CI y First Name
        UITheme.addFormRow2(panel, gbc, 0, "CI:", txtCI, "First Name:", txtFirstName);
        // Fila 1: Last Name y Phone
        UITheme.addFormRow2(panel, gbc, 1, "Last Name:", txtLastName, "Phone:", txtPhone);
        // Fila 2: Address (ocupa todo el ancho)
        gbc.gridy = 2; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(6, 12, 6, 6);
        panel.add(UITheme.styledLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 12);
        panel.add(txtAddress, gbc);
        gbc.gridwidth = 1; // restaurar

        return panel;
    }

    // --- TABLA ---
    private JScrollPane buildTablePanel() {
        String[] cols = {"CI", "First Name", "Last Name", "Phone", "Address"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableClients = new JTable(tableModel);
        UITheme.styleTable(tableClients);

        // Click en fila llena el formulario automáticamente
        tableClients.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableClients.getSelectedRow() >= 0) {
                int row = tableClients.getSelectedRow();
                txtCI.setText(tableModel.getValueAt(row, 0).toString());
                txtFirstName.setText(tableModel.getValueAt(row, 1).toString());
                txtLastName.setText(tableModel.getValueAt(row, 2).toString());
                txtPhone.setText(tableModel.getValueAt(row, 3) != null
                    ? tableModel.getValueAt(row, 3).toString() : "");
                txtAddress.setText(tableModel.getValueAt(row, 4) != null
                    ? tableModel.getValueAt(row, 4).toString() : "");
            }
        });

        JScrollPane scroll = new JScrollPane(tableClients);
        scroll.getViewport().setBackground(UITheme.WHITE);
        scroll.setPreferredSize(new Dimension(0, 300));
        return scroll;
    }

    // --- BOTONES ---
    private JPanel buildButtonPanel() {
        UITheme.styleSaveButton(btnSave);
        UITheme.styleUpdateButton(btnUpdate);
        UITheme.styleDeleteButton(btnDelete);
        return UITheme.createButtonPanel(btnSave, btnUpdate, btnDelete);
    }

    // --- MÉTODOS LLAMADOS POR EL CONTROLLER ---
    public void showList(ResultSet data) {
        tableModel.setRowCount(0);
        try {
            while (data != null && data.next()) {
                tableModel.addRow(new Object[]{
                    data.getInt("ci"),
                    data.getString("first_name"),
                    data.getString("last_name"),
                    data.getString("phone"),
                    data.getString("address")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Gym MVC", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearFields() {
        txtCI.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        tableClients.clearSelection();
    }

    // --- GETTERS PARA EL CONTROLLER ---
    public int getCI() {
        try { return Integer.parseInt(txtCI.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }
    public String getFirstName() { return txtFirstName.getText().trim(); }
    public String getLastName()  { return txtLastName.getText().trim(); }
    public String getPhone()     { return txtPhone.getText().trim(); }
    public String getAddress()   { return txtAddress.getText().trim(); }

    public int getSelectedCI() {
        int row = tableClients.getSelectedRow();
        if (row < 0) return 0;
        return Integer.parseInt(tableModel.getValueAt(row, 0).toString());
    }

    // --- EXPONER BOTONES AL CONTROLLER ---
    public JButton getBtnSave()   { return btnSave; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
}
