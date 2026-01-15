package model;

import java.util.ArrayList;
import java.util.List;

public class Point {
    private int id;
    private String nom;
    private double x;
    private double y;

    // Toutes les routes liées à ce point (départ OU arrivée)
    private List<Route> routes = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public List<Route> getRoutes() {
        return routes;
    }

    public void addRoute(Route r) {
        if (!routes.contains(r)) {
            routes.add(r);
        }
    }

    @Override
    public String toString() {
        return nom;
    }
}
