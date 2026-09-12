package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class ExerciseView extends JPanel {

    private JTextField txtName        = new JTextField(25);
    private JTextField txtDescription = new JTextField(35);
    private JTextField txtVideoUrl    = new JTextField(35);
    private JComboBox<String> cmbDifficulty = new JComboBox<>(
        new String[]{"Beginner", "Intermediate", "Advanced"});
    private JComboBox<Object[]> cmbMuscleGroup = new JComboBox<>();

    private JTable  tableExercises  = new JTable();
    private JButton btnSave         = new JButton("Save");
    private JButton btnUpdate       = new JButton("Update");
    private JButton btnDelete       = new JButton("Delete");
    private JButton btnPreviewVideo = new JButton("Preview Video");

    private int selectedId = -1;

    public ExerciseView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Norte: Formulario ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Exercise Data"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtName, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Difficulty:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbDifficulty, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtDescription, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Video URL:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtVideoUrl, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(btnPreviewVideo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Muscle Group:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbMuscleGroup, gbc);

        add(formPanel, BorderLayout.NORTH);

        // --- Panel Centro: Tabla ---
        tableExercises.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableExercises.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableExercises.getSelectedRow() >= 0) {
                int row = tableExercises.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) tableExercises.getModel();
                selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
                txtName.setText(model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "");
                txtDescription.setText(model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "");
                txtVideoUrl.setText(model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "");
                if (model.getValueAt(row, 4) != null) cmbDifficulty.setSelectedItem(model.getValueAt(row, 4).toString());
            }
        });
        add(new JScrollPane(tableExercises), BorderLayout.CENTER);

        // --- Panel Sur: Botones ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnPanel.add(btnSave);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // --- Carga de opciones desde el Controller ---
    public void loadMuscleGroupOptions(ResultSet rs) {
        cmbMuscleGroup.removeAllItems();
        try {
            if (rs != null) {
                while (rs.next()) {
                    final int id = rs.getInt("id_muscle_group");
                    final String name = rs.getString("name");
                    cmbMuscleGroup.addItem(new Object[]{id, name});
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        cmbMuscleGroup.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value instanceof Object[]) lbl.setText(((Object[]) value)[1].toString());
            if (isSelected) { lbl.setBackground(list.getSelectionBackground()); lbl.setOpaque(true); }
            return lbl;
        });
    }

    // --- Abrir video en navegador ---
    public void openVideo(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot open video: " + url);
        }
    }

    // --- Getters para el Controller ---
    public String getName()        { return txtName.getText().trim(); }
    public String getDescription() { return txtDescription.getText().trim(); }
    public String getVideoUrl()    { return txtVideoUrl.getText().trim(); }
    public String getDifficulty()  { return (String) cmbDifficulty.getSelectedItem(); }
    public int    getSelectedId()  { return selectedId; }
    public int getMuscleGroupId() {
        Object[] sel = (Object[]) cmbMuscleGroup.getSelectedItem();
        return (sel != null) ? (int) sel[0] : -1;
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
            tableExercises.setModel(model);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void clearFields() {
        txtName.setText("");
        txtDescription.setText("");
        txtVideoUrl.setText("");
        cmbDifficulty.setSelectedIndex(0);
        if (cmbMuscleGroup.getItemCount() > 0) cmbMuscleGroup.setSelectedIndex(0);
        selectedId = -1;
        tableExercises.clearSelection();
    }

    // --- Exponer botones para el Controller ---
    public JButton getBtnSave()         { return btnSave; }
    public JButton getBtnUpdate()       { return btnUpdate; }
    public JButton getBtnDelete()       { return btnDelete; }
    public JButton getBtnPreviewVideo() { return btnPreviewVideo; }
}
