package persistance;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PanelObstacle extends JPanel {
    public JTextField txtKmDepart;
    public JTextField txtKmArrivee;
    public JTextField txtTaux;
    public JButton btnAjouterObstacle;

    public PanelObstacle() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        setBackground(new Color(255, 250, 240));
        setPreferredSize(new Dimension(1200, 80));
        
        // Bordure moderne
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0), 2),
            "⚠️ Gestion des Obstacles",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 13),
            new Color(178, 34, 34)
        );
        setBorder(titledBorder);

        // Kilomètre départ
        add(creerLabel("📏 Km Départ:"));
        txtKmDepart = creerTextField(8);
        txtKmDepart.setToolTipText("Kilomètre de début de l'obstacle");
        add(txtKmDepart);

        // Séparateur visuel
        add(creerSeparateur());

        // Kilomètre fin
        add(creerLabel("📏 Km Fin:"));
        txtKmArrivee = creerTextField(8);
        txtKmArrivee.setToolTipText("Kilomètre de fin de l'obstacle");
        add(txtKmArrivee);

        // Séparateur visuel
        add(creerSeparateur());

        // Taux de ralentissement
        add(creerLabel("🐌 Ralentissement (%):"));
        txtTaux = creerTextField(6);
        txtTaux.setToolTipText("Pourcentage de ralentissement (1-100)");
        add(txtTaux);

        // Séparateur visuel
        add(creerSeparateur());

        // Bouton ajouter
        btnAjouterObstacle = creerBoutonAjouter();
        add(btnAjouterObstacle);
    }

    private JLabel creerLabel(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(new Color(139, 69, 19));
        return label;
    }

    private JTextField creerTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setPreferredSize(new Dimension(columns * 12, 28));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0), 1),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
        return field;
    }

    private JButton creerBoutonAjouter() {
        JButton btn = new JButton("➕ Ajouter Obstacle");
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(220, 20, 60));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(160, 32));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 69, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(220, 20, 60));
            }
        });
        
        return btn;
    }

    private JSeparator creerSeparateur() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 30));
        sep.setForeground(new Color(255, 140, 0));
        return sep;
    }
}