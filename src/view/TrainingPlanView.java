package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class TrainingPlanView extends JPanel {

    private JTextField txtPlanName  = new JTextField(20);
    private JTextField txtDate      = new JTextField(12);
    private JTextField txtObjective = new JTextField(30);
    private JComboBox<Object[]> cmbClient   = new JComboBox<>();
    private JComboBox<Object[]> cmbExercise = new JComboBox<>();
    private JTextField txtSets     = new JTextField(4);
    private JTextField txtReps     = new JTextField(4);
    private JTextField txtRestTime = new JTextField(5);

    private JTable tablePlans       = new JTable();
    private JTable tableExercises   = new JTable();  // ejercicios del plan en curso

    private JButton btnSave            = new JButton("Save Plan");
    private JButton btnUpdate          = new JButton("Update Plan");
    private JButton btnDelete          = new JButton("Delete Plan");
    private JButton btnAddExercise     = new JButton("Add Exercise");
    private JButton btnRemoveExercise  = new JButton("Remove Exercise");

    private int selectedPlanId = -1;

    // Modelo de la tabla de detalle (solo en memoria, pre-guardado)
    private DefaultTableModel detailTableModel = new DefaultTableModel(
        new String[]{"ExerciseId", "Exercise Name", "Sets", "Reps", "Rest(s)", "Order"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };

    public TrainingPlanView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Norte: Formulario del plan ---
        JPanel planForm = new JPanel(new GridBagLayout());
        planForm.setBorder(BorderFactory.createTitledBorder("Training Plan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        planForm.add(new JLabel("Plan Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        planForm.add(txtPlanName, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        planForm.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        planForm.add(txtDate, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        planForm.add(new JLabel("Objective:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        planForm.add(txtObjective, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        planForm.add(new JLabel("Client:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        planForm.add(cmbClient, gbc);

        // Sub-panel: agregar ejercicios al plan
        JPanel exPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        exPanel.setBorder(BorderFactory.createTitledBorder("Add Exercise to Plan"));
        exPanel.add(new JLabel("Exercise:")); exPanel.add(cmbExercise);
        exPanel.add(new JLabel("Sets:")); exPanel.add(txtSets);
        exPanel.add(new JLabel("Reps:")); exPanel.add(txtReps);
        exPanel.add(new JLabel("Rest(s):")); exPanel.add(txtRestTime);
        exPanel.add(btnAddExercise);
        exPanel.add(btnRemoveExercise);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        planForm.add(exPanel, gbc);

        add(planForm, BorderLayout.NORTH);

        // --- Panel Centro: split entre planes y detalle ---
        tableExercises.setModel(detailTableModel);
        tableExercises.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tablePlans.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePlans.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablePlans.getSelectedRow() >= 0) {
                int row = tablePlans.getSelectedRow();
                DefaultTableModel m = (DefaultTableModel) tablePlans.getModel();
                selectedPlanId = Integer.parseInt(m.getValueAt(row, 0).toString());
                txtPlanName.setText(m.getValueAt(row, 1).toString());
                txtDate.setText(m.getValueAt(row, 2).toString());
                txtObjective.setText(m.getValueAt(row, 3) != null ? m.getValueAt(row, 3).toString() : "");
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(tablePlans), new JScrollPane(tableExercises));
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        // --- Panel Sur: botones del plan ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnPanel.add(btnSave);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // --- Carga de combos desde el Controller ---
    public void loadClientOptions(ResultSet rs) {
        cmbClient.removeAllItems();
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

    public void loadExerciseOptions(ResultSet rs) {
        cmbExercise.removeAllItems();
        try {
            if (rs != null) {
                while (rs.next()) {
                    final int id = rs.getInt("id_exercise");
                    final String name = rs.getString("name");
                    cmbExercise.addItem(new Object[]{id, name});
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        cmbExercise.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value instanceof Object[]) lbl.setText(((Object[]) value)[1].toString());
            if (isSelected) { lbl.setBackground(list.getSelectionBackground()); lbl.setOpaque(true); }
            return lbl;
        });
    }

    // Agrega una fila al detalle en memoria (aun no guardado)
    public void addExerciseRow(int exerciseId, int sets, int reps, int restTime) {
        Object[] sel = (Object[]) cmbExercise.getSelectedItem();
        String exerciseName = (sel != null) ? sel[1].toString() : "";
        int order = detailTableModel.getRowCount() + 1;
        detailTableModel.addRow(new Object[]{exerciseId, exerciseName, sets, reps, restTime, order});
    }

    public void removeSelectedExerciseRow() {
        int row = tableExercises.getSelectedRow();
        if (row >= 0) detailTableModel.removeRow(row);
    }

    // Retorna int[][] con cada fila = [exerciseId, sets, reps, restTime, order]
    public int[][] getPlanDetailsData() {
        int rows = detailTableModel.getRowCount();
        int[][] data = new int[rows][5];
        for (int i = 0; i < rows; i++) {
            data[i][0] = Integer.parseInt(detailTableModel.getValueAt(i, 0).toString()); // exerciseId
            data[i][1] = Integer.parseInt(detailTableModel.getValueAt(i, 2).toString()); // sets
            data[i][2] = Integer.parseInt(detailTableModel.getValueAt(i, 3).toString()); // reps
            data[i][3] = Integer.parseInt(detailTableModel.getValueAt(i, 4).toString()); // restTime
            data[i][4] = Integer.parseInt(detailTableModel.getValueAt(i, 5).toString()); // order
        }
        return data;
    }

    // --- Getters para el Controller ---
    public String getPlanName()       { return txtPlanName.getText().trim(); }
    public String getDate()           { return txtDate.getText().trim(); }
    public String getObjective()      { return txtObjective.getText().trim(); }
    public int    getSelectedPlanId() { return selectedPlanId; }
    public int    getSets()           { return Integer.parseInt(txtSets.getText().trim()); }
    public int    getReps()           { return Integer.parseInt(txtReps.getText().trim()); }
    public int    getRestTime()       { return Integer.parseInt(txtRestTime.getText().trim()); }
    public int getClientCI() {
        Object[] sel = (Object[]) cmbClient.getSelectedItem();
        return (sel != null) ? (int) sel[0] : -1;
    }
    public int getSelectedExerciseId() {
        Object[] sel = (Object[]) cmbExercise.getSelectedItem();
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
            tablePlans.setModel(model);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void clearFields() {
        txtPlanName.setText("");
        txtDate.setText("");
        txtObjective.setText("");
        txtSets.setText("");
        txtReps.setText("");
        txtRestTime.setText("");
        detailTableModel.setRowCount(0);
        selectedPlanId = -1;
        tablePlans.clearSelection();
    }

    // --- Exponer botones para el Controller ---
    public JButton getBtnSave()           { return btnSave; }
    public JButton getBtnUpdate()         { return btnUpdate; }
    public JButton getBtnDelete()         { return btnDelete; }
    public JButton getBtnAddExercise()    { return btnAddExercise; }
    public JButton getBtnRemoveExercise() { return btnRemoveExercise; }
}
