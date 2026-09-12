import com.formdev.flatlaf.FlatDarkLaf;
import model.*;
import controller.*;
import view.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Tema moderno - una sola linea, no es framework
        FlatDarkLaf.setup();

        // Personalizacion de apariencia
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 8);
        UIManager.put("TabbedPane.tabHeight", 36);
        UIManager.put("Table.rowHeight", 28);
        UIManager.put("TextComponent.arc", 6);

        // Inicializar DB (crea gym.db y tablas si no existen)
        DatabaseConnection.getInstance();

        // Instanciar modelos
        ClientModel        clientModel        = new ClientModel();
        MuscleGroupModel   muscleGroupModel   = new MuscleGroupModel();
        ExerciseModel      exerciseModel      = new ExerciseModel();
        TrainingPlanModel  planModel          = new TrainingPlanModel();
        PlanDetailModel    detailModel        = new PlanDetailModel();
        MeasurementModel   measurementModel   = new MeasurementModel();
        ReportModel        reportModel        = new ReportModel();

        // Lanzar ventana principal
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(
                clientModel, muscleGroupModel, exerciseModel,
                planModel, detailModel, measurementModel, reportModel);
            frame.setVisible(true);
        });
    }
}
