package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ExerciseView extends JPanel {

    private JTextField txtName        = new JTextField(20);
    private JTextField txtDescription = new JTextField(30);
    private JTextField txtVideoUrl    = new JTextField(35);
    private JComboBox<String>   cmbDifficulty  = new JComboBox<>(new String[]{"Beginner","Intermediate","Advanced"});
    private JComboBox<String>   cmbMuscleGroup = new JComboBox<>();
    private JButton btnPreviewVideo = new JButton("Preview Video");

    // Mapa nombre -> id para getMuscleGroupId()
    private Map<String, Integer> muscleGroupIds = new HashMap<>();

    private DefaultTableModel tableModel;
    private JTable            tableExercises;

    private JButton btnSave   = new JButton("Save");
    private JButton btnUpdate = new JButton("Update");
    private JButton btnDelete = new JButton("Delete");

    public ExerciseView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.CREAM);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        add(buildFormPanel(),   BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    // --- FORMULARIO ---
    private JPanel buildFormPanel() {
        JPanel panel = UITheme.createSectionPanel("Exercise Data");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        txtName.setFont(UITheme.LABEL_FONT);
        txtDescription.setFont(UITheme.LABEL_FONT);
        txtVideoUrl.setFont(UITheme.LABEL_FONT);
        UITheme.styleComboBox(cmbDifficulty);
        UITheme.styleComboBox(cmbMuscleGroup);
        UITheme.styleSecondaryButton(btnPreviewVideo);

        // Fila 0: Name + Difficulty
        UITheme.addFormRow2(panel, gbc, 0, "Name:", txtName, "Difficulty:", cmbDifficulty);

        // Fila 1: Description (ancho completo)
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 1;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(6, 12, 6, 6);
        panel.add(UITheme.styledLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 12);
        panel.add(txtDescription, gbc);

        // Fila 2: Video URL + Preview button
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 1;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(6, 12, 6, 6);
        panel.add(UITheme.styledLabel("Video URL:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 8);
        panel.add(txtVideoUrl, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(6, 0, 6, 12);
        panel.add(btnPreviewVideo, gbc);

        // Fila 3: Muscle Group
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 1;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(6, 12, 6, 6);
        panel.add(UITheme.styledLabel("Muscle Group:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 12);
        panel.add(cmbMuscleGroup, gbc);
        gbc.gridwidth = 1;

        return panel;
    }

    // --- TABLA ---
    private JScrollPane buildTablePanel() {
        String[] cols = {"ID", "Name", "Muscle Group", "Difficulty", "Video URL"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableExercises = new JTable(tableModel);
        UITheme.styleTable(tableExercises);

        tableExercises.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableExercises.getSelectedRow() >= 0) {
                int row = tableExercises.getSelectedRow();
                txtName.setText(tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "");
                // col 2 = muscle_group_name, col 3 = difficulty, col 4 = video_url
                String mgName = tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "";
                cmbMuscleGroup.setSelectedItem(mgName);
                String diff = tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "Beginner";
                cmbDifficulty.setSelectedItem(diff);
                txtVideoUrl.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
            }
        });

        JScrollPane scroll = new JScrollPane(tableExercises);
        scroll.getViewport().setBackground(UITheme.WHITE);
        scroll.setPreferredSize(new Dimension(0, 280));
        return scroll;
    }

    // --- BOTONES ---
    private JPanel buildButtonPanel() {
        UITheme.styleSaveButton(btnSave);
        UITheme.styleUpdateButton(btnUpdate);
        UITheme.styleDeleteButton(btnDelete);
        return UITheme.createButtonPanel(btnSave, btnUpdate, btnDelete);
    }

    // --- CARGA DE OPCIONES ---
    public void loadMuscleGroupOptions(ResultSet rs) {
        cmbMuscleGroup.removeAllItems();
        muscleGroupIds.clear();
        cmbMuscleGroup.addItem("-- Select --");
        try {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("id_muscle_group");
                    String name = rs.getString("name");
                    muscleGroupIds.put(name, id);
                    cmbMuscleGroup.addItem(name);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- ABRIR VIDEO ---
    public void openVideo(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            showMessage("Cannot open: " + url);
        }
    }

    // --- MÉTODOS DEL CONTROLLER ---
    public void showList(ResultSet data) {
        tableModel.setRowCount(0);
        try {
            while (data != null && data.next()) {
                tableModel.addRow(new Object[]{
                    data.getInt("id_exercise"),
                    data.getString("name"),
                    data.getString("muscle_group_name"),
                    data.getString("difficulty"),
                    data.getString("video_url")
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
        txtVideoUrl.setText("");
        cmbDifficulty.setSelectedIndex(0);
        if (cmbMuscleGroup.getItemCount() > 0) cmbMuscleGroup.setSelectedIndex(0);
        tableExercises.clearSelection();
    }

    // --- GETTERS ---
    public String getName()        { return txtName.getText().trim(); }
    public String getDescription() { return txtDescription.getText().trim(); }
    public String getVideoUrl()    { return txtVideoUrl.getText().trim(); }
    public String getDifficulty()  { return (String) cmbDifficulty.getSelectedItem(); }

    public int getMuscleGroupId() {
        String selected = (String) cmbMuscleGroup.getSelectedItem();
        if (selected == null || !muscleGroupIds.containsKey(selected)) return -1;
        return muscleGroupIds.get(selected);
    }

    public int getSelectedId() {
        int row = tableExercises.getSelectedRow();
        if (row < 0) return 0;
        return Integer.parseInt(tableModel.getValueAt(row, 0).toString());
    }

    // --- EXPONER BOTONES AL CONTROLLER ---
    public JButton getBtnSave()         { return btnSave; }
    public JButton getBtnUpdate()       { return btnUpdate; }
    public JButton getBtnDelete()       { return btnDelete; }
    public JButton getBtnPreviewVideo() { return btnPreviewVideo; }
}
