package controller;

import model.*;
import view.TrainingPlanView;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TrainingPlanController {

    private TrainingPlanView  view;
    private TrainingPlanModel planModel;
    private PlanDetailModel   detailModel;
    private ClientModel       clientModel;
    private ExerciseModel     exerciseModel;

    public TrainingPlanController(TrainingPlanView view,
                                  TrainingPlanModel planModel,
                                  PlanDetailModel   detailModel,
                                  ClientModel       clientModel,
                                  ExerciseModel     exerciseModel) {
        this.view          = view;
        this.planModel     = planModel;
        this.detailModel   = detailModel;
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
        view.loadDetailRows(detailModel.getByPlan(planId));
    }

    // Agregar una fila vacía al área de detalles
    public void addExerciseToPlan() {
        view.addExerciseRow();
    }

    // Eliminar la última fila del área de detalles
    public void removeExerciseFromPlan() {
        view.removeLastExerciseRow();
    }

    // Crear plan + todos sus detalles en una sola operación
    // Columnas schema TrainingPlan: plan_name, date, objective, ci_client
    // Columnas schema PlanDetail: id_plan, id_detail, id_exercise, sets, reps, rest_time, exercise_order
    public void savePlan() {
        int[][] details = view.getPlanDetailsData();
        if (details.length == 0) {
            view.showMessage("Add at least one exercise to the plan.");
            return;
        }
        int planId = planModel.create(
            view.getPlanName(), view.getDate(),
            view.getObjective(), view.getClientCI());
        if (planId == -1) {
            view.showMessage("Error: Could not create training plan.");
            return;
        }
        for (int[] d : details) {
            // d[0]=id_exercise, d[1]=sets, d[2]=reps, d[3]=rest_time, d[4]=exercise_order
            int nextId = detailModel.getNextDetailId(planId);
            detailModel.create(planId, nextId, d[0], d[1], d[2], d[3], d[4]);
        }
        view.showMessage("Training plan saved successfully.");
        listTrainingPlans();
        view.clearFields();
    }

    // Actualizar cabecera + borrar detalles anteriores + recrear desde filas actuales
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
        // Actualizar cabecera TrainingPlan: plan_name, date, objective (ci_client no cambia)
        boolean ok = planModel.update(
            planId, view.getPlanName(),
            view.getDate(), view.getObjective());
        if (!ok) { view.showMessage("Error: Could not update plan."); return; }

        // Borrar detalles anteriores y recrear desde filas actuales
        detailModel.deleteByPlan(planId);
        for (int[] d : details) {
            int nextId = detailModel.getNextDetailId(planId);
            detailModel.create(planId, nextId, d[0], d[1], d[2], d[3], d[4]);
        }
        view.showMessage("Training plan updated.");
        listTrainingPlans();
        view.clearFields();
    }

    public void deletePlan() {
        int planId = view.getSelectedPlanId();
        if (planId == -1) {
            view.showMessage("Select a saved plan first.");
            return;
        }
        // Eliminar primero los detalles (FK), luego el plan
        detailModel.deleteByPlan(planId);
        boolean ok = planModel.delete(planId);
        view.showMessage(ok ? "Plan deleted." : "Error: Could not delete plan.");
        if (ok) { listTrainingPlans(); view.clearFields(); }
    }
}
