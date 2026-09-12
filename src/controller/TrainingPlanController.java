package controller;

import model.*;
import view.TrainingPlanView;

public class TrainingPlanController {

    private TrainingPlanView view;
    private TrainingPlanModel planModel;
    private PlanDetailModel detailModel;
    private ClientModel clientModel;
    private ExerciseModel exerciseModel;

    public TrainingPlanController(TrainingPlanView view,
                                  TrainingPlanModel planModel,
                                  PlanDetailModel detailModel,
                                  ClientModel clientModel,
                                  ExerciseModel exerciseModel) {
        this.view          = view;
        this.planModel     = planModel;
        this.detailModel   = detailModel;
        this.clientModel   = clientModel;
        this.exerciseModel = exerciseModel;
        loadClients();
        loadExercises();
        listTrainingPlans();
        view.getBtnSave().addActionListener(e -> savePlan());
        view.getBtnUpdate().addActionListener(e -> updatePlan());
        view.getBtnDelete().addActionListener(e -> deletePlan());
        view.getBtnAddExercise().addActionListener(e -> addExerciseToPlan());
        view.getBtnRemoveExercise().addActionListener(e -> removeExerciseFromPlan());
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

    public void savePlan() {
        int planId = planModel.create(
            view.getPlanName(), view.getDate(),
            view.getObjective(), view.getClientCI());
        if (planId == -1) {
            view.showMessage("Error: Could not create training plan.");
            return;
        }
        // getPlanDetailsData() retorna int[][] donde cada fila es:
        // [exerciseId, sets, reps, restTime, order]
        int[][] details = view.getPlanDetailsData();
        for (int[] d : details) {
            int nextId = detailModel.getNextDetailId(planId);
            detailModel.create(planId, nextId, d[0], d[1], d[2], d[3], d[4]);
        }
        view.showMessage("Training plan saved successfully.");
        listTrainingPlans();
        view.clearFields();
    }

    public void updatePlan() {
        boolean ok = planModel.update(
            view.getSelectedPlanId(), view.getPlanName(),
            view.getDate(), view.getObjective());
        view.showMessage(ok ? "Plan updated." : "Error: Could not update plan.");
        if (ok) { listTrainingPlans(); view.clearFields(); }
    }

    public void deletePlan() {
        int id = view.getSelectedPlanId();
        detailModel.deleteByPlan(id);
        boolean ok = planModel.delete(id);
        view.showMessage(ok ? "Plan deleted." : "Error: Could not delete plan.");
        if (ok) { listTrainingPlans(); view.clearFields(); }
    }

    public void addExerciseToPlan() {
        view.addExerciseRow(
            view.getSelectedExerciseId(),
            view.getSets(),
            view.getReps(),
            view.getRestTime());
    }

    public void removeExerciseFromPlan() {
        view.removeSelectedExerciseRow();
    }
}
