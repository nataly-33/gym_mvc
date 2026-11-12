package controller;

import model.ClientModel;
import model.MeasurementModel;
import model.strategy.EvaluacionContext;
import model.strategy.EvaluacionGrasaStrategy;
import model.strategy.EvaluacionOMSStrategy;
import model.strategy.EvaluacionRiesgoCardioStrategy;
import view.MeasurementView;

public class MeasurementController {

    private MeasurementView view;
    private EvaluacionContext context;
    private MeasurementModel model;
    private ClientModel clientModel;

    public MeasurementController(MeasurementView view,
                                 MeasurementModel model,
                                 ClientModel clientModel) {
        this.view        = view;
        this.context     = new EvaluacionContext(new EvaluacionOMSStrategy());
        this.model       = model;
        this.clientModel = clientModel;

        loadClients();
        listMeasurements();

        view.getBtnSave().addActionListener(e -> saveMeasurement());
        view.getBtnUpdate().addActionListener(e -> updateMeasurement());
        view.getBtnDelete().addActionListener(e -> deleteMeasurement());
        view.getCmbClient().addActionListener(e -> {
            if (!view.isUpdatingFromTable()) {
                filterByClient(view.getClientCI());
            }
        });

        view.getBtnEvaluate().addActionListener(e -> evaluateMeasurement());
    }

    public void listMeasurements() {
        view.showList(model.getAll());
    }

    public void loadClients() {
        view.loadClientOptions(clientModel.getAll());
    }

    public void saveMeasurement() {
        boolean ok = model.create(
            view.getClientCI(), view.getWeightKg(), view.getBodyFat(),
            view.getChestCm(), view.getGlutes(), view.getWaistCm(),
            view.getMeasurementDate());
        view.showMessage(ok ? "Measurement saved." : "Error: Could not save measurement.");
        if (ok) { listMeasurements(); view.clearFields(); }
    }

    public void updateMeasurement() {
        boolean ok = model.update(
            view.getSelectedId(), view.getWeightKg(), view.getBodyFat(),
            view.getChestCm(), view.getGlutes(), view.getWaistCm(),
            view.getMeasurementDate());
        view.showMessage(ok ? "Measurement updated." : "Error: Could not update.");
        if (ok) { listMeasurements(); view.clearFields(); }
    }

    public void deleteMeasurement() {
        boolean ok = model.delete(view.getSelectedId());
        view.showMessage(ok ? "Measurement deleted." : "Error: Could not delete.");
        if (ok) { listMeasurements(); view.clearFields(); }
    }

    public void filterByClient(int ci) {
        if (ci <= 0) { listMeasurements(); return; }
        view.showList(model.getByClient(ci));
    }

    public void evaluateMeasurement() {
        double weight  = view.getWeightKg();
        double waist   = view.getWaistCm();
        double glutes  = view.getGlutes();
        double bodyFat = view.getBodyFat();
        double chest   = view.getChestCm();

        if (weight <= 0 && waist <= 0) {
            view.showMessage("Seleccione una medida de la tabla o complete los campos para evaluar.");
            return;
        }

        int index = view.getSelectedEvaluationIndex();
        if (index == 0) {
            context.setStrategy(new EvaluacionOMSStrategy());
        } else if (index == 1) {
            context.setStrategy(new EvaluacionRiesgoCardioStrategy());
        } else {
            context.setStrategy(new EvaluacionGrasaStrategy());
        }

        String diagnosis = context.ejecutarEvaluacion(weight, waist, glutes, bodyFat, chest);
        view.showEvaluationResult(diagnosis);
    }

    public EvaluacionContext getContext() {
        return context;
    }
}
