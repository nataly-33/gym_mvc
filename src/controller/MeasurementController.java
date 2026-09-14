package controller;

import model.ClientModel;
import model.MeasurementModel;
import view.MeasurementView;

public class MeasurementController {

    private MeasurementView view;
    private MeasurementModel model;
    private ClientModel clientModel;

    public MeasurementController(MeasurementView view,
                                 MeasurementModel model,
                                 ClientModel clientModel) {
        this.view        = view;
        this.model       = model;
        this.clientModel = clientModel;
        loadClients();
        listMeasurements();
        view.getBtnSave().addActionListener(e -> saveMeasurement());
        view.getBtnUpdate().addActionListener(e -> updateMeasurement());
        view.getBtnDelete().addActionListener(e -> deleteMeasurement());
        view.getCmbClient().addActionListener(e -> filterByClient(view.getClientCI()));
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
}
