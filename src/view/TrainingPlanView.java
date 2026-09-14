package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TrainingPlanView extends JPanel {

    // --- Campos del formulario ---
    private JTextField txtPlanName  = new JTextField(22);
    private JTextField txtDate      = new JTextField(12);
    private JComboBox<String> cmbClient = new JComboBox<>();
    private JTextField txtObjective = new JTextField(35);

    // --- Filas de ejercicios (el "carrito") ---
    private List<ExerciseItem>     exerciseItems = new ArrayList<>();
    private List<ExerciseRowPanel> exerciseRows  = new ArrayList<>();
    private JPanel                 rowsContainer;
    private JScrollPane            rowsScroll;

    // --- Tabla de planes guardados (derecha) ---
    private DefaultTableModel savedPlansModel;
    private JTable            tableSavedPlans;

    // --- Mapeo clienteCI por índice del combo ---
    private List<Integer> clientCiList = new ArrayList<>();

    // --- Botones ---
    private JButton btnAdd    = new JButton("Add ▼");
    private JButton btnRemove = new JButton("✕ Remove Last");
    private JButton btnSave   = new JButton("Save Plan");
    private JButton btnUpdate = new JButton("Update Plan");
    private JButton btnDelete = new JButton("Delete Plan");

    // --- Estado: plan seleccionado ---
    private int selectedPlanId = -1;

    public TrainingPlanView() {
        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.CREAM);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 10, 12));

        // Estilo de campos
        txtPlanName.setFont(UITheme.LABEL_FONT);
        txtDate.setFont(UITheme.LABEL_FONT);
        txtObjective.setFont(UITheme.LABEL_FONT);
        UITheme.styleComboBox(cmbClient);

        // Estilo de botones
        UITheme.styleSecondaryButton(btnAdd);
        UITheme.styleSecondaryButton(btnRemove);
        UITheme.styleSaveButton(btnSave);
        UITheme.styleUpdateButton(btnUpdate);
        UITheme.styleDeleteButton(btnDelete);

        // Split: 70% izquierda (formulario + filas), 30% derecha (planes guardados)
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            buildLeftPanel(),
            buildRightPanel());
        split.setResizeWeight(0.72);
        split.setDividerSize(6);
        split.setBackground(UITheme.CREAM);

        add(split, BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    // =========================================================
    // PANEL IZQUIERDO: formulario + filas de ejercicios
    // =========================================================
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.CREAM);

        // --- Formulario superior ---
        JPanel formPanel = UITheme.createSectionPanel("Training Plan");
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        UITheme.addFormRow2(formPanel, gbc, 0, "Plan Name:", txtPlanName, "Date (yyyy-MM-dd):", txtDate);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 1;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 12, 6, 6);
        formPanel.add(UITheme.styledLabel("Client:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 12);
        cmbClient.setPreferredSize(new Dimension(0, 32));
        formPanel.add(cmbClient, gbc);
        gbc.gridwidth = 1;

        UITheme.addFormRow(formPanel, gbc, 2, "Objective:", txtObjective);

        // --- Botones Add/Remove ---
        JPanel addRemovePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        addRemovePanel.setBackground(UITheme.CREAM);
        addRemovePanel.add(btnAdd);
        addRemovePanel.add(btnRemove);
        JLabel hint = UITheme.styledLabel("← Select exercise, fill Sets/Reps/Rest, then Add");
        hint.setForeground(new Color(0x7A6A30));
        addRemovePanel.add(hint);

        // --- Contenedor de filas dinámicas ---
        rowsContainer = new JPanel();
        rowsContainer.setLayout(new BoxLayout(rowsContainer, BoxLayout.Y_AXIS));
        rowsContainer.setBackground(UITheme.CREAM);

        rowsScroll = new JScrollPane(rowsContainer);
        rowsScroll.setBackground(UITheme.CREAM);
        rowsScroll.getViewport().setBackground(UITheme.CREAM);
        rowsScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UITheme.GOLD, 1),
            "Exercise Details (unsaved)",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            UITheme.LABEL_FONT, UITheme.TEXT));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 4));
        centerPanel.setBackground(UITheme.CREAM);
        centerPanel.add(addRemovePanel, BorderLayout.NORTH);
        centerPanel.add(rowsScroll, BorderLayout.CENTER);

        panel.add(formPanel,   BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // PANEL DERECHO: tabla de planes guardados
    // =========================================================
    private JPanel buildRightPanel() {
        JPanel panel = UITheme.createSectionPanel("Saved Plans");
        panel.setLayout(new BorderLayout());

        // Columnas basadas en el schema: id_plan, plan_name, date, ci_client → client_name
        String[] cols = {"ID", "Plan Name", "Date", "Client"};
        savedPlansModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableSavedPlans = new JTable(savedPlansModel);
        UITheme.styleTable(tableSavedPlans);
        tableSavedPlans.getColumnModel().getColumn(0).setPreferredWidth(30);
        tableSavedPlans.getColumnModel().getColumn(1).setPreferredWidth(120);

        JScrollPane scroll = new JScrollPane(tableSavedPlans);
        scroll.getViewport().setBackground(UITheme.WHITE);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // BOTONES INFERIORES
    // =========================================================
    private JPanel buildButtonPanel() {
        return UITheme.createButtonPanel(btnSave, btnUpdate, btnDelete);
    }

    // =========================================================
    // MÉTODOS LLAMADOS POR EL CONTROLLER
    // =========================================================

    public void showList(ResultSet data) {
        savedPlansModel.setRowCount(0);
        try {
            while (data != null && data.next()) {
                savedPlansModel.addRow(new Object[]{
                    data.getInt("id_plan"),
                    data.getString("plan_name"),
                    data.getString("date"),
                    data.getString("client_name")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Gym MVC", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearFields() {
        txtPlanName.setText("");
        txtDate.setText("");
        txtObjective.setText("");
        if (cmbClient.getItemCount() > 0) cmbClient.setSelectedIndex(0);
        clearExerciseRows();
        tableSavedPlans.clearSelection();
        selectedPlanId = -1;
    }

    // Cargar datos de un plan existente en el formulario
    // Usa columnas del schema: id_plan, plan_name, date, objective, ci_client
    public void loadPlanForm(ResultSet planData) {
        try {
            if (planData != null && planData.next()) {
                selectedPlanId = planData.getInt("id_plan");
                txtPlanName.setText(planData.getString("plan_name"));
                txtDate.setText(planData.getString("date"));
                String obj = planData.getString("objective");
                txtObjective.setText(obj != null ? obj : "");
                int ci = planData.getInt("ci_client");
                for (int i = 0; i < clientCiList.size(); i++) {
                    if (clientCiList.get(i) == ci) {
                        cmbClient.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Cargar los detalles del plan en las filas dinámicas
    // Usa columnas del schema: id_exercise, sets, reps, rest_time
    public void loadDetailRows(ResultSet detailData) {
        clearExerciseRows();
        try {
            while (detailData != null && detailData.next()) {
                addExerciseRow();
                ExerciseRowPanel row = exerciseRows.get(exerciseRows.size() - 1);
                row.loadData(
                    detailData.getInt("id_exercise"),
                    detailData.getInt("sets"),
                    detailData.getInt("reps"),
                    detailData.getInt("rest_time")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Columnas usadas del schema Client: ci, first_name, last_name
    public void loadClientOptions(ResultSet data) {
        cmbClient.removeAllItems();
        clientCiList.clear();
        try {
            while (data != null && data.next()) {
                int ci = data.getInt("ci");
                String display = ci + " - " + data.getString("first_name")
                               + " " + data.getString("last_name");
                cmbClient.addItem(display);
                clientCiList.add(ci);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Columnas usadas del schema Exercise: id_exercise, name + MuscleGroup.name
    public void loadExerciseOptions(ResultSet data) {
        exerciseItems.clear();
        try {
            while (data != null && data.next()) {
                exerciseItems.add(new ExerciseItem(
                    data.getInt("id_exercise"),
                    data.getString("name") + " (" + data.getString("muscle_group_name") + ")"
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        // Actualizar combos de filas existentes
        for (ExerciseRowPanel row : exerciseRows) {
            ExerciseItem selected = (ExerciseItem) row.cmbExercise.getSelectedItem();
            row.cmbExercise.removeAllItems();
            for (ExerciseItem item : exerciseItems) row.cmbExercise.addItem(item);
            if (selected != null) {
                for (int i = 0; i < row.cmbExercise.getItemCount(); i++) {
                    if (row.cmbExercise.getItemAt(i).id == selected.id) {
                        row.cmbExercise.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    // Agregar una nueva fila vacía
    public void addExerciseRow() {
        ExerciseRowPanel row = new ExerciseRowPanel(exerciseRows.size() + 1, exerciseItems);
        exerciseRows.add(row);
        rowsContainer.add(row);
        rowsContainer.revalidate();
        rowsContainer.repaint();
    }

    // Eliminar la última fila
    public void removeLastExerciseRow() {
        if (!exerciseRows.isEmpty()) {
            ExerciseRowPanel last = exerciseRows.remove(exerciseRows.size() - 1);
            rowsContainer.remove(last);
            rowsContainer.revalidate();
            rowsContainer.repaint();
        }
    }

    private void clearExerciseRows() {
        exerciseRows.clear();
        rowsContainer.removeAll();
        rowsContainer.revalidate();
        rowsContainer.repaint();
    }

    // =========================================================
    // GETTERS PARA EL CONTROLLER
    // =========================================================

    public String getPlanName()  { return txtPlanName.getText().trim(); }
    public String getDate()      { return txtDate.getText().trim(); }
    public String getObjective() { return txtObjective.getText().trim(); }

    public int getClientCI() {
        int idx = cmbClient.getSelectedIndex();
        return (idx >= 0 && idx < clientCiList.size()) ? clientCiList.get(idx) : 0;
    }

    public int getSelectedPlanId() { return selectedPlanId; }

    // Retorna int[][] con [exerciseId, sets, reps, restTime, order] por cada fila
    // Mapeado a columnas del schema: id_exercise, sets, reps, rest_time, exercise_order
    public int[][] getPlanDetailsData() {
        int[][] data = new int[exerciseRows.size()][5];
        for (int i = 0; i < exerciseRows.size(); i++) {
            ExerciseRowPanel row = exerciseRows.get(i);
            data[i][0] = row.getExerciseId(); // id_exercise
            data[i][1] = row.getSets();       // sets
            data[i][2] = row.getReps();       // reps
            data[i][3] = row.getRestTime();   // rest_time
            data[i][4] = i + 1;               // exercise_order
        }
        return data;
    }

    // =========================================================
    // EXPONER BOTONES Y TABLA AL CONTROLLER
    // =========================================================
    public JButton getBtnSave()   { return btnSave; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnAdd()    { return btnAdd; }
    public JButton getBtnRemove() { return btnRemove; }
    public JTable  getTableSavedPlans() { return tableSavedPlans; }

    // =========================================================
    // INNER CLASSES
    // =========================================================

    private static class ExerciseItem {
        final int    id;
        final String display;
        ExerciseItem(int id, String display) {
            this.id      = id;
            this.display = display;
        }
        @Override public String toString() { return display; }
    }

    private class ExerciseRowPanel extends JPanel {
        JComboBox<ExerciseItem> cmbExercise = new JComboBox<>();
        JTextField txtSets = new JTextField(5);
        JTextField txtReps = new JTextField(5);
        JTextField txtRest = new JTextField(5);

        ExerciseRowPanel(int rowNum, List<ExerciseItem> items) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
            setBackground(UITheme.CREAM);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            JLabel numLabel = UITheme.styledLabel("#" + rowNum);
            numLabel.setPreferredSize(new Dimension(28, 28));
            cmbExercise.setPreferredSize(new Dimension(240, 32));
            UITheme.styleComboBox(cmbExercise);
            for (ExerciseItem item : items) cmbExercise.addItem(item);
            txtSets.setFont(UITheme.LABEL_FONT);
            txtReps.setFont(UITheme.LABEL_FONT);
            txtRest.setFont(UITheme.LABEL_FONT);
            add(numLabel);
            add(cmbExercise);
            add(UITheme.styledLabel("Sets:"));    add(txtSets);
            add(UITheme.styledLabel("Reps:"));    add(txtReps);
            add(UITheme.styledLabel("Rest(s):")); add(txtRest);
        }

        // Pre-llenar al cargar plan existente
        // exerciseId = id_exercise, rest = rest_time (schema)
        void loadData(int exerciseId, int sets, int reps, int rest) {
            for (int i = 0; i < cmbExercise.getItemCount(); i++) {
                if (cmbExercise.getItemAt(i).id == exerciseId) {
                    cmbExercise.setSelectedIndex(i);
                    break;
                }
            }
            txtSets.setText(String.valueOf(sets));
            txtReps.setText(String.valueOf(reps));
            txtRest.setText(String.valueOf(rest));
        }

        int getExerciseId() {
            ExerciseItem it = (ExerciseItem) cmbExercise.getSelectedItem();
            return it != null ? it.id : -1;
        }
        int getSets()     { try { return Integer.parseInt(txtSets.getText().trim()); } catch (Exception e) { return 0; } }
        int getReps()     { try { return Integer.parseInt(txtReps.getText().trim()); } catch (Exception e) { return 0; } }
        int getRestTime() { try { return Integer.parseInt(txtRest.getText().trim()); } catch (Exception e) { return 0; } }
    }
}
