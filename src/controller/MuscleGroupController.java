package controller;

import model.MuscleGroupModel;
import view.MuscleGroupView;

public class MuscleGroupController {

    private MuscleGroupView view;
    private MuscleGroupModel model;

    public MuscleGroupController(MuscleGroupView view, MuscleGroupModel model) {
        this.view  = view;
        this.model = model;
        listMuscleGroups();
        view.getBtnSave().addActionListener(e -> saveMuscleGroup());
        view.getBtnUpdate().addActionListener(e -> updateMuscleGroup());
        view.getBtnDelete().addActionListener(e -> deleteMuscleGroup());
    }

    public void listMuscleGroups() {
        view.showList(model.getAll());
    }

    public void saveMuscleGroup() {
        boolean ok = model.create(view.getName(), view.getDescription());
        view.showMessage(ok ? "Muscle group saved." : "Error: Could not save.");
        if (ok) { listMuscleGroups(); view.clearFields(); }
    }

    public void updateMuscleGroup() {
        boolean ok = model.update(view.getSelectedId(), view.getName(), view.getDescription());
        view.showMessage(ok ? "Muscle group updated." : "Error: Could not update.");
        if (ok) { listMuscleGroups(); view.clearFields(); }
    }

    public void deleteMuscleGroup() {
        boolean ok = model.delete(view.getSelectedId());
        view.showMessage(ok ? "Muscle group deleted." : "Error: Could not delete.");
        if (ok) { listMuscleGroups(); view.clearFields(); }
    }
}
