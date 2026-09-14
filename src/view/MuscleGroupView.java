package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;

public class MuscleGroupView extends JPanel {

    private JTextField txtName        = new JTextField(25);
    private JTextField txtDescription = new JTextField(35);

    private DefaultTableModel tableModel;
    private JTable            tableMuscleGroups;

    private JButton btnSave   = new JButton("Save");
    private JButton btnUpdate = new JButton("Update");
    private JButton btnDelete = new JButton("Delete");

    public MuscleGroupView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.CREAM);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        add(buildFormPanel(),   BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    // --- FORMULARIO ---
    private JPanel buildFormPanel() {
        JPanel panel = UITheme.createSectionPanel("Muscle Group Data");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        txtName.setFont(UITheme.LABEL_FONT);
        txtDescription.setFont(UITheme.LABEL_FONT);

        UITheme.addFormRow2(panel, gbc, 0, "Name:", txtName, "Description:", txtDescription);

        return panel;
    }

    // --- TABLA ---
    private JScrollPane buildTablePanel() {
        String[] cols = {"ID", "Name", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableMuscleGroups = new JTable(tableModel);
        UITheme.styleTable(tableMuscleGroups);

        tableMuscleGroups.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableMuscleGroups.getSelectedRow() >= 0) {
                int row = tableMuscleGroups.getSelectedRow();
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtDescription.setText(tableModel.getValueAt(row, 2) != null
                    ? tableModel.getValueAt(row, 2).toString() : "");
            }
        });

        JScrollPane scroll = new JScrollPane(tableMuscleGroups);
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
                    data.getInt("id_muscle_group"),
                    data.getString("name"),
                    data.getString("description")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Gym MVC", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearFields() {
        txtName.setText("");
        txtDescription.setText("");
        tableMuscleGroups.clearSelection();
    }

    // --- GETTERS PARA EL CONTROLLER ---
    public String getName()        { return txtName.getText().trim(); }
    public String getDescription() { return txtDescription.getText().trim(); }

    public int getSelectedId() {
        int row = tableMuscleGroups.getSelectedRow();
        if (row < 0) return 0;
        return Integer.parseInt(tableModel.getValueAt(row, 0).toString());
    }

    // --- EXPONER BOTONES AL CONTROLLER ---
    public JButton getBtnSave()   { return btnSave; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
}
