package view;

import controller.*;
import model.*;
import javax.swing.*;
import java.awt.*;

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
        setMinimumSize(new Dimension(1100, 750));
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension((int)(screen.width * 0.82), (int)(screen.height * 0.85)));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.CREAM);

        // Header dorado
        JLabel header = new JLabel("  Gym Management System");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setBackground(UITheme.GOLD);
        header.setForeground(UITheme.TEXT);
        header.setOpaque(true);
        header.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        add(header, BorderLayout.NORTH);

        // Crear vistas
        ClientView       clientView       = new ClientView();
        MuscleGroupView  muscleGroupView  = new MuscleGroupView();
        ExerciseView     exerciseView     = new ExerciseView();
        TrainingPlanView planView         = new TrainingPlanView();
        MeasurementView  measurementView  = new MeasurementView();
        ReportView       reportView       = new ReportView();

        // Instanciar controllers (se suscriben a eventos de las vistas)
        new ClientController(clientView, clientModel);
        new MuscleGroupController(muscleGroupView, muscleGroupModel);
        new ExerciseController(exerciseView, exerciseModel, muscleGroupModel);
        new TrainingPlanController(planView, planModel, detailModel,
                                   clientModel, exerciseModel);
        new MeasurementController(measurementView, measurementModel, clientModel);
        new ReportController(reportView, reportModel, clientModel, planModel);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabs.setBackground(UITheme.CREAM);
        tabs.addTab("Clients",        clientView);
        tabs.addTab("Muscle Groups",  muscleGroupView);
        tabs.addTab("Exercises",      exerciseView);
        tabs.addTab("Training Plans", planView);
        tabs.addTab("Measurements",   measurementView);
        tabs.addTab("Reports",        reportView);

        add(tabs, BorderLayout.CENTER);
        pack();
    }
}
