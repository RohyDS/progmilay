package persistance;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import model.Point;
import model.Voiture;

public class PanelConfiguration extends JPanel {
    public JComboBox<Voiture> comboVoiture;
    public JComboBox<Point> comboDepart;
    public JComboBox<Point> comboArrivee;
    public JButton btnCalculer;
    private JTextField txtVitesseMoyenne;
    private JTextField txtHeureDepart;  // Nouveau champ pour l'heure de départ

    public PanelConfiguration(List<Voiture> voitures, List<Point> points) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(320, 700));
        setBackground(new Color(240, 248, 255));
        
        // Bordure moderne avec titre stylé
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "⚙️ Configuration du Trajet",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(25, 25, 112)
        );
        setBorder(BorderFactory.createCompoundBorder(
            titledBorder,
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Section Voiture
        add(creerSectionLabel("🚗 Sélection de la Voiture"));
        comboVoiture = creerComboBoxStylee(voitures.toArray(new Voiture[0]));
        add(comboVoiture);
        add(Box.createVerticalStrut(20));

        // Section Vitesse
        add(creerSectionLabel("⚡ Vitesse Moyenne (km/h)"));
        txtVitesseMoyenne = creerTextFieldStylee();
        txtVitesseMoyenne.setToolTipText("Entrez la vitesse moyenne souhaitée");
        add(txtVitesseMoyenne);
        add(Box.createVerticalStrut(20));

        // Section Heure de départ
        add(creerSectionLabel("🕐 Heure de Départ (HH:mm)"));
        txtHeureDepart = creerTextFieldStylee();
        txtHeureDepart.setText("08:00");  // Valeur par défaut
        txtHeureDepart.setToolTipText("Format : HH:mm (ex: 08:00, 14:30)");
        add(txtHeureDepart);
        add(Box.createVerticalStrut(20));

        // Section Point de départ
        add(creerSectionLabel("📍 Point de Départ"));
        comboDepart = creerComboBoxStylee(points.toArray(new Point[0]));
        add(comboDepart);
        add(Box.createVerticalStrut(20));

        // Section Point d'arrivée
        add(creerSectionLabel("🎯 Point d'Arrivée"));
        comboArrivee = creerComboBoxStylee(points.toArray(new Point[0]));
        add(comboArrivee);
        add(Box.createVerticalStrut(30));

        // Bouton calculer stylé
        btnCalculer = creerBoutonStylee("🔍 Calculer le Trajet Optimal");
        add(btnCalculer);
        
        add(Box.createVerticalGlue());
    }

    private JLabel creerSectionLabel(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(25, 25, 112));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private <T> JComboBox<T> creerComboBoxStylee(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        combo.setMaximumSize(new Dimension(280, 30));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        return combo;
    }

    private JTextField creerTextFieldStylee() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setMaximumSize(new Dimension(280, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private JButton creerBoutonStylee(String texte) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(70, 130, 180));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(280, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(100, 149, 237));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(70, 130, 180));
            }
        });
        
        return btn;
    }

    public JTextField getTxtVitesseMoyenne() {
        return txtVitesseMoyenne;
    }
    
    public JTextField getTxtHeureDepart() {
        return txtHeureDepart;
    }
}