package persistance;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

import dao.*;
import fonction.*;
import model.*;
import model.Point;

public class FenetreSimulation extends JFrame {

    private PanelAffichage panelAffichage;
    private PanelConfiguration panelConfig;
    private PanelResultat panelResultat;
    private PanelObstacle panelObstacle;

    private List<Route> routes;
    private List<Voiture> voitures;
    private List<Point> points;

    private Connection oracleConn;
    private Connection postgresConn;

    public FenetreSimulation() throws Exception {
        super("Simulation trajet voiture");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        oracleConn = OracleDB.getConnection();
        postgresConn = PostgresBD.getConnection();

        points   = PointDAO.getAllPoints(oracleConn);
        routes   = RouteDAO.getAllRoutes(oracleConn, points);
        voitures = VoitureDAO.getAllVoitures(postgresConn);

        panelAffichage = new PanelAffichage(routes, points);
        panelConfig    = new PanelConfiguration(voitures, points);
        panelResultat  = new PanelResultat();
        panelObstacle  = new PanelObstacle();

        add(panelAffichage, BorderLayout.CENTER);
        add(panelConfig, BorderLayout.WEST);
        add(panelResultat, BorderLayout.EAST);
        add(panelObstacle, BorderLayout.SOUTH);

        panelConfig.btnCalculer.addActionListener(e -> calculerDureeTrajet());
        panelObstacle.btnAjouterObstacle.addActionListener(e -> ajouterObstacle());
    }

