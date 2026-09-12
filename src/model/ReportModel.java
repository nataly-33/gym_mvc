package model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.sql.*;

public class ReportModel {

    private Connection connection;

    public ReportModel() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public ResultSet getClientInfo(int ci) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM client WHERE ci = ?");
            ps.setInt(1, ci);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getPlanWithDetails(int planId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT tp.plan_name, tp.date, tp.objective, " +
                "e.name AS exercise_name, e.video_url, " +
                "pd.sets, pd.reps, pd.rest_time, pd.exercise_order " +
                "FROM training_plan tp " +
                "JOIN plan_detail pd ON tp.id_plan = pd.id_plan " +
                "JOIN exercise e ON pd.id_exercise = e.id_exercise " +
                "WHERE tp.id_plan = ? ORDER BY pd.exercise_order");
            ps.setInt(1, planId);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public boolean generatePDF(ResultSet clientInfo, ResultSet planDetails,
                               String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Font titleFont  = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

            document.add(new Paragraph("GYM TRAINING REPORT", titleFont));
            document.add(Chunk.NEWLINE);

            if (clientInfo != null && clientInfo.next()) {
                document.add(new Paragraph(
                    "Client: " + clientInfo.getString("first_name") +
                    " " + clientInfo.getString("last_name"), headerFont));
                document.add(new Paragraph("CI: " + clientInfo.getInt("ci"), normalFont));
                document.add(Chunk.NEWLINE);
            }

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            for (String h : new String[]{"Exercise","Sets","Reps","Rest(s)","Video URL"}) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            String currentPlan = "";
            while (planDetails != null && planDetails.next()) {
                String planName = planDetails.getString("plan_name");
                if (!planName.equals(currentPlan)) {
                    currentPlan = planName;
                    PdfPCell planCell = new PdfPCell(
                        new Phrase("Plan: " + currentPlan +
                                   " - " + planDetails.getString("date"), headerFont));
                    planCell.setColspan(5);
                    table.addCell(planCell);
                }
                table.addCell(new Phrase(planDetails.getString("exercise_name"), normalFont));
                table.addCell(new Phrase(String.valueOf(planDetails.getInt("sets")), normalFont));
                table.addCell(new Phrase(String.valueOf(planDetails.getInt("reps")), normalFont));
                table.addCell(new Phrase(String.valueOf(planDetails.getInt("rest_time")), normalFont));
                String url = planDetails.getString("video_url");
                table.addCell(new Phrase(url != null ? url : "-", normalFont));
            }
            document.add(table);
            document.close();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
