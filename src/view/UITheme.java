package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class UITheme {

    public static final Color GOLD        = new Color(0xDFAC13);
    public static final Color ORANGE_SOFT = new Color(0xF0A56C);
    public static final Color YELLOW      = new Color(0xF0D876);
    public static final Color CREAM       = new Color(0xF0E1C4);
    public static final Color ORANGE_DARK = new Color(0xE88A4C);
    public static final Color TEXT        = new Color(0x1A1A1A);
    public static final Color WHITE       = Color.WHITE;

    public static final Font TITLE_FONT  = new Font("SansSerif", Font.BOLD, 20);
    public static final Font LABEL_FONT  = new Font("SansSerif", Font.PLAIN, 15);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 15);
    public static final Font TABLE_FONT  = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);

    public static final Dimension BUTTON_SIZE = new Dimension(145, 42);

    public static void styleSaveButton(JButton btn) {
        btn.setBackground(GOLD);
        btn.setForeground(TEXT);
        btn.setFont(BUTTON_FONT);
        btn.setPreferredSize(BUTTON_SIZE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    public static void styleUpdateButton(JButton btn) {
        btn.setBackground(ORANGE_SOFT);
        btn.setForeground(WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setPreferredSize(BUTTON_SIZE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    public static void styleDeleteButton(JButton btn) {
        btn.setBackground(ORANGE_DARK);
        btn.setForeground(WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setPreferredSize(BUTTON_SIZE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    public static void styleSecondaryButton(JButton btn) {
        btn.setBackground(YELLOW);
        btn.setForeground(TEXT);
        btn.setFont(BUTTON_FONT);
        btn.setPreferredSize(BUTTON_SIZE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(LABEL_FONT);
        combo.setBackground(WHITE);
        combo.setForeground(TEXT);
    }

    public static JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(TEXT);
        return lbl;
    }

    public static JLabel styledTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(TITLE_FONT);
        lbl.setForeground(TEXT);
        return lbl;
    }

    public static void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(32);
        table.setSelectionBackground(GOLD);
        table.setSelectionForeground(WHITE);
        table.setGridColor(new Color(0xD4C4A0));
        table.setBackground(WHITE);
        table.setForeground(TEXT);
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(YELLOW);
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
    }

    // Panel con borde titulado dorado
    public static JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(CREAM);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GOLD, 2),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 15),
            TEXT));
        return panel;
    }

    // Panel de botones centrado
    public static JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        panel.setBackground(CREAM);
        for (JButton btn : buttons) panel.add(btn);
        return panel;
    }

    // Fila de formulario con label + campo (util para buildFormPanel)
    // Usar en un JPanel con GridBagLayout
    public static void addFormRow(JPanel panel, GridBagConstraints gbc,
                                   int row, String label, JComponent field) {
        gbc.gridy     = row;
        gbc.gridx     = 0;
        gbc.weightx   = 0;
        gbc.fill      = GridBagConstraints.NONE;
        gbc.anchor    = GridBagConstraints.WEST;
        gbc.insets    = new Insets(6, 12, 6, 6);
        panel.add(styledLabel(label), gbc);

        gbc.gridx     = 1;
        gbc.weightx   = 1;
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.insets    = new Insets(6, 0, 6, 12);
        panel.add(field, gbc);
    }

    // Dos pares label+campo en la misma fila
    public static void addFormRow2(JPanel panel, GridBagConstraints gbc,
                                    int row,
                                    String label1, JComponent field1,
                                    String label2, JComponent field2) {
        gbc.gridy     = row;
        gbc.gridx     = 0;
        gbc.weightx   = 0;
        gbc.fill      = GridBagConstraints.NONE;
        gbc.anchor    = GridBagConstraints.WEST;
        gbc.insets    = new Insets(6, 12, 6, 6);
        panel.add(styledLabel(label1), gbc);

        gbc.gridx     = 1;
        gbc.weightx   = 0.4;
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.insets    = new Insets(6, 0, 6, 20);
        panel.add(field1, gbc);

        gbc.gridx     = 2;
        gbc.weightx   = 0;
        gbc.fill      = GridBagConstraints.NONE;
        gbc.insets    = new Insets(6, 0, 6, 6);
        panel.add(styledLabel(label2), gbc);

        gbc.gridx     = 3;
        gbc.weightx   = 0.4;
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.insets    = new Insets(6, 0, 6, 12);
        panel.add(field2, gbc);
    }
}
