package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class ReportView extends JPanel {

    private JComboBox<Object[]> cmbClient = new JComboBox<>();
    private JList<Object[]>     lstPlans  = new JList<>();
    private JButton             btnGenerate = new JButton("Generate PDF Report");

    public ReportView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Panel Norte: Selector de cliente ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topPanel.setBorder(BorderFactory.createTitledBorder("Select Client"));
        topPanel.add(new JLabel("Client:"));
        topPanel.add(cmbClient);
        add(topPanel, BorderLayout.NORTH);

        // --- Panel Centro: Lista de planes (multi-seleccion) ---
        lstPlans.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lstPlans.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value instanceof Object[]) lbl.setText(((Object[]) value)[1].toString());
            if (isSelected) { lbl.setBackground(list.getSelectionBackground()); lbl.setOpaque(true); }
            return lbl;
        });
        JPanel plansPanel = new JPanel(new BorderLayout());
        plansPanel.setBorder(BorderFactory.createTitledBorder("Training Plans (select one or more)"));
        plansPanel.add(new JScrollPane(lstPlans), BorderLayout.CENTER);
        add(plansPanel, BorderLayout.CENTER);

        // --- Panel Sur: Boton generar ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnPanel.add(btnGenerate);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // --- Carga de combos y listas desde el Controller ---
    public void loadClientOptions(ResultSet rs) {
        cmbClient.removeAllItems();
        try {
            if (rs != null) {
                while (rs.next()) {
                    final int ci = rs.getInt("ci");
                    final String name = rs.getString("first_name") + " " + rs.getString("last_name");
                    cmbClient.addItem(new Object[]{ci, name});
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        cmbClient.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value instanceof Object[]) lbl.setText(((Object[]) value)[1].toString());
            if (isSelected) { lbl.setBackground(list.getSelectionBackground()); lbl.setOpaque(true); }
            return lbl;
        });
    }

    public void loadPlanOptions(ResultSet rs) {
        DefaultListModel<Object[]> listModel = new DefaultListModel<>();
        try {
            if (rs != null) {
                while (rs.next()) {
                    final int id = rs.getInt("id_plan");
                    final String name = rs.getString("plan_name") + " (" + rs.getString("date") + ")";
                    listModel.addElement(new Object[]{id, name});
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        lstPlans.setModel(listModel);
    }

    // --- Getters para el Controller ---
    public int getSelectedClientCI() {
        Object[] sel = (Object[]) cmbClient.getSelectedItem();
        return (sel != null) ? (int) sel[0] : -1;
    }

    public int[] getSelectedPlanIds() {
        List<Object[]> selected = lstPlans.getSelectedValuesList();
        int[] ids = new int[selected.size()];
        for (int i = 0; i < selected.size(); i++) ids[i] = (int) selected.get(i)[0];
        return ids;
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    // --- Exponer componentes para el Controller ---
    public JComboBox<Object[]> getCmbClient()   { return cmbClient; }
    public JButton             getBtnGenerate() { return btnGenerate; }
}
