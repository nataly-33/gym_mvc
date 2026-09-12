package controller;

import model.ExerciseModel;
import model.MuscleGroupModel;
import view.ExerciseView;

public class ExerciseController {

    private ExerciseView view;
    private ExerciseModel exerciseModel;
    private MuscleGroupModel muscleGroupModel;

    public ExerciseController(ExerciseView view,
                              ExerciseModel exerciseModel,
                              MuscleGroupModel muscleGroupModel) {
        this.view             = view;
        this.exerciseModel    = exerciseModel;
        this.muscleGroupModel = muscleGroupModel;
        loadMuscleGroups();
        listExercises();
        view.getBtnSave().addActionListener(e -> saveExercise());
        view.getBtnUpdate().addActionListener(e -> updateExercise());
        view.getBtnDelete().addActionListener(e -> deleteExercise());
        view.getBtnPreviewVideo().addActionListener(e -> previewVideo());
    }

    public void listExercises() {
        view.showList(exerciseModel.getAll());
    }

    public void loadMuscleGroups() {
        view.loadMuscleGroupOptions(muscleGroupModel.getAll());
    }

    public void saveExercise() {
        boolean ok = exerciseModel.create(
            view.getName(), view.getDescription(), view.getVideoUrl(),
            view.getDifficulty(), view.getMuscleGroupId());
        view.showMessage(ok ? "Exercise saved." : "Error: Could not save exercise.");
        if (ok) { listExercises(); view.clearFields(); }
    }

    public void updateExercise() {
        boolean ok = exerciseModel.update(
            view.getSelectedId(), view.getName(), view.getDescription(),
            view.getVideoUrl(), view.getDifficulty(), view.getMuscleGroupId());
        view.showMessage(ok ? "Exercise updated." : "Error: Could not update exercise.");
        if (ok) { listExercises(); view.clearFields(); }
    }

    public void deleteExercise() {
        int id = view.getSelectedId();
        if (exerciseModel.hasActivePlans(id)) {
            view.showMessage("Cannot delete: exercise belongs to an active plan.");
            return;
        }
        boolean ok = exerciseModel.delete(id);
        view.showMessage(ok ? "Exercise deleted." : "Error: Could not delete exercise.");
        if (ok) { listExercises(); view.clearFields(); }
    }

    public void previewVideo() {
        String url = view.getVideoUrl();
        if (url == null || url.trim().isEmpty()) {
            view.showMessage("No video URL provided.");
            return;
        }
        view.openVideo(url);
    }
}
