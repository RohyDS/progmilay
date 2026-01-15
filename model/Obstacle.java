package model;

public class Obstacle {
    int id;
    double tauxRetablissement;
    double kilometreDepart;
    double kilometreArrivee;
    int idRoute;

    public Obstacle() {
    }

    public Obstacle(int id, double tauxRetablissement, double kilometreDepart, double kilometreArrivee, int idRoute) {
        this.id = id;
        this.tauxRetablissement = tauxRetablissement;
        this.kilometreDepart = kilometreDepart;
        this.kilometreArrivee = kilometreArrivee;
        this.idRoute = idRoute;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public double getTauxRetablissement() {
        return tauxRetablissement;
    } 
    public void setTauxRetablissement(double tauxRetablissement) {
        this.tauxRetablissement = tauxRetablissement;
    }
    public double getKilometreDepart() {
        return kilometreDepart;
    }

    public void setKilometreDepart(double kilometreDepart) {
        this.kilometreDepart = kilometreDepart;
    }

    public double getKilometreArrivee() {
        return kilometreArrivee;
    }

    public void setKilometreArrivee(double kilometreArrivee) {
        this.kilometreArrivee = kilometreArrivee;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }

}
