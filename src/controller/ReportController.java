package controller;

import model.*;
import view.ReportView;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ReportController {

    private ReportView view;
    private ReportModel reportModel;
    private ClientModel clientModel;
    private TrainingPlanModel planModel;

    public ReportController(ReportView view,
                            ReportModel reportModel,
                            ClientModel clientModel,
                            TrainingPlanModel planModel) {
        this.view        = view;
        this.reportModel = reportModel;
        this.clientModel = clientModel;
        this.planModel   = planModel;
        loadClients();
        view.getCmbClient().addActionListener(e ->
            loadPlansByClient(view.getSelectedClientCI()));
        view.getBtnGenerate().addActionListener(e -> generateReport());
    }

    public void loadClients() {
        view.loadClientOptions(clientModel.getAll());
    }

    public void loadPlansByClient(int ci) {
        if (ci <= 0) return;
        view.loadPlanOptions(planModel.getByClient(ci));
    }

    public void generateReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        chooser.setSelectedFile(new File("training_report.pdf"));
        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;
        String filePath = chooser.getSelectedFile().getAbsolutePath();
        int ci = view.getSelectedClientCI();
        int[] planIds = view.getSelectedPlanIds();
        if (planIds.length == 0) {
            view.showMessage("Select at least one training plan.");
            return;
        }
        boolean ok = reportModel.generatePDF(
            reportModel.getClientInfo(ci),
            reportModel.getPlanWithDetails(planIds[0]),
            filePath);
        view.showMessage(ok ? "PDF generated: " + filePath : "Error generating PDF.");
    }
}
