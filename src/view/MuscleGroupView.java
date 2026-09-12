package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class MuscleGroupView extends JPanel {

    private JTextField txtName        = new JTextField(25);
    private JTextField txtDescription = new JTextField(40);

    private JTable  tableMuscleGroups = new JTable();
    private JButton btnSave           = new JButton("Save");
    private JButton btnUpdate         = new JButton("Update");
    private JButton btnDelete         = new JButton("Delete");

    private int selectedId = -1;

    public MuscleGroupView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Norte: Formulario ---
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Muscle Group Data"));
        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(txtDescription);
        add(formPanel, BorderLayout.NORTH);

        // --- Panel Centro: Tabla ---
        tableMuscleGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableMuscleGroups.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableMuscleGroups.getSelectedRow() >= 0) {
                int row = tableMuscleGroups.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) tableMuscleGroups.getModel();
                selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
                txtName.setText(model.getValueAt(row, 1).toString());
                txtDescription.setText(model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "");
            }
        });
        add(new JScrollPane(tableMuscleGroups), BorderLayout.CENTER);

        // --- Panel Sur: Botones ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnPanel.add(btnSave);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // --- Getters para el Controller ---
    public String getName()        { return txtName.getText().trim(); }
    public String getDescription() { return txtDescription.getText().trim(); }
    public int    getSelectedId()  { return selectedId; }

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
            tableMuscleGroups.setModel(model);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void clearFields() {
        txtName.setText("");
        txtDescription.setText("");
        selectedId = -1;
        tableMuscleGroups.clearSelection();
    }

    // --- Exponer botones para el Controller ---
    public JButton getBtnSave()   { return btnSave; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
}
