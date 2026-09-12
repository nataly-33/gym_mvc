package view;

import controller.*;
import model.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame(ClientModel clientModel,
                     MuscleGroupModel muscleGroupModel,
                     ExerciseModel exerciseModel,
                     TrainingPlanModel planModel,
                     PlanDetailModel detailModel,
                     MeasurementModel measurementModel,
                     ReportModel reportModel) {

        setTitle("Gym Management System — MVC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        // Crear vistas
        ClientView        clientView        = new ClientView();
        MuscleGroupView   muscleGroupView   = new MuscleGroupView();
        ExerciseView      exerciseView      = new ExerciseView();
        TrainingPlanView  planView          = new TrainingPlanView();
        MeasurementView   measurementView   = new MeasurementView();
        ReportView        reportView        = new ReportView();

        // Instanciar controladores (ellos se suscriben a los eventos de las vistas)
        new ClientController(clientView, clientModel);
        new MuscleGroupController(muscleGroupView, muscleGroupModel);
        new ExerciseController(exerciseView, exerciseModel, muscleGroupModel);
        new TrainingPlanController(planView, planModel, detailModel, clientModel, exerciseModel);
        new MeasurementController(measurementView, measurementModel, clientModel);
        new ReportController(reportView, reportModel, clientModel, planModel);

        // Pestanas
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Clients",         clientView);
        tabs.addTab("Muscle Groups",   muscleGroupView);
        tabs.addTab("Exercises",       exerciseView);
        tabs.addTab("Training Plans",  planView);
        tabs.addTab("Measurements",    measurementView);
        tabs.addTab("Reports",         reportView);

        add(tabs);
    }
}