    private void calculerDureeTrajet() {
        panelResultat.zoneResultat.setText("");
        panelResultat.zoneObstacles.setText("");

        Voiture voiture = (Voiture) panelConfig.comboVoiture.getSelectedItem();
        Point depart    = (Point) panelConfig.comboDepart.getSelectedItem();
        Point arrivee   = (Point) panelConfig.comboArrivee.getSelectedItem();
        String heureDepart = panelConfig.getTxtHeureDepart().getText();

        // Vérifications
        if (voiture == null || depart == null || arrivee == null) {
            panelResultat.zoneResultat.setText("❌ Sélection incomplète");
            return;
        }

        if (!heureDepart.matches("^[0-2][0-9]:[0-5][0-9]$")) {
            panelResultat.zoneResultat.setText("❌ Format d'heure invalide ! Utilisez HH:mm");
            return;
        }

        double vitesseMoyenne;
        try {
            vitesseMoyenne = Double.parseDouble(panelConfig.getTxtVitesseMoyenne().getText());
        } catch (NumberFormatException e) {
            panelResultat.zoneResultat.setText("❌ Vitesse moyenne invalide !");
            return;
        }

        if (vitesseMoyenne <= 0 || vitesseMoyenne > voiture.getVitesseMax()) {
            panelResultat.zoneResultat.setText(
                "❌ Vitesse doit être > 0 et ≤ " + voiture.getVitesseMax()
            );
            return;
        }

        voiture.setVitesseMoyenne(vitesseMoyenne);

        // Affichage info voiture
        panelResultat.zoneResultat.append(
            "🚗 Voiture : " + voiture.getNom() + " (" + voiture.getType() + ")\n" +
            "⚡ Vitesse max : " + voiture.getVitesseMax() + " km/h | " +
            "Vitesse moyenne : " + vitesseMoyenne + " km/h\n" +
            "⛽ Réservoir : " + voiture.getReservoir() + " L | " +
            "Consommation : " + voiture.getConsommation() + " L/100km\n" +
            "🕐 Heure de départ : " + heureDepart + "\n\n"
        );

        try {
            List<Obstacle> tousObstacles = ObstacleDAO.getAllObstacles(oracleConn);
            List<Pause> toutesPauses = PauseDAO.getAllPauses(oracleConn);

            List<List<Route>> chemins = DfsService.trouverTousLesChemins(depart, arrivee);
            if (chemins.isEmpty()) {
                panelResultat.zoneResultat.append("❌ Aucun chemin trouvé !");
                return;
            }

            double meilleureDuree = Double.MAX_VALUE;
            int indexMeilleurChemin = -1;
            int num = 1;

            // Analyse de chaque chemin
            for (List<Route> chemin : chemins) {
                ResultatTrajet resultat = Fonction.calculerHeureArrivee(
                    voiture, chemin, tousObstacles, toutesPauses, heureDepart
                );

                double distanceTotale = chemin.stream().mapToDouble(Route::getDistance).sum();
                double carburantNecessaire = voiture.calculerConsommationPourDistance(distanceTotale);
                boolean carburantSuffisant = carburantNecessaire <= voiture.getReservoir();
                
                // Calculer vitesse moyenne réelle du chemin complet
                double vitesseMoyenneReelleChemin = voiture.calculerVitesseMoyenneReelleChemin(chemin, tousObstacles);

                panelResultat.zoneResultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                panelResultat.zoneResultat.append("📍 Chemin " + num + " :\n");

                // Affichage routes, obstacles et pauses
                for (Route r : chemin) {
                    // Calculer vitesse moyenne réelle pour cette route
                    double vitesseMoyenneReelle = voiture.calculerVitesseMoyenneReelle(r, tousObstacles);
                    
                    panelResultat.zoneResultat.append(
                        "  📌 Route : " + r.getNom() + " (" + r.getDistance() + " km)\n"
                    );

                    // Obstacles
                    List<Obstacle> obstaclesRoute = new ArrayList<>();
                    for (Obstacle o : tousObstacles) {
                        if (o.getIdRoute() == r.getId()) obstaclesRoute.add(o);
                    }

                    if (!obstaclesRoute.isEmpty()) {
                        for (Obstacle o : obstaclesRoute) {
                            panelResultat.zoneResultat.append(
                                "    ⚠️ Obstacle : " + o.getTauxRetablissement() + "% | " +
                                "km " + o.getKilometreDepart() + " → " + o.getKilometreArrivee() + "\n"
                            );
                        }
                        // Afficher la vitesse moyenne réelle impactée par les obstacles
                        panelResultat.zoneResultat.append(
                            "    📊 Vitesse moyenne réelle : " + String.format("%.2f km/h", vitesseMoyenneReelle) + 
                            " (impact: " + String.format("%.1f%%", ((vitesseMoyenne - vitesseMoyenneReelle) / vitesseMoyenne * 100)) + ")\n"
                        );
                    } else {
                        panelResultat.zoneResultat.append("    ✅ Aucun obstacle\n");
                    }

                    // ✅ CORRECTION : Pauses avec statut - extraction correcte de la durée
                    List<Pause> pausesRoute = new ArrayList<>();
                    for (Pause p : toutesPauses) {
                        if (p.getIdRoute() == r.getId()) pausesRoute.add(p);
                    }

                    for (Pause p : pausesRoute) {
                        boolean pauseAcceptee = false;
                        int dureeEffective = 0;

                        // Rechercher si cette pause a été prise
                        for (String detail : resultat.detailsPauses) {
                            if (detail.contains(r.getNom()) && 
                                detail.contains("km " + String.format("%.1f", p.getPositionKm()))) {
                                pauseAcceptee = true;
                                
                                // ✅ EXTRACTION CORRIGÉE : Format "(42 min)" au lieu de "(42 min effective)"
                                int debut = detail.lastIndexOf('(');
                                int fin = detail.lastIndexOf(" min)");
                                
                                if (debut != -1 && fin != -1 && fin > debut) {
                                    try {
                                        String dureePart = detail.substring(debut + 1, fin).trim();
                                        dureeEffective = (int) Double.parseDouble(dureePart);
                                    } catch (Exception ex) {
                                        System.err.println("⚠️ Erreur parsing durée pause : " + ex.getMessage());
                                        ex.printStackTrace();
                                    }
                                }
                                break;
                            }
                        }

                        if (pauseAcceptee) {
                            panelResultat.zoneResultat.append(
                                "    ✅ Pause acceptée : km " + p.getPositionKm() + " | " +
                                p.getHeureDebut() + " → " + p.getHeureFin() +
                                " (prévue: " + p.getDureeMinutes() + " min, effective: " + 
                                dureeEffective + " min)\n"
                            );
                        } else {
                            panelResultat.zoneResultat.append(
                                "    ❌ Pause NON prise : km " + p.getPositionKm() + " | " +
                                p.getHeureDebut() + " → " + p.getHeureFin() +
                                " (" + p.getDureeMinutes() + " min) - hors période\n"
                            );
                        }
                    }
                }

                // Résumé
                panelResultat.zoneResultat.append(resultat.getResume() + "\n");
                panelResultat.zoneResultat.append(
                    "  ├─ 📏 Distance : " + String.format("%.2f km", distanceTotale) + "\n" +
                    "  ├─ 🚀 Vitesse moyenne réelle : " + String.format("%.2f km/h", vitesseMoyenneReelleChemin) + "\n" +
                    "  ├─ ⛽ Carburant : " + String.format("%.2f L", carburantNecessaire) + "\n" +
                    "  └─ " + (carburantSuffisant ? 
                        "✅ Suffisant (reste " + String.format("%.2f L", 
                            voiture.getReservoir() - carburantNecessaire) + ")" : 
                        "❌ Insuffisant (manque " + String.format("%.2f L", 
                            carburantNecessaire - voiture.getReservoir()) + ")") + "\n\n"
                );

                if (resultat.dureeTotal < meilleureDuree) {
                    meilleureDuree = resultat.dureeTotal;
                    indexMeilleurChemin = num - 1;
                }
                num++;
            }

            panelResultat.zoneResultat.append(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "🏆 Chemin le plus rapide : Chemin " + (indexMeilleurChemin + 1) +
                " (" + Fonction.formaterDuree(meilleureDuree) + ")\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n"
            );

            // Choix chemin
            String[] choix = new String[chemins.size() + 1];
            for (int i = 0; i < chemins.size(); i++) choix[i] = "Chemin " + (i + 1);
            choix[chemins.size()] = "Chemin le plus rapide";

            String selection = (String) JOptionPane.showInputDialog(
                this, "Quel chemin voulez-vous animer ?", "Choix du chemin",
                JOptionPane.QUESTION_MESSAGE, null, choix, choix[chemins.size()]
            );

            int indexChoisi = (selection == null || selection.contains("plus rapide")) ?
                indexMeilleurChemin : Integer.parseInt(selection.replace("Chemin ", "")) - 1;

            List<Route> cheminChoisi = chemins.get(indexChoisi);

            // Vérifications
            String erreurCarburant = VoitureDAO.verifierCarburantSuffisant(voiture, cheminChoisi);
            if (erreurCarburant != null) {
                JOptionPane.showMessageDialog(this, erreurCarburant,
                    "Carburant Insuffisant", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String erreurLargeur = VoitureDAO.verifierLargeurVoiture(voiture, cheminChoisi);
            if (erreurLargeur != null) {
                JOptionPane.showMessageDialog(this, erreurLargeur,
                    "Voiture trop large", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Animation
            panelAffichage.demarrerAnimation(cheminChoisi, voiture, depart, tousObstacles);

        } catch (Exception ex) {
            panelResultat.zoneResultat.append("❌ Erreur : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void ajouterObstacle() {
        try {
            Point depart = (Point) panelConfig.comboDepart.getSelectedItem();
            Point arrivee = (Point) panelConfig.comboArrivee.getSelectedItem();
            
            if (depart == null || arrivee == null) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner départ et arrivée.");
                return;
            }

            List<List<Route>> chemins = DfsService.trouverTousLesChemins(depart, arrivee);
            if (chemins.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun chemin trouvé !");
                return;
            }

            List<Route> toutesRoutes = new ArrayList<>();
            for (List<Route> chemin : chemins) {
                for (Route r : chemin) {
                    if (!toutesRoutes.contains(r)) toutesRoutes.add(r);
                }
            }

            String[] nomsRoutes = toutesRoutes.stream()
                .map(Route::getNom).toArray(String[]::new);

            String routeChoisie = (String) JOptionPane.showInputDialog(
                this, "Choisissez la route :", "Ajouter obstacle",
                JOptionPane.QUESTION_MESSAGE, null, nomsRoutes, nomsRoutes[0]
            );

            if (routeChoisie == null) return;

            Route route = toutesRoutes.stream()
                .filter(r -> r.getNom().equals(routeChoisie))
                .findFirst().orElse(null);

            if (route == null) return;

            double kmDepart = Double.parseDouble(panelObstacle.txtKmDepart.getText());
            double kmArrivee = Double.parseDouble(panelObstacle.txtKmArrivee.getText());
            double taux = Double.parseDouble(panelObstacle.txtTaux.getText());

            Obstacle o = new Obstacle();
            o.setIdRoute(route.getId());
            o.setKilometreDepart(kmDepart);
            o.setKilometreArrivee(kmArrivee);
            o.setTauxRetablissement(taux);

            ObstacleDAO.ajouterObstacle(oracleConn, o);
            JOptionPane.showMessageDialog(this, "✅ Obstacle ajouté");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valeurs numériques invalides !");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new FenetreSimulation().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}