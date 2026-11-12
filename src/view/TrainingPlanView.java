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

    // --- Indicador visual y botones del Patrón State ---
    private JLabel  lblStatusBadge = new JLabel("  ESTADO: BORRADOR  ", SwingConstants.CENTER);
    private JButton btnActivate    = new JButton("▶ Activar");
    private JButton btnComplete    = new JButton("✔ Completar");
    private JButton btnCancel      = new JButton("✕ Cancelar");
    private JButton btnReopen      = new JButton("↺ Reabrir Borrador");

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

    // --- Botones CRUD ---
    private JButton btnAdd    = new JButton("Add ▼");
    private JButton btnRemove = new JButton("✕ Remove Last");
    private JButton btnSave   = new JButton("Save Plan");
    private JButton btnUpdate = new JButton("Update Plan");
    private JButton btnDelete = new JButton("Delete Plan");

    // --- Estado: plan seleccionado ---
    private int selectedPlanId = -1;
    private String currentPlanStatus = "Borrador";

    public TrainingPlanView() {
        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.CREAM);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 10, 12));

        // Estilo de campos
        txtPlanName.setFont(UITheme.LABEL_FONT);
        txtDate.setFont(UITheme.LABEL_FONT);
        txtObjective.setFont(UITheme.LABEL_FONT);
        UITheme.styleComboBox(cmbClient);

        // Estilo de botones CRUD
        UITheme.styleSecondaryButton(btnAdd);
        UITheme.styleSecondaryButton(btnRemove);
        UITheme.styleSaveButton(btnSave);
        UITheme.styleUpdateButton(btnUpdate);
        UITheme.styleDeleteButton(btnDelete);

        // Estilo de botones State
        styleStateButtons();

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

        // Estado inicial de la interfaz
        updateStateUI("Borrador");
    }

    private void styleStateButtons() {
        lblStatusBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatusBadge.setOpaque(true);
        lblStatusBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xB8860B), 2),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        btnActivate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnActivate.setBackground(new Color(0x3B82F6));
        btnActivate.setForeground(Color.WHITE);
        btnActivate.setFocusPainted(false);

        btnComplete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnComplete.setBackground(new Color(0x10B981));
        btnComplete.setForeground(Color.WHITE);
        btnComplete.setFocusPainted(false);

        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setBackground(new Color(0xEF4444));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);

        btnReopen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReopen.setBackground(new Color(0x6B7280));
        btnReopen.setForeground(Color.WHITE);
        btnReopen.setFocusPainted(false);
    }

    // =========================================================
    // PANEL IZQUIERDO: formulario + barra de estado + filas
    // =========================================================
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.CREAM);

        // --- Formulario superior ---
        JPanel formPanel = UITheme.createSectionPanel("Training Plan Header & Lifecycle State");
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Fila 0: Barra de Estado (Patrón State)
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 12, 8, 6);
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(UITheme.styledLabel("Lifecycle State:"), gbc);

        JPanel stateBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        stateBar.setBackground(UITheme.CREAM);
        stateBar.add(lblStatusBadge);
        stateBar.add(btnActivate);
        stateBar.add(btnComplete);
        stateBar.add(btnCancel);
        stateBar.add(btnReopen);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 8, 12);
        formPanel.add(stateBar, gbc);
        gbc.gridwidth = 1;

        // Fila 1: Plan Name y Date
        UITheme.addFormRow2(formPanel, gbc, 1, "Plan Name:", txtPlanName, "Date (yyyy-MM-dd):", txtDate);

        // Fila 2: Client Combo
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 1;
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

        // Fila 3: Objective
        UITheme.addFormRow(formPanel, gbc, 3, "Objective:", txtObjective);

        // --- Botones Add/Remove de detalles ---
        JPanel addRemovePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        addRemovePanel.setBackground(UITheme.CREAM);
        addRemovePanel.add(btnAdd);
        addRemovePanel.add(btnRemove);
        JLabel hint = UITheme.styledLabel("← Add exercises (Locked if plan is Active or Completed)");
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
            "Exercise Details (Plan Details)",
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

        // Columna 4: Status (Patrón State)
        String[] cols = {"ID", "Plan Name", "Date", "Client", "Status"};
        savedPlansModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableSavedPlans = new JTable(savedPlansModel);
        UITheme.styleTable(tableSavedPlans);
        tableSavedPlans.getColumnModel().getColumn(0).setPreferredWidth(30);
        tableSavedPlans.getColumnModel().getColumn(1).setPreferredWidth(110);
        tableSavedPlans.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableSavedPlans.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableSavedPlans.getColumnModel().getColumn(4).setPreferredWidth(85);

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
    // CONTROL VISUAL DE ESTADOS (PATRÓN STATE)
    // =========================================================
    public void updateStateUI(String status) {
        this.currentPlanStatus = (status != null && !status.trim().isEmpty()) ? status : "Borrador";

        if ("Activo".equalsIgnoreCase(currentPlanStatus)) {
            lblStatusBadge.setText("  ESTADO: ACTIVO (EN CURSO)  ");
            lblStatusBadge.setBackground(new Color(0xDBEAFE)); // Light Blue
            lblStatusBadge.setForeground(new Color(0x1E40AF)); // Dark Blue

            // En Activo: Se bloquea la edición de ejercicios (no se cambia la rutina en marcha)
            btnAdd.setEnabled(false);
            btnRemove.setEnabled(false);
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(false);

            btnActivate.setEnabled(false);
            btnComplete.setEnabled(true);  // ¡Habilitado en verde para completar!
            btnCancel.setEnabled(true);    // Se puede cancelar
            btnReopen.setEnabled(false);

        } else if ("Completado".equalsIgnoreCase(currentPlanStatus)) {
            lblStatusBadge.setText("  ESTADO: COMPLETADO  ");
            lblStatusBadge.setBackground(new Color(0xD1FAE5)); // Light Green
            lblStatusBadge.setForeground(new Color(0x065F46)); // Dark Green

            // En Completado: Todo es de solo lectura histórica
            btnAdd.setEnabled(false);
            btnRemove.setEnabled(false);
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(false);

            btnActivate.setEnabled(false);
            btnComplete.setEnabled(false);
            btnCancel.setEnabled(false);
            btnReopen.setEnabled(true);   // Permite reabrir como borrador

        } else if ("Cancelado".equalsIgnoreCase(currentPlanStatus)) {
            lblStatusBadge.setText("  ESTADO: CANCELADO  ");
            lblStatusBadge.setBackground(new Color(0xFEE2E2)); // Light Red
            lblStatusBadge.setForeground(new Color(0x991B1B)); // Dark Red

            btnAdd.setEnabled(false);
            btnRemove.setEnabled(false);
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(false);

            btnActivate.setEnabled(false);
            btnComplete.setEnabled(false);
            btnCancel.setEnabled(false);
            btnReopen.setEnabled(true);   // Permite recuperar a borrador

        } else { // "Borrador"
            lblStatusBadge.setText("  ESTADO: BORRADOR (EDICIÓN)  ");
            lblStatusBadge.setBackground(new Color(0xFEF3C7)); // Light Amber
            lblStatusBadge.setForeground(new Color(0x92400E)); // Dark Amber

            // En Borrador: Edición libre
            btnAdd.setEnabled(true);
            btnRemove.setEnabled(true);
            btnSave.setEnabled(selectedPlanId == -1);
            btnUpdate.setEnabled(selectedPlanId != -1);

            btnActivate.setEnabled(selectedPlanId != -1); // Se puede activar una vez guardado
            btnComplete.setEnabled(false);                // ¡BLOQUEADO! No se puede completar en borrador
            btnCancel.setEnabled(selectedPlanId != -1);
            btnReopen.setEnabled(false);
        }
    }

    // =========================================================
    // MÉTODOS LLAMADOS POR EL CONTROLLER
    // =========================================================

    public void showList(ResultSet data) {
        savedPlansModel.setRowCount(0);
        try {
            while (data != null && data.next()) {
                String st = data.getString("status");
                if (st == null || st.trim().isEmpty()) st = "Borrador";
                savedPlansModel.addRow(new Object[]{
                    data.getInt("id_plan"),
                    data.getString("plan_name"),
                    data.getString("date"),
                    data.getString("client_name"),
                    st
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Gym MVC — State Training Plan", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearFields() {
        txtPlanName.setText("");
        txtDate.setText("");
        txtObjective.setText("");
        if (cmbClient.getItemCount() > 0) cmbClient.setSelectedIndex(0);
        clearExerciseRows();
        tableSavedPlans.clearSelection();
        selectedPlanId = -1;
        updateStateUI("Borrador");
    }

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
                String st = planData.getString("status");
                updateStateUI(st);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

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

    public void loadClientOptions(ResultSet data) {
        cmbClient.removeAllItems();
        clientCiList.clear();
        try {
            while (data != null && data.next()) {
                int ci = data.getInt("ci");
                String display = ci + " - " + data.getString("first_name") + " " + data.getString("last_name");
                cmbClient.addItem(display);
                clientCiList.add(ci);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

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

    public void addExerciseRow() {
        ExerciseRowPanel row = new ExerciseRowPanel(exerciseRows.size() + 1, exerciseItems);
        exerciseRows.add(row);
        rowsContainer.add(row);
        rowsContainer.revalidate();
        rowsContainer.repaint();
    }

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
    public String getCurrentPlanStatus() { return currentPlanStatus; }

    public int[][] getPlanDetailsData() {
        int[][] data = new int[exerciseRows.size()][5];
        for (int i = 0; i < exerciseRows.size(); i++) {
            ExerciseRowPanel row = exerciseRows.get(i);
            data[i][0] = row.getExerciseId();
            data[i][1] = row.getSets();
            data[i][2] = row.getReps();
            data[i][3] = row.getRestTime();
            data[i][4] = i + 1;
        }
        return data;
    }

    // Exponer botones CRUD y State
    public JButton getBtnSave()     { return btnSave; }
    public JButton getBtnUpdate()   { return btnUpdate; }
    public JButton getBtnDelete()   { return btnDelete; }
    public JButton getBtnAdd()      { return btnAdd; }
    public JButton getBtnRemove()   { return btnRemove; }
    public JButton getBtnActivate() { return btnActivate; }
    public JButton getBtnComplete() { return btnComplete; }
    public JButton getBtnCancel()   { return btnCancel; }
    public JButton getBtnReopen()   { return btnReopen; }
    public JTable  getTableSavedPlans() { return tableSavedPlans; }

    // Inner classes
    private static class ExerciseItem {
        final int id;
        final String display;
        ExerciseItem(int id, String display) { this.id = id; this.display = display; }
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
            add(UITheme.styledLabel("Sets:"));   add(txtSets);
            add(UITheme.styledLabel("Reps:"));   add(txtReps);
            add(UITheme.styledLabel("Rest(s):")); add(txtRest);
        }

        void loadData(int exerciseId, int sets, int reps, int rest) {
            for (int i = 0; i < cmbExercise.getItemCount(); i++) {
                if (cmbExercise.getItemAt(i).id == exerciseId) {
                    cmbExercise.setSelectedIndex(i); break;
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
        int getSets()     { try { return Integer.parseInt(txtSets.getText().trim()); } catch(Exception e){return 0;} }
        int getReps()     { try { return Integer.parseInt(txtReps.getText().trim()); } catch(Exception e){return 0;} }
        int getRestTime() { try { return Integer.parseInt(txtRest.getText().trim()); } catch(Exception e){return 0;} }
    }
}
