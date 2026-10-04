package controller;

import model.*;
import view.TrainingPlanView;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TrainingPlanController {

    private TrainingPlanView  view;
    private TrainingPlanModel planModel;
    private ClientModel       clientModel;
    private ExerciseModel     exerciseModel;

    public TrainingPlanController(TrainingPlanView view,
                                  TrainingPlanModel planModel,
                                  ClientModel       clientModel,
                                  ExerciseModel     exerciseModel) {
        this.view          = view;
        this.planModel     = planModel;
        this.clientModel   = clientModel;
        this.exerciseModel = exerciseModel;

        loadClients();
        loadExercises();
        listTrainingPlans();

        // Suscribir eventos de botones
        view.getBtnSave().addActionListener(e -> savePlan());
        view.getBtnUpdate().addActionListener(e -> updatePlan());
        view.getBtnDelete().addActionListener(e -> deletePlan());
        view.getBtnAdd().addActionListener(e -> addExerciseToPlan());
        view.getBtnRemove().addActionListener(e -> removeExerciseFromPlan());

        // Click en tabla de planes guardados -> cargar plan en formulario
        view.getTableSavedPlans().getSelectionModel()
            .addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting() &&
                        view.getTableSavedPlans().getSelectedRow() >= 0) {
                        loadSavedPlan();
                    }
                }
            });
    }

    public void listTrainingPlans() {
        view.showList(planModel.getAll());
    }

    public void loadClients() {
        view.loadClientOptions(clientModel.getAll());
    }

    public void loadExercises() {
        view.loadExerciseOptions(exerciseModel.getAll());
    }

    // Carga el plan seleccionado de la tabla al formulario + sus filas de detalle
    public void loadSavedPlan() {
        int row = view.getTableSavedPlans().getSelectedRow();
        if (row < 0) return;
        int planId = (int) view.getTableSavedPlans().getModel().getValueAt(row, 0);
        view.loadPlanForm(planModel.getById(planId));
        // Por composición, se consultan los detalles a través de planModel
        view.loadDetailRows(planModel.getDetailsByPlan(planId));
    }

    // Agregar una fila vacía al área de detalles
    public void addExerciseToPlan() {
        view.addExerciseRow();
    }

    // Eliminar la última fila del área de detalles
    public void removeExerciseFromPlan() {
        view.removeLastExerciseRow();
    }

    // Crear plan + todos sus detalles a través de planModel (composición)
    public void savePlan() {
        int[][] details = view.getPlanDetailsData();
        if (details.length == 0) {
            view.showMessage("Add at least one exercise to the plan.");
            return;
        }
        boolean ok = planModel.saveWithDetails(
            view.getPlanName(), view.getDate(),
            view.getObjective(), view.getClientCI(), details);
        if (!ok) {
            view.showMessage("Error: Could not create training plan.");
            return;
        }
        view.showMessage("Training plan saved successfully.");
        listTrainingPlans();
        view.clearFields();
    }

    // Actualizar cabecera + detalles a través de planModel (composición)
    public void updatePlan() {
        int planId = view.getSelectedPlanId();
        if (planId == -1) {
            view.showMessage("Select a saved plan first.");
            return;
        }
        int[][] details = view.getPlanDetailsData();
        if (details.length == 0) {
            view.showMessage("Add at least one exercise to the plan.");
            return;
        }
        boolean ok = planModel.updateWithDetails(
            planId, view.getPlanName(),
            view.getDate(), view.getObjective(), details);
        if (!ok) {
            view.showMessage("Error: Could not update plan.");
            return;
        }

        view.showMessage("Training plan updated.");
        listTrainingPlans();
        view.clearFields();
    }

    // Eliminar plan (por composición elimina automáticamente sus detalles)
    public void deletePlan() {
        int planId = view.getSelectedPlanId();
        if (planId == -1) {
            view.showMessage("Select a saved plan first.");
            return;
        }
        boolean ok = planModel.delete(planId);
        view.showMessage(ok ? "Plan deleted." : "Error: Could not delete plan.");
        if (ok) { listTrainingPlans(); view.clearFields(); }
    }
}

