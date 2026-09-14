package view;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReportView extends JPanel {

    private JComboBox<String>   cmbClient   = new JComboBox<>();
    private JList<String>       listPlans   = new JList<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JButton             btnGenerate = new JButton("Generate PDF");

    private List<Integer> clientCIs = new ArrayList<>();
    private List<Integer> planIds   = new ArrayList<>();

    public ReportView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.CREAM);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Panel Norte: Selector de cliente ---
        JPanel topPanel = UITheme.createSectionPanel("Select Client");
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        UITheme.styleComboBox(cmbClient);
        topPanel.add(UITheme.styledLabel("Client:"));
        topPanel.add(cmbClient);
        add(topPanel, BorderLayout.NORTH);

        // --- Panel Centro: Lista de planes (multi-selección) ---
        listPlans.setModel(listModel);
        listPlans.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listPlans.setFont(UITheme.LABEL_FONT);
        listPlans.setBackground(UITheme.WHITE);
        listPlans.setForeground(UITheme.TEXT);
        listPlans.setSelectionBackground(UITheme.GOLD);
        listPlans.setSelectionForeground(UITheme.TEXT);

        JPanel plansPanel = UITheme.createSectionPanel("Training Plans (select one or more)");
        plansPanel.setLayout(new BorderLayout());
        JScrollPane scrollPlans = new JScrollPane(listPlans);
        scrollPlans.getViewport().setBackground(UITheme.WHITE);
        plansPanel.add(scrollPlans, BorderLayout.CENTER);
        add(plansPanel, BorderLayout.CENTER);

        // --- Panel Sur: Botón generar ---
        UITheme.styleSaveButton(btnGenerate);
        add(UITheme.createButtonPanel(btnGenerate), BorderLayout.SOUTH);
    }

    // --- CARGA DE OPCIONES ---
    public void loadClientOptions(ResultSet rs) {
        cmbClient.removeAllItems();
        clientCIs.clear();
        try {
            if (rs != null) {
                while (rs.next()) {
                    int ci = rs.getInt("ci");
                    String name = ci + " - " + rs.getString("first_name") + " " + rs.getString("last_name");
                    clientCIs.add(ci);
                    cmbClient.addItem(name);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void loadPlanOptions(ResultSet rs) {
        listModel.clear();
        planIds.clear();
        try {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("id_plan");
                    String name = id + " - " + rs.getString("plan_name");
                    planIds.add(id);
                    listModel.addElement(name);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- GETTERS ---
    public int getSelectedClientCI() {
        int idx = cmbClient.getSelectedIndex();
        if (idx < 0 || idx >= clientCIs.size()) return -1;
        return clientCIs.get(idx);
    }

    public int[] getSelectedPlanIds() {
        int[] selectedIndices = listPlans.getSelectedIndices();
        int[] ids = new int[selectedIndices.length];
        for (int i = 0; i < selectedIndices.length; i++) {
            ids[i] = planIds.get(selectedIndices[i]);
        }
        return ids;
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Gym MVC", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- EXPONER COMPONENTES AL CONTROLLER ---
    public JComboBox<String> getCmbClient()   { return cmbClient; }
    public JButton           getBtnGenerate() { return btnGenerate; }
}
