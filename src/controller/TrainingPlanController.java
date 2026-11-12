package controller;

import model.ClientModel;
import model.ExerciseModel;
import model.TrainingPlanModel;
import model.state.PlanContext;
import view.TrainingPlanView;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TrainingPlanController {

    private TrainingPlanView  view;
    private PlanContext       planContext;
    private TrainingPlanModel planModel;
    private ClientModel       clientModel;
    private ExerciseModel     exerciseModel;

    public TrainingPlanController(TrainingPlanView view,
                                  TrainingPlanModel planModel,
                                  ClientModel       clientModel,
                                  ExerciseModel     exerciseModel) {
        this.view          = view;
        this.planContext   = new PlanContext();
        this.planModel     = planModel;
        this.clientModel   = clientModel;
        this.exerciseModel = exerciseModel;

        loadClients();
        loadExercises();
        listTrainingPlans();

        view.getBtnSave().addActionListener(e -> savePlan());
        view.getBtnUpdate().addActionListener(e -> updatePlan());
        view.getBtnDelete().addActionListener(e -> deletePlan());
        view.getBtnAdd().addActionListener(e -> addExerciseToPlan());
        view.getBtnRemove().addActionListener(e -> removeExerciseFromPlan());

        view.getBtnActivate().addActionListener(e -> activatePlan());
        view.getBtnComplete().addActionListener(e -> completePlan());
        view.getBtnCancel().addActionListener(e -> cancelPlan());
        view.getBtnReopen().addActionListener(e -> reopenPlan());

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

    public void loadSavedPlan() {
        int row = view.getTableSavedPlans().getSelectedRow();
        if (row < 0) return;
        int planId = (int) view.getTableSavedPlans().getModel().getValueAt(row, 0);

        view.loadPlanForm(planModel.getById(planId));

        String statusStr = (String) view.getTableSavedPlans().getModel().getValueAt(row, 4);
        planContext.cargarEstado(statusStr);
        view.updateStateUI(planContext.getNombreEstado());

        view.loadDetailRows(planModel.getDetailsByPlan(planId));
    }

    public void addExerciseToPlan() {
        if (!planContext.puedeEditar()) {
            view.showMessage("No se pueden agregar ejercicios: el plan está en estado '" +
                planContext.getNombreEstado() + "' (solo se permite en Borrador).");
            return;
        }
        view.addExerciseRow();
    }

    public void removeExerciseFromPlan() {
        if (!planContext.puedeEditar()) {
            view.showMessage("No se pueden eliminar ejercicios: el plan está en estado '" +
                planContext.getNombreEstado() + "' (solo se permite en Borrador).");
            return;
        }
        view.removeLastExerciseRow();
    }

    public void savePlan() {
        if (!planContext.puedeEditar()) {
            view.showMessage("No se puede guardar: el estado actual es '" +
                planContext.getNombreEstado() + "'.");
            return;
        }
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
        view.showMessage("Training plan saved successfully in Draft status.");
        listTrainingPlans();
        view.clearFields();
        planContext.cargarEstado("Borrador");
        view.updateStateUI(planContext.getNombreEstado());
    }

    public void updatePlan() {
        int planId = view.getSelectedPlanId();
        if (planId == -1) {
            view.showMessage("Select a saved plan first.");
            return;
        }
        if (!planContext.puedeEditar()) {
            view.showMessage("No se puede editar: el plan está en estado '" +
                planContext.getNombreEstado() + "'. Debe estar en Borrador para editar.");
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

        view.showMessage("Training plan updated successfully.");
        listTrainingPlans();
        view.clearFields();
    }

    public void deletePlan() {
        int planId = view.getSelectedPlanId();
        if (planId == -1) {
            view.showMessage("Select a saved plan first.");
            return;
        }
        boolean ok = planModel.delete(planId);
        view.showMessage(ok ? "Plan deleted." : "Error: Could not delete plan.");
        if (ok) {
            listTrainingPlans();
            view.clearFields();
            planContext.cargarEstado("Borrador");
            view.updateStateUI(planContext.getNombreEstado());
        }
    }

    public void activatePlan() {
        int planId = view.getSelectedPlanId();
        if (planId == -1) {
            view.showMessage("Select a saved plan first.");
            return;
        }
        String estadoAnterior = planContext.getNombreEstado();
        planContext.activar();
        String nuevoEstado = planContext.getNombreEstado();
        if (!estadoAnterior.equals(nuevoEstado)) {
            planModel.cambiarEstadoEnBD(planId, nuevoEstado);
            view.showMessage("Plan activado con éxito.");
            listTrainingPlans();
            view.updateStateUI(nuevoEstado);
        } else {
            view.showMessage("No se puede activar el plan desde el estado '" + estadoAnterior + "'.");
        }
    }

    public void completePlan() {
        int planId = view.getSelectedPlanId();
        if (planId == -1) {
            view.showMessage("Select a saved plan first.");
            return;
        }
        String estadoAnterior = planContext.getNombreEstado();
        planContext.completar();
        String nuevoEstado = planContext.getNombreEstado();
        if (!estadoAnterior.equals(nuevoEstado)) {
            planModel.cambiarEstadoEnBD(planId, nuevoEstado);
            view.showMessage("Plan completado exitosamente.");
            listTrainingPlans();
            view.updateStateUI(nuevoEstado);
        } else {
            view.showMessage("No se puede completar el plan desde el estado '" + estadoAnterior + "'. Debe estar Activo previamente.");
        }
    }

    public void cancelPlan() {
        int planId = view.getSelectedPlanId();
        if (planId == -1) {
            view.showMessage("Select a saved plan first.");
            return;
        }
        String estadoAnterior = planContext.getNombreEstado();
        planContext.cancelar();
        String nuevoEstado = planContext.getNombreEstado();
        if (!estadoAnterior.equals(nuevoEstado)) {
            planModel.cambiarEstadoEnBD(planId, nuevoEstado);
            view.showMessage("Plan cancelado.");
            listTrainingPlans();
            view.updateStateUI(nuevoEstado);
        } else {
            view.showMessage("No se puede cancelar el plan desde el estado '" + estadoAnterior + "'.");
        }
    }

    public void reopenPlan() {
        int planId = view.getSelectedPlanId();
        if (planId == -1) {
            view.showMessage("Select a saved plan first.");
            return;
        }
        String estadoAnterior = planContext.getNombreEstado();
        planContext.reiniciarBorrador();
        String nuevoEstado = planContext.getNombreEstado();
        if (!estadoAnterior.equals(nuevoEstado)) {
            planModel.cambiarEstadoEnBD(planId, nuevoEstado);
            view.showMessage("Plan reabierto como Borrador.");
            listTrainingPlans();
            view.updateStateUI(nuevoEstado);
        } else {
            view.showMessage("No se puede reabrir el plan desde el estado actual.");
        }
    }

    public PlanContext getPlanContext() {
        return planContext;
    }
}
