package persistance;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.ArrayList;

import model.Route;
import model.Point;
import model.Voiture;
import model.Obstacle;
import model.Pause;
import model.VoitureAffichee;

public class PanelAffichage extends JPanel {

    private List<Route> routes;
    private List<Point> points;
    private List<VoitureAffichee> voituresAffichees = new ArrayList<>();
    private List<Obstacle> tousObstacles = new ArrayList<>();

    public PanelAffichage(List<Route> routes, List<Point> points) {
        this.routes = routes;
        this.points = points;
        setBackground(new Color(50, 150, 80));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Antialiasing maximal pour un rendu ultra-lisse
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        dessinerRoutes(g2d);
        dessinerVoitures(g2d);
    }

    // ===========================
    // Dessiner routes ultra stylées
    // ===========================
    private void dessinerRoutes(Graphics2D g2d) {
        if (routes.isEmpty() || points.isEmpty()) return;

        double minX = points.stream().mapToDouble(Point::getX).min().orElse(0);
        double minY = points.stream().mapToDouble(Point::getY).min().orElse(0);
        double maxX = points.stream().mapToDouble(Point::getX).max().orElse(1);
        double maxY = points.stream().mapToDouble(Point::getY).max().orElse(1);
        int panelWidth = getWidth() - 60;
        int panelHeight = getHeight() - 60;

        // Dessiner chaque route
        for (Route r : routes) {
            if (r.getDepart() == null || r.getArrivee() == null) continue;

            int x1 = (int) ((r.getDepart().getX() - minX) / (maxX - minX) * panelWidth) + 30;
            int y1 = (int) ((r.getDepart().getY() - minY) / (maxY - minY) * panelHeight) + 30;
            int x2 = (int) ((r.getArrivee().getX() - minX) / (maxX - minX) * panelWidth) + 30;
            int y2 = (int) ((r.getArrivee().getY() - minY) / (maxY - minY) * panelHeight) + 30;

            // Vérifier si cette route a des obstacles
            List<Obstacle> obstaclesRoute = new ArrayList<>();
            for (Obstacle o : tousObstacles) {
                if (o.getIdRoute() == r.getId()) {
                    obstaclesRoute.add(o);
                }
            }
            boolean aObstacle = !obstaclesRoute.isEmpty();

            // Largeur de la route augmentée (min 20, max 50)
            int largeurRoute = Math.min(50, Math.max(20, (int)(r.getLargeur() / 1.5)));
            
            // Dessiner l'ombre de la route pour effet 3D
            g2d.setStroke(new BasicStroke(largeurRoute + 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.drawLine(x1 + 2, y1 + 2, x2 + 2, y2 + 2);

            // Dessiner la route principale
            g2d.setStroke(new BasicStroke(largeurRoute, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            if (aObstacle) {
                // Dégradé rouge pour route avec obstacle
                GradientPaint gradient = new GradientPaint(x1, y1, new Color(200, 50, 50), x2, y2, new Color(150, 30, 30));
                g2d.setPaint(gradient);
            } else {
                // Dégradé gris pour route normale
                GradientPaint gradient = new GradientPaint(x1, y1, new Color(70, 70, 70), x2, y2, new Color(50, 50, 50));
                g2d.setPaint(gradient);
            }
            g2d.drawLine(x1, y1, x2, y2);

            // Bordures de route
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(new Color(200, 200, 200));
            
            double angle = Math.atan2(y2 - y1, x2 - x1);
            int offsetX = (int)(Math.sin(angle) * largeurRoute / 2);
            int offsetY = (int)(-Math.cos(angle) * largeurRoute / 2);
            
            g2d.drawLine(x1 + offsetX, y1 + offsetY, x2 + offsetX, y2 + offsetY);
            g2d.drawLine(x1 - offsetX, y1 - offsetY, x2 - offsetX, y2 - offsetY);

            // Lignes blanches pointillées au centre (plus épaisses)
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
                          10.0f, new float[]{15.0f, 12.0f}, 0.0f));
            g2d.setColor(Color.WHITE);
            g2d.drawLine(x1, y1, x2, y2);

            // Nom de la route sur la route
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;
            
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(r.getNom());
            
            // Fond pour le nom de route
            g2d.setColor(new Color(40, 40, 40, 200));
            g2d.fillRoundRect(midX - textWidth/2 - 6, midY - 18, textWidth + 12, 20, 8, 8);
            
            g2d.setColor(Color.WHITE);
            g2d.drawString(r.getNom(), midX - textWidth/2, midY - 4);

            // Dessiner les obstacles sur la route de manière très visible
            for (Obstacle o : obstaclesRoute) {
                double tDepart = o.getKilometreDepart() / r.getDistance();
                double tFin = o.getKilometreArrivee() / r.getDistance();
                
                int xObstDepart = (int)(x1 + tDepart * (x2 - x1));
                int yObstDepart = (int)(y1 + tDepart * (y2 - y1));
                int xObstFin = (int)(x1 + tFin * (x2 - x1));
                int yObstFin = (int)(y1 + tFin * (y2 - y1));
                
                // Zone d'obstacle en orange vif
                g2d.setStroke(new BasicStroke(largeurRoute - 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(new Color(255, 140, 0, 180));
                g2d.drawLine(xObstDepart, yObstDepart, xObstFin, yObstFin);
                
                // Hachures pour effet travaux
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(new Color(255, 200, 0));
                for (int i = 0; i < 5; i++) {
                    double t = tDepart + (tFin - tDepart) * i / 5.0;
                    int xh = (int)(x1 + t * (x2 - x1));
                    int yh = (int)(y1 + t * (y2 - y1));
                    g2d.drawLine(xh - 8, yh - 8, xh + 8, yh + 8);
                    g2d.drawLine(xh - 8, yh + 8, xh + 8, yh - 8);
                }
            }

            // Triangle d'avertissement pour obstacles
            if (aObstacle) {
                g2d.setColor(new Color(255, 220, 0));
                g2d.setStroke(new BasicStroke(3));
                
                int[] xTriangle = {midX, midX - 12, midX + 12};
                int[] yTriangle = {midY + 15, midY + 35, midY + 35};
                g2d.fillPolygon(xTriangle, yTriangle, 3);
                
                g2d.setColor(Color.BLACK);
                g2d.drawPolygon(xTriangle, yTriangle, 3);
                
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("!", midX - 4, midY + 32);
            }
        }

        // Dessiner les points (intersections/villes) - plus gros
        for (Point p : points) {
            int x = (int) ((p.getX() - minX) / (maxX - minX) * panelWidth) + 30;
            int y = (int) ((p.getY() - minY) / (maxY - minY) * panelHeight) + 30;

            // Ombre du point
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillOval(x - 14, y - 12, 28, 28);

            // Cercle extérieur doré
            g2d.setColor(new Color(255, 215, 0));
            g2d.fillOval(x - 14, y - 14, 28, 28);
            
            // Cercle moyen blanc
            g2d.setColor(Color.WHITE);
            g2d.fillOval(x - 12, y - 12, 24, 24);
            
            // Cercle intérieur rouge vif
            g2d.setColor(new Color(255, 60, 60));
            g2d.fillOval(x - 9, y - 9, 18, 18);

            // Point central blanc
            g2d.setColor(Color.WHITE);
            g2d.fillOval(x - 3, y - 3, 6, 6);

            // Nom du point avec design amélioré
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(p.getNom());
            
            // Ombre du texte
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(x - textWidth/2 - 8, y - 34, textWidth + 16, 22, 8, 8);
            
            // Fond coloré pour le texte
            GradientPaint textGradient = new GradientPaint(
                x, y - 34, new Color(70, 130, 180),
                x, y - 12, new Color(100, 150, 200)
            );
            g2d.setPaint(textGradient);
            g2d.fillRoundRect(x - textWidth/2 - 6, y - 32, textWidth + 12, 20, 6, 6);
            
            // Bordure du fond
            g2d.setColor(new Color(50, 100, 150));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x - textWidth/2 - 6, y - 32, textWidth + 12, 20, 6, 6);
            
            // Texte
            g2d.setColor(Color.WHITE);
            g2d.drawString(p.getNom(), x - textWidth/2, y - 16);
        }
    }

    // ===========================
    // Dessiner voitures (icône au lieu d'émoji)
    // ===========================
    private void dessinerVoitures(Graphics2D g2d) {
        if (voituresAffichees.isEmpty()) return;

        double minX = points.stream().mapToDouble(Point::getX).min().orElse(0);
        double minY = points.stream().mapToDouble(Point::getY).min().orElse(0);
        double maxX = points.stream().mapToDouble(Point::getX).max().orElse(1);
        double maxY = points.stream().mapToDouble(Point::getY).max().orElse(1);
        int panelWidth = getWidth() - 60;
        int panelHeight = getHeight() - 60;

        for (VoitureAffichee va : voituresAffichees) {
            Route r = va.getRouteCourante();
            if (r == null) continue;

            // Obtenir les points visuels selon le sens de parcours
            Point pDepart = va.getPointDepartVisuel();
            Point pArrivee = va.getPointArriveeVisuel();
            
            if (pDepart == null || pArrivee == null) continue;

            // Utiliser le facteur d'interpolation
            double t = va.getFacteurInterpolation();

            // Calculer les coordonnées avec les VRAIS points de départ et arrivée visuels
            int x1 = (int) ((pDepart.getX() - minX) / (maxX - minX) * panelWidth) + 30;
            int y1 = (int) ((pDepart.getY() - minY) / (maxY - minY) * panelHeight) + 30;
            int x2 = (int) ((pArrivee.getX() - minX) / (maxX - minX) * panelWidth) + 30;
            int y2 = (int) ((pArrivee.getY() - minY) / (maxY - minY) * panelHeight) + 30;

            int x = (int) (x1 + t * (x2 - x1));
            int y = (int) (y1 + t * (y2 - y1));

            // Vérifier si sur obstacle - utilise la nouvelle méthode
            boolean surObstacle = va.estSurObstacle(tousObstacles);

            // Dessiner une voiture stylée (icône vectorielle)
            dessinerIconeVoiture(g2d, x, y, surObstacle);

            // Nom de la voiture avec design moderne
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String nom = va.getVoiture().getNom();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(nom);
            
            // Ombre
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(x - textWidth/2 - 7, y + 18, textWidth + 14, 22, 8, 8);
            
            // Fond dégradé - change de couleur si sur obstacle
            GradientPaint gradient;
            if (surObstacle) {
                gradient = new GradientPaint(
                    x, y + 18, new Color(255, 100, 0),
                    x, y + 40, new Color(200, 50, 0)
                );
            } else {
                gradient = new GradientPaint(
                    x, y + 18, new Color(30, 144, 255),
                    x, y + 40, new Color(0, 100, 200)
                );
            }
            g2d.setPaint(gradient);
            g2d.fillRoundRect(x - textWidth/2 - 5, y + 20, textWidth + 10, 18, 6, 6);
            
            // Bordure
            g2d.setColor(surObstacle ? new Color(180, 40, 0) : new Color(0, 80, 180));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x - textWidth/2 - 5, y + 20, textWidth + 10, 18, 6, 6);
            
            // Texte
            g2d.setColor(Color.WHITE);
            g2d.drawString(nom, x - textWidth/2, y + 34);
        }
    }

    // ===========================
    // Dessiner icône de voiture vectorielle
    // ===========================
    private void dessinerIconeVoiture(Graphics2D g2d, int x, int y, boolean surObstacle) {
        // Ombre de la voiture
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(x - 13, y - 6, 26, 14, 8, 8);

        // Corps de la voiture - couleur change selon obstacle
        GradientPaint carGradient;
        if (surObstacle) {
            // Voiture orange/rouge sur obstacle
            carGradient = new GradientPaint(
                x, y - 10, new Color(255, 140, 0),
                x, y + 8, new Color(220, 100, 0)
            );
        } else {
            // Voiture bleue normale
            carGradient = new GradientPaint(
                x, y - 10, new Color(30, 144, 255),
                x, y + 8, new Color(0, 100, 200)
            );
        }
        g2d.setPaint(carGradient);
        
        // Carrosserie principale
        g2d.fillRoundRect(x - 12, y - 8, 24, 12, 6, 6);
        
        // Toit
        g2d.fillRoundRect(x - 8, y - 12, 16, 8, 4, 4);

        // Contour noir
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(x - 12, y - 8, 24, 12, 6, 6);
        g2d.drawRoundRect(x - 8, y - 12, 16, 8, 4, 4);

        // Fenêtres (bleu clair)
        g2d.setColor(new Color(173, 216, 230));
        g2d.fillRect(x - 7, y - 11, 6, 5);
        g2d.fillRect(x + 1, y - 11, 6, 5);

        // Roues noires
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x - 10, y + 2, 6, 6);
        g2d.fillOval(x + 4, y + 2, 6, 6);

        // Jantes grises
        g2d.setColor(new Color(180, 180, 180));
        g2d.fillOval(x - 9, y + 3, 4, 4);
        g2d.fillOval(x + 5, y + 3, 4, 4);

        // Phares avant (jaune ou rouge selon obstacle)
        if (surObstacle) {
            g2d.setColor(new Color(255, 0, 0)); // Phares rouges (freinage)
        } else {
            g2d.setColor(new Color(255, 255, 150)); // Phares jaunes normaux
        }
        g2d.fillOval(x + 10, y - 5, 3, 2);
        g2d.fillOval(x + 10, y + 1, 3, 2);
    }

    // ===========================
    // Démarrer animation RAPIDE avec ralentissement visuel sur obstacle
    // ===========================
// ===========================
// Nouvelle version de demarrerAnimation
// ===========================
// ===========================
// Démarrer animation avec obstacles et pauses
// ===========================
public void demarrerAnimation(
        List<Route> chemin,
        Voiture voiture,
        Point pointDepart,
        List<Obstacle> tousObstacles) {

    this.tousObstacles = tousObstacles;
    voituresAffichees.clear();

    VoitureAffichee va = new VoitureAffichee(voiture, chemin, pointDepart);
    voituresAffichees.add(va);

    Timer timer = new Timer(30, e -> {

        if (va.estTerminee()) {
            ((Timer) e.getSource()).stop();
            return;
        }

        double ratioObstacle = va.obtenirRatioVitesseActuelle(tousObstacles);
        double deltaHeure = (0.15 / 60.0) * ratioObstacle;

        va.avancer(deltaHeure, tousObstacles);
        repaint();
    });

    timer.start();
}




}