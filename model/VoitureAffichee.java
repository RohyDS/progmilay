package model;

import java.util.List;
import java.util.ArrayList;

public class VoitureAffichee {
    private Voiture voiture;
    private List<Route> chemin;
    private List<Boolean> sensInverse; // true si route parcourue en sens inverse
    private int indexRoute;
    private double positionKm;
    private double vitesse;
    private Point pointPrecedent; // Pour tracker le sens de déplacement

    public VoitureAffichee(Voiture v, List<Route> chemin, Point pointDepart) {
        this.voiture = v;
        this.chemin = chemin;
        this.indexRoute = 0;
        this.positionKm = 0;
        this.vitesse = v.getVitesseMoyenne();
        this.pointPrecedent = pointDepart;
        
        // Détecter le sens de parcours de chaque route
        this.sensInverse = new ArrayList<>();
        detecterSensParcours(pointDepart);
    }
    
    /**
     * Détecte si chaque route est parcourue dans le sens inverse
     * En se basant sur le point de départ du trajet
     */
    private void detecterSensParcours(Point pointDepart) {
        Point pointCourant = pointDepart;
        
        for (Route route : chemin) {
            // Si le point courant correspond au départ de la route = sens normal
            // Si le point courant correspond à l'arrivée de la route = sens inverse
            boolean inverse = route.getArrivee().equals(pointCourant);
            sensInverse.add(inverse);
            
            // Mettre à jour le point courant pour la prochaine route
            if (inverse) {
                pointCourant = route.getDepart();
            } else {
                pointCourant = route.getArrivee();
            }
        }
    }

    public Voiture getVoiture() {
        return voiture;
    }

    public boolean estTerminee() {
        return indexRoute >= chemin.size();
    }

    public Route getRouteCourante() {
        if (estTerminee()) return null;
        return chemin.get(indexRoute);
    }
    
    /**
     * Retourne true si la route courante est parcourue en sens inverse
     */
    public boolean estSensInverse() {
        if (estTerminee()) return false;
        return sensInverse.get(indexRoute);
    }

    public double getPositionKm() {
        return positionKm;
    }
    
    /**
     * Obtient le point de départ visuel de la route courante
     * (selon le sens de parcours)
     */
    public Point getPointDepartVisuel() {
        if (estTerminee()) return null;
        Route r = getRouteCourante();
        return estSensInverse() ? r.getArrivee() : r.getDepart();
    }
    
    /**
     * Obtient le point d'arrivée visuel de la route courante
     * (selon le sens de parcours)
     */
    public Point getPointArriveeVisuel() {
        if (estTerminee()) return null;
        Route r = getRouteCourante();
        return estSensInverse() ? r.getDepart() : r.getArrivee();
    }
    
    /**
     * Obtient le facteur t pour l'interpolation (0.0 à 1.0)
     * Prend en compte le sens de parcours
     */
    public double getFacteurInterpolation() {
        if (estTerminee()) return 0.0;
        
        Route r = getRouteCourante();
        double t = positionKm / r.getDistance();
        t = Math.min(Math.max(t, 0.0), 1.0);
        
        // Le facteur t reste toujours de 0 à 1
        // Mais on utilisera les points visuels corrects pour l'interpolation
        return t;
    }

    /**
     * Obtenir le ratio de vitesse actuelle (1.0 = normale, <1.0 = ralentie)
     * Utilisé pour ajuster la vitesse de l'animation visuellement
     * PREND EN COMPTE LE SENS DE PARCOURS
     */
    public double obtenirRatioVitesseActuelle(List<Obstacle> obstacles) {
        if (estTerminee()) return 1.0;
        
        Route r = getRouteCourante();
        double ratioVitesse = 1.0;
        
        for (Obstacle o : obstacles) {
            if (o.getIdRoute() != r.getId()) continue;
            
            // Calculer la position réelle selon le sens
            double positionReelle = positionKm;
            if (estSensInverse()) {
                // Si sens inverse, inverser la position
                positionReelle = r.getDistance() - positionKm;
            }
            
            // Vérifier si on est dans l'obstacle
            if (positionReelle >= o.getKilometreDepart() && positionReelle <= o.getKilometreArrivee()) {
                ratioVitesse = (100.0 - o.getTauxRetablissement()) / 100.0;
                break;
            }
        }
        
        return ratioVitesse;
    }
    
    /**
     * Vérifie si la voiture est actuellement sur un obstacle
     * PREND EN COMPTE LE SENS DE PARCOURS
     */
    public boolean estSurObstacle(List<Obstacle> obstacles) {
        if (estTerminee()) return false;
        
        Route r = getRouteCourante();
        
        for (Obstacle o : obstacles) {
            if (o.getIdRoute() != r.getId()) continue;
            
            // Calculer la position réelle selon le sens
            double positionReelle = positionKm;
            if (estSensInverse()) {
                // Si sens inverse, inverser la position
                positionReelle = r.getDistance() - positionKm;
            }
            
            // Vérifier si on est dans l'obstacle
            if (positionReelle >= o.getKilometreDepart() && positionReelle <= o.getKilometreArrivee()) {
                return true;
            }
        }
        
        return false;
    }

    public void avancer(double deltaHeure, List<Obstacle> obstacles) {
        if (estTerminee()) return;

        Route r = getRouteCourante();
        
        // La vitesse visuelle est déjà ajustée par le deltaHeure dans l'animation
        // Ici on avance simplement à vitesse constante
        double distanceAvancee = vitesse * deltaHeure;
        positionKm += distanceAvancee;

        if (positionKm >= r.getDistance()) {
            positionKm = 0;
            indexRoute++;
        }
    }
}