package model;

import java.util.List;

public class Voiture {

    private int id;
    private double vitesseMax;
    private double vitesseMoyenne;
    private double largeur;
    private double longueur;
    private String nom;
    private String type;
    private double reservoir;
    private double consommation;

    public Voiture() {}

    public Voiture(int id, double vitesseMax, double vitesseMoyenne,
                   double largeur, double longueur,
                   String nom, String type,
                   double reservoir, double consommation) {

        this.id = id;
        this.vitesseMax = vitesseMax;
        this.vitesseMoyenne = vitesseMoyenne;
        this.largeur = largeur;
        this.longueur = longueur;
        this.nom = nom;
        this.type = type;
        this.reservoir = reservoir;
        this.consommation = consommation;
    }

    /* =======================
       GETTERS / SETTERS
       ======================= */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getVitesseMax() { return vitesseMax; }
    public void setVitesseMax(double vitesseMax) { this.vitesseMax = vitesseMax; }

    public double getVitesseMoyenne() { return vitesseMoyenne; }
    public void setVitesseMoyenne(double vitesseMoyenne) { this.vitesseMoyenne = vitesseMoyenne; }

    public double getLargeur() { return largeur; }
    public void setLargeur(double largeur) { this.largeur = largeur; }

    public double getLongueur() { return longueur; }
    public void setLongueur(double longueur) { this.longueur = longueur; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getReservoir() { return reservoir; }
    public void setReservoir(double reservoir) { this.reservoir = reservoir; }

    public double getConsommation() { return consommation; }
    public void setConsommation(double consommation) { this.consommation = consommation; }

    /* CARBURANT */

    public double calculerConsommationPourDistance(double distanceKm) {
        return (consommation * distanceKm) / 100.0;
    }

    public double calculerAutonomie() {
        if (consommation == 0) return Double.MAX_VALUE;
        return (reservoir * 100.0) / consommation;
    }

    public boolean reservoirSuffisant(double distanceKm) {
        return calculerConsommationPourDistance(distanceKm) <= reservoir;
    }

    /* VITESSE MOYENNE RÉELLE
       (FORMULE PONDÉRÉE) */

    public double calculerVitesseMoyenneReelle(Route route, List<Obstacle> obstacles) {

        double distanceTotale = route.getDistance();

        List<Obstacle> obstaclesRoute = obstacles.stream()
                .filter(o -> o.getIdRoute() == route.getId())
                .sorted((o1, o2) ->
                        Double.compare(o1.getKilometreDepart(), o2.getKilometreDepart()))
                .toList();

        if (obstaclesRoute.isEmpty()) {
            return vitesseMoyenne;
        }

        double sommeVitesseDistance = 0.0;
        double kmCourant = 0.0;
        //decomposition an'ilay ny obstacle tsirairay
        for (Obstacle obs : obstaclesRoute) {

            // Segment tsy misy obstacle obstacle
            if (obs.getKilometreDepart() > kmCourant) {
                double dLibre = obs.getKilometreDepart() - kmCourant;
                sommeVitesseDistance += vitesseMoyenne * dLibre;
                kmCourant = obs.getKilometreDepart();
            }

            // Segment @ obstacle
            double debut = Math.max(kmCourant, obs.getKilometreDepart());
            double fin = Math.min(obs.getKilometreArrivee(), distanceTotale);

            if (fin > debut) {
                double dObstacle = fin - debut;
                double vitesseReduite =
                        vitesseMoyenne * (100.0 - obs.getTauxRetablissement()) / 100.0;

                sommeVitesseDistance += vitesseReduite * dObstacle;
                kmCourant = fin;
            }
        }

        // Segment final sans obstacle
        if (kmCourant < distanceTotale) {
            double dRestant = distanceTotale - kmCourant;
            sommeVitesseDistance += vitesseMoyenne * dRestant;
        }

        return sommeVitesseDistance / distanceTotale;
    }

    /* CHEMIN COMPLET */

    public double calculerVitesseMoyenneReelleChemin(List<Route> chemin,List<Obstacle> obstacles) {

        double distanceTotale = 0.0;
        double sommeVitesseDistance = 0.0;

        for (Route route : chemin) {
            double d = route.getDistance();
            double v = calculerVitesseMoyenneReelle(route, obstacles);

            distanceTotale += d;
            sommeVitesseDistance += v * d;
        }

        return sommeVitesseDistance / distanceTotale;
    }

    @Override
    public String toString() {
        return nom + " (" + type + ")";
    }
}
