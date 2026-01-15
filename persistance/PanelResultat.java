package persistance;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PanelResultat extends JPanel {
    public JTextArea zoneObstacles;
    public JTextArea zoneResultat;

    public PanelResultat() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(350, 700));
        setBackground(new Color(248, 248, 255));
        
        // Bordure principale
        TitledBorder mainBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
            "📊 Résultats de l'Analyse",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(0, 100, 0)
        );
        setBorder(BorderFactory.createCompoundBorder(
            mainBorder,
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Section Obstacles
        add(creerTitreSection("⚠️ Obstacles Détectés"));
        zoneObstacles = creerZoneTexteStylee(6);
        JScrollPane scrollObstacles = creerScrollPaneStylee(zoneObstacles);
        scrollObstacles.setMaximumSize(new Dimension(330, 150));
        add(scrollObstacles);
        
        add(Box.createVerticalStrut(15));
        
        // Séparateur décoratif
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(60, 179, 113));
        separator.setMaximumSize(new Dimension(330, 2));
        add(separator);
        
        add(Box.createVerticalStrut(15));

        // Section Résultats
        add(creerTitreSection("✅ Calcul des Trajets"));
        zoneResultat = creerZoneTexteStylee(15);
        JScrollPane scrollResultat = creerScrollPaneStylee(zoneResultat);
        add(scrollResultat);

        add(Box.createVerticalStrut(10));
    }

    private JLabel creerTitreSection(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(new Color(25, 25, 112));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Panneau avec fond coloré
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setMaximumSize(new Dimension(330, 25));
        panel.setBackground(new Color(230, 240, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 149, 237), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        panel.add(label);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        return label;
    }

    private JTextArea creerZoneTexteStylee(int rows) {
        JTextArea textArea = new JTextArea(rows, 28);
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(new Color(255, 255, 255));
        textArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return textArea;
    }

    private JScrollPane creerScrollPaneStylee(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(176, 196, 222), 2),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Personnaliser la scrollbar
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 149, 237);
                this.trackColor = new Color(240, 248, 255);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton btn = super.createDecreaseButton(orientation);
                btn.setBackground(new Color(176, 196, 222));
                return btn;
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton btn = super.createIncreaseButton(orientation);
                btn.setBackground(new Color(176, 196, 222));
                return btn;
            }
        });
        
        return scrollPane;
    }
}