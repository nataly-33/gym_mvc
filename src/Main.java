import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import model.*;
import controller.*;
import view.*;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class Main {

    private static String detectFont() {
        String[] candidates = {
            "Noto Sans", "Ubuntu", "Liberation Sans",
            "DejaVu Sans", "Cantarell", "FreeSans", "SansSerif"
        };
        Set<String> available = new HashSet<>(Arrays.asList(
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                               .getAvailableFontFamilyNames()));
        for (String f : candidates) {
            if (available.contains(f)) return f;
        }
        return "SansSerif"; 
    }

    public static void main(String[] args) {
        
        // 1. Escalar TODO (java base + flatlaf)
        // Puedes cambiar el 2.0 por 1.5 si resulta muy grande
        System.setProperty("sun.java2d.uiScale", "2.0");
        System.setProperty("flatlaf.uiScale", "2.0");

        // 2. Detectar y configurar LA ÚNICA fuente base. FlatLaf la escalará automáticamente.
        String fontName = detectFont();
        System.out.println("Font detectado: " + fontName);
        UIManager.put("defaultFont", new FontUIResource(new Font(fontName, Font.PLAIN, 14)));

        // 3. Cargar paleta de colores personalizada
        try {
            File propsFile = new File("flatlaf-custom.properties");
            if (propsFile.exists()) {
                Properties props = new Properties();
                props.load(new FileInputStream(propsFile));
                FlatLaf.setGlobalExtraDefaults((java.util.Map) props);
            }
        } catch (Exception e) {
            System.err.println("No se cargo flatlaf-custom.properties: " + e.getMessage());
        }

        // 4. Instalar tema claro
        FlatLightLaf.setup();

        // 5. Ajustes de forma
        UIManager.put("Button.arc",          10);
        UIManager.put("Component.arc",        8);
        UIManager.put("TabbedPane.tabHeight", 44);
        UIManager.put("Table.rowHeight",      32);
        UIManager.put("TextComponent.arc",     6);

        // 6. Inicializar BD
        DatabaseConnection.getInstance();

        // 7. Instanciar modelos
        ClientModel       clientModel      = new ClientModel();
        MuscleGroupModel  muscleGroupModel = new MuscleGroupModel();
        ExerciseModel     exerciseModel    = new ExerciseModel();
        TrainingPlanModel planModel        = new TrainingPlanModel();
        PlanDetailModel   detailModel      = new PlanDetailModel();
        MeasurementModel  measurementModel = new MeasurementModel();
        ReportModel       reportModel      = new ReportModel();

        // 8. Lanzar ventana
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(
                clientModel, muscleGroupModel, exerciseModel,
                planModel, detailModel, measurementModel, reportModel);
            frame.setVisible(true);
        });
    }
}
