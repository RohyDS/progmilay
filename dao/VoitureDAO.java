package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Route;
import model.Voiture;

public class VoitureDAO {
    
    public static List<Voiture> getAllVoitures(Connection conn) throws SQLException {
        List<Voiture> voitures = new ArrayList<>();
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT id, vitesse_max, largeur, longueur, nom, type, reservoir, consommation FROM voiture"
        );
        
        while(rs.next()){
            Voiture v = new Voiture();
            v.setId(rs.getInt("id"));
            v.setVitesseMax(rs.getDouble("vitesse_max"));
            v.setLargeur(rs.getDouble("largeur"));
            v.setLongueur(rs.getDouble("longueur"));
            v.setNom(rs.getString("nom"));
            v.setType(rs.getString("type"));
            v.setReservoir(rs.getDouble("reservoir"));
            v.setConsommation(rs.getDouble("consommation"));
            voitures.add(v);
        }
        rs.close();
        return voitures;
    }
    
    /**
     * Vérifie si la voiture est trop large pour les routes
     */
    public static String verifierLargeurVoiture(Voiture voiture, List<Route> routes) {
        for (Route r : routes) {
            if (voiture.getLargeur() > r.getLargeur() / 2.0) {
                return "La voiture \"" + voiture.getNom() + "\" est trop large pour la route " + r.getNom();
            }
        }
        return null;
    }
    
    /**
     * Vérifie si le carburant est suffisant pour parcourir toutes les routes
     * @param voiture La voiture à vérifier
     * @param routes Liste des routes du chemin
     * @return Message d'erreur si insuffisant, null si OK
     */
    public static String verifierCarburantSuffisant(Voiture voiture, List<Route> routes) {
        double distanceTotale = 0.0;
        
        // Calculer la distance totale du trajet
        for (Route r : routes) {
            distanceTotale += r.getDistance();
        }
        
        // Calculer la consommation nécessaire
        double consommationNecessaire = voiture.calculerConsommationPourDistance(distanceTotale);
        
        // Vérifier si le réservoir est suffisant
        if (consommationNecessaire > voiture.getReservoir()) {
            double autonomie = voiture.calculerAutonomie();
            return String.format(
                "⛽ Carburant insuffisant !\n" +
                "Distance totale : %.2f km\n" +
                "Consommation nécessaire : %.2f L\n" +
                "Capacité réservoir : %.2f L\n" +
                "Autonomie maximale : %.2f km\n" +
                "Il manque %.2f litres de carburant !",
                distanceTotale,
                consommationNecessaire,
                voiture.getReservoir(),
                autonomie,
                consommationNecessaire - voiture.getReservoir()
            );
        }
        
        // Afficher un message de succès avec les infos
        return null; // Tout est OK
    }
    
    /**
     * Obtient les informations de carburant pour un trajet
     */
    public static String getInfoCarburant(Voiture voiture, List<Route> routes) {
        double distanceTotale = 0.0;
        for (Route r : routes) {
            distanceTotale += r.getDistance();
        }
        
        double consommationNecessaire = voiture.calculerConsommationPourDistance(distanceTotale);
        double carburantRestant = voiture.getReservoir() - consommationNecessaire;
        double autonomieRestante = (carburantRestant * 100.0) / voiture.getConsommation();
        
        return String.format(
            "⛽ Informations Carburant :\n" +
            "Distance totale : %.2f km\n" +
            "Consommation : %.2f L/100km\n" +
            "Carburant nécessaire : %.2f L\n" +
            "Capacité réservoir : %.2f L\n" +
            "Carburant restant à l'arrivée : %.2f L\n" +
            "Autonomie restante : %.2f km",
            distanceTotale,
            voiture.getConsommation(),
            consommationNecessaire,
            voiture.getReservoir(),
            carburantRestant,
            autonomieRestante
        );
    }
}