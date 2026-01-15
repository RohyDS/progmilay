package model;

public class Route {
    private int id;
    private String nom;
    private Point depart;
    private Point arrivee;
    private double distance;
    private double largeur;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Point getDepart() { return depart; }
    public void setDepart(Point depart) { this.depart = depart; }

    public Point getArrivee() { return arrivee; }
    public void setArrivee(Point arrivee) { this.arrivee = arrivee; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public double getLargeur() { return largeur; }
    public void setLargeur(double largeur) { this.largeur = largeur; }

    @Override
    public String toString() {
        return nom + " (" + depart.getNom() + " → " + arrivee.getNom() + ")";
    }
}
