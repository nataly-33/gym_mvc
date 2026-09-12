package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class ClientView extends JPanel {

    private JTextField txtCI        = new JTextField(10);
    private JTextField txtFirstName = new JTextField(20);
    private JTextField txtLastName  = new JTextField(20);
    private JTextField txtPhone     = new JTextField(15);
    private JTextField txtAddress   = new JTextField(30);

    private JTable  tableClients = new JTable();
    private JButton btnSave      = new JButton("Save");
    private JButton btnUpdate    = new JButton("Update");
    private JButton btnDelete    = new JButton("Delete");

    private int selectedCI = -1;

    public ClientView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Norte: Formulario ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Client Data"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"CI:", "First Name:", "Last Name:", "Phone:", "Address:"};
        JTextField[] fields = {txtCI, txtFirstName, txtLastName, txtPhone, txtAddress};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = (i % 2) * 2;
            gbc.gridy = i / 2;
            gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = (i % 2) * 2 + 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            formPanel.add(fields[i], gbc);
        }
        add(formPanel, BorderLayout.NORTH);

        // --- Panel Centro: Tabla ---
        tableClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableClients.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableClients.getSelectedRow() >= 0) {
                int row = tableClients.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) tableClients.getModel();
                selectedCI = Integer.parseInt(model.getValueAt(row, 0).toString());
                txtCI.setText(model.getValueAt(row, 0).toString());
                txtFirstName.setText(model.getValueAt(row, 1).toString());
                txtLastName.setText(model.getValueAt(row, 2).toString());
                txtPhone.setText(model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "");
                txtAddress.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
            }
        });
        add(new JScrollPane(tableClients), BorderLayout.CENTER);

        // --- Panel Sur: Botones ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnPanel.add(btnSave);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // --- Getters para el Controller ---
    public int    getCI()          { return Integer.parseInt(txtCI.getText().trim()); }
    public String getFirstName()   { return txtFirstName.getText().trim(); }
    public String getLastName()    { return txtLastName.getText().trim(); }
    public String getPhone()       { return txtPhone.getText().trim(); }
    public String getAddress()     { return txtAddress.getText().trim(); }
    public int    getSelectedCI()  { return selectedCI; }

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
            tableClients.setModel(model);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void clearFields() {
        txtCI.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        selectedCI = -1;
        tableClients.clearSelection();
    }

    // --- Exponer botones para el Controller ---
    public JButton getBtnSave()   { return btnSave; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
}
