package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TrainingPlanView extends JPanel {

    private JComboBox<String> cmbClient   = new JComboBox<>();
    private JTextField txtPlanName  = new JTextField(25);
    private JTextField txtDate      = new JTextField(12);
    private JTextField txtObjective = new JTextField(35);
    private JTextField txtSets      = new JTextField(5);
    private JTextField txtReps      = new JTextField(5);
    private JTextField txtRestTime  = new JTextField(5);

    // Maps CI -> index and exercise id -> index
    private List<Integer> clientCIs     = new ArrayList<>();
    private List<Integer> exerciseIds   = new ArrayList<>();

    // Tabla de ejercicios disponibles
    private DefaultTableModel availableModel;
    private JTable tableAvailableExercises;

    // Tabla de detalles del plan actual (en memoria)
    private DefaultTableModel detailModel;
    private JTable tablePlanDetails;

    // Tabla de planes guardados en BD
    private DefaultTableModel plansModel;
    private JTable tableTrainingPlans;

    // Panel de ejercicios disponibles (necesario para el split del constructor)
    private JPanel availablePanel;

    private JButton btnSave           = new JButton("Save Plan");
    private JButton btnUpdate         = new JButton("Update Plan");
    private JButton btnDelete         = new JButton("Delete Plan");
    private JButton btnAddExercise    = new JButton("Add →");
    private JButton btnRemoveExercise = new JButton("← Remove");

    public TrainingPlanView() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.CREAM);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel centerPanel = buildCenterPanel();

        // Split horizontal arriba: formulario (65%) | Available Exercises (35%)
        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildFormPanel(), availablePanel);
        topSplit.setResizeWeight(0.65);   // 65% formulario, 35% ejercicios disponibles
        topSplit.setDividerSize(6);
        topSplit.setContinuousLayout(true);
        topSplit.setBackground(UITheme.CREAM);
        topSplit.setBorder(null);

        // Split vertical: [form+available arriba] | [plans+details abajo]
        // Esto permite que el usuario arrastre el divisor y achique la parte de arriba
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            topSplit, centerPanel);
        mainSplit.setResizeWeight(0.45);  // ~45% arriba, ~55% abajo por defecto
        mainSplit.setDividerSize(7);
        mainSplit.setContinuousLayout(true);
        mainSplit.setBackground(UITheme.CREAM);
        mainSplit.setBorder(null);

        add(mainSplit,          BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    // --- FORMULARIO ---
    private JPanel buildFormPanel() {
        JPanel panel = UITheme.createSectionPanel("Training Plan");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        txtPlanName.setFont(UITheme.LABEL_FONT);
        txtDate.setFont(UITheme.LABEL_FONT);
        txtObjective.setFont(UITheme.LABEL_FONT);
        txtSets.setFont(UITheme.LABEL_FONT);
        txtReps.setFont(UITheme.LABEL_FONT);
        txtRestTime.setFont(UITheme.LABEL_FONT);
        UITheme.styleComboBox(cmbClient);

        // Fila 0: Plan Name + Date
        UITheme.addFormRow2(panel, gbc, 0, "Plan Name:", txtPlanName, "Date (yyyy-MM-dd):", txtDate);
        // Fila 1: Client (full width)
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 1;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(6, 12, 6, 6);
        panel.add(UITheme.styledLabel("Client:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 12);
        panel.add(cmbClient, gbc);
        gbc.gridwidth = 1;
        // Fila 2: Objective (full width)
        gbc.gridy = 2; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(6, 12, 6, 6);
        panel.add(UITheme.styledLabel("Objective:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 12);
        panel.add(txtObjective, gbc);
        gbc.gridwidth = 1;

        // Sub-panel de agregar ejercicio
        JPanel exPanel = UITheme.createSectionPanel("Add Exercise to Plan");
        exPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
        exPanel.add(UITheme.styledLabel("Sets:")); exPanel.add(txtSets);
        exPanel.add(UITheme.styledLabel("Reps:")); exPanel.add(txtReps);
        exPanel.add(UITheme.styledLabel("Rest(s):")); exPanel.add(txtRestTime);
        UITheme.styleSecondaryButton(btnAddExercise);
        UITheme.styleSecondaryButton(btnRemoveExercise);
        exPanel.add(btnAddExercise);
        exPanel.add(btnRemoveExercise);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 4;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        panel.add(exPanel, gbc);
        gbc.gridwidth = 1;

        return panel;
    }

    // --- PANEL CENTRAL con nuevo layout ---
    private JPanel buildCenterPanel() {
        // Tabla de ejercicios disponibles
        availableModel = new DefaultTableModel(new String[]{"ID","Exercise","Muscle Group"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableAvailableExercises = new JTable(availableModel);
        UITheme.styleTable(tableAvailableExercises);
        tableAvailableExercises.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Tabla de detalles del plan (en memoria)
        detailModel = new DefaultTableModel(new String[]{"#","Exercise ID","Sets","Reps","Rest(s)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablePlanDetails = new JTable(detailModel);
        UITheme.styleTable(tablePlanDetails);
        tablePlanDetails.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Tabla de planes guardados en BD
        plansModel = new DefaultTableModel(new String[]{"ID","Plan Name","Date","Client","Objective"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableTrainingPlans = new JTable(plansModel);
        UITheme.styleTable(tableTrainingPlans);
        tableTrainingPlans.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableTrainingPlans.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableTrainingPlans.getSelectedRow() >= 0) {
                int row = tableTrainingPlans.getSelectedRow();
                txtPlanName.setText(plansModel.getValueAt(row, 1).toString());
                txtDate.setText(plansModel.getValueAt(row, 2).toString());
                String obj = plansModel.getValueAt(row, 4) != null ? plansModel.getValueAt(row, 4).toString() : "";
                txtObjective.setText(obj);
            }
        });

        // =====================================================================
        // NUEVO LAYOUT
        //   FILA SUPERIOR:
        //     [buildFormPanel() — ya en NORTH]  |  Available Exercises (1/4)
        //   FILA INFERIOR:
        //     Saved Training Plans (1/2)         |  Plan Details (1/2)
        // =====================================================================

        // -- Columna derecha arriba: Available Exercises (campo de clase) --
        availablePanel = new JPanel(new BorderLayout(4, 4));
        availablePanel.setBackground(UITheme.CREAM);
        availablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UITheme.GOLD, 2),
            "Available Exercises",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 13),
            UITheme.TEXT));
        JScrollPane availableScroll = new JScrollPane(tableAvailableExercises);
        availableScroll.getViewport().setBackground(UITheme.WHITE);
        availablePanel.add(availableScroll, BorderLayout.CENTER);

        // -- Fila inferior izquierda: Saved Training Plans --
        JPanel plansPanel = new JPanel(new BorderLayout(4, 4));
        plansPanel.setBackground(UITheme.CREAM);
        plansPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UITheme.GOLD, 2),
            "Saved Training Plans",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 13),
            UITheme.TEXT));
        JScrollPane plansScroll = new JScrollPane(tableTrainingPlans);
        plansScroll.getViewport().setBackground(UITheme.WHITE);
        plansPanel.add(plansScroll, BorderLayout.CENTER);

        // -- Fila inferior derecha: Plan Details --
        JPanel detailsPanel = new JPanel(new BorderLayout(4, 4));
        detailsPanel.setBackground(UITheme.CREAM);
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UITheme.GOLD, 2),
            "Plan Details (unsaved)",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 13),
            UITheme.TEXT));
        JScrollPane detailsScroll = new JScrollPane(tablePlanDetails);
        detailsScroll.getViewport().setBackground(UITheme.WHITE);
        detailsPanel.add(detailsScroll, BorderLayout.CENTER);

        // -- Fila inferior: plans (50%) + details (50%) --
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 8, 0));
        bottomRow.setBackground(UITheme.CREAM);
        bottomRow.add(plansPanel);
        bottomRow.add(detailsPanel);

        // El CENTER solo tiene la fila inferior: Saved Plans | Plan Details
        // (availablePanel ya fue añadido al topSplit en el constructor)
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UITheme.CREAM);
        center.add(bottomRow, BorderLayout.CENTER);

        return center;
    }


    // --- BOTONES ---
    private JPanel buildButtonPanel() {
        UITheme.styleSaveButton(btnSave);
        UITheme.styleUpdateButton(btnUpdate);
        UITheme.styleDeleteButton(btnDelete);
        return UITheme.createButtonPanel(btnSave, btnUpdate, btnDelete);
    }


    // --- CARGA DE OPCIONES ---
    public void loadClientOptions(ResultSet rs) {
        cmbClient.removeAllItems();
        clientCIs.clear();
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

    public void loadExerciseOptions(ResultSet rs) {
        availableModel.setRowCount(0);
        exerciseIds.clear();
        try {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("id_exercise");
                    exerciseIds.add(id);
                    availableModel.addRow(new Object[]{
                        id,
                        rs.getString("name"),
                        rs.getString("muscle_group_name")
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- MÉTODOS DEL CONTROLLER ---
    public void showList(ResultSet data) {
        plansModel.setRowCount(0);
        try {
            while (data != null && data.next()) {
                plansModel.addRow(new Object[]{
                    data.getInt("id_plan"),
                    data.getString("plan_name"),
                    data.getString("date"),
                    data.getString("client_name"),
                    data.getString("objective")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void addExerciseRow(int exId, int sets, int reps, int restTime) {
        int order = detailModel.getRowCount() + 1;
        detailModel.addRow(new Object[]{order, exId, sets, reps, restTime});
    }

    public void removeSelectedExerciseRow() {
        int row = tablePlanDetails.getSelectedRow();
        if (row >= 0) detailModel.removeRow(row);
    }

    public int[][] getPlanDetailsData() {
        int rows = detailModel.getRowCount();
        int[][] data = new int[rows][5];
        for (int i = 0; i < rows; i++) {
            data[i][0] = Integer.parseInt(detailModel.getValueAt(i, 1).toString()); // exerciseId
            data[i][1] = Integer.parseInt(detailModel.getValueAt(i, 2).toString()); // sets
            data[i][2] = Integer.parseInt(detailModel.getValueAt(i, 3).toString()); // reps
            data[i][3] = Integer.parseInt(detailModel.getValueAt(i, 4).toString()); // restTime
            data[i][4] = Integer.parseInt(detailModel.getValueAt(i, 0).toString()); // order
        }
        return data;
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Gym MVC", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearFields() {
        txtPlanName.setText("");
        txtDate.setText("");
        txtObjective.setText("");
        txtSets.setText("");
        txtReps.setText("");
        txtRestTime.setText("");
        detailModel.setRowCount(0);
        tableTrainingPlans.clearSelection();
    }

    // --- GETTERS ---
    public String getPlanName()  { return txtPlanName.getText().trim(); }
    public String getDate()      { return txtDate.getText().trim(); }
    public String getObjective() { return txtObjective.getText().trim(); }

    public int getClientCI() {
        int idx = cmbClient.getSelectedIndex();
        if (idx < 0 || idx >= clientCIs.size()) return -1;
        return clientCIs.get(idx);
    }

    public int getSets() {
        try { return Integer.parseInt(txtSets.getText().trim()); } catch (Exception e) { return 0; }
    }
    public int getReps() {
        try { return Integer.parseInt(txtReps.getText().trim()); } catch (Exception e) { return 0; }
    }
    public int getRestTime() {
        try { return Integer.parseInt(txtRestTime.getText().trim()); } catch (Exception e) { return 0; }
    }

    public int getSelectedPlanId() {
        int row = tableTrainingPlans.getSelectedRow();
        if (row < 0) return -1;
        return Integer.parseInt(plansModel.getValueAt(row, 0).toString());
    }

    public int getSelectedExerciseId() {
        int row = tableAvailableExercises.getSelectedRow();
        if (row < 0 || row >= exerciseIds.size()) return -1;
        return exerciseIds.get(row);
    }

    // --- EXPONER BOTONES AL CONTROLLER ---
    public JButton getBtnSave()           { return btnSave; }
    public JButton getBtnUpdate()         { return btnUpdate; }
    public JButton getBtnDelete()         { return btnDelete; }
    public JButton getBtnAddExercise()    { return btnAddExercise; }
    public JButton getBtnRemoveExercise() { return btnRemoveExercise; }
}
