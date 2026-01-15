package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Pause;

public class PauseDAO {
    
    /**
     * Récupère toutes les pauses
     */
    public static List<Pause> getAllPauses(Connection conn) throws SQLException {
        List<Pause> pauses = new ArrayList<>();
        String sql = "SELECT id, id_route, position_km, heure_debut, heure_fin FROM pause ORDER BY id_route, position_km";
        
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            Pause p = new Pause();
            p.setId(rs.getInt("id"));
            p.setIdRoute(rs.getInt("id_route"));
            p.setPositionKm(rs.getDouble("position_km"));
            p.setHeureDebut(rs.getString("heure_debut"));
            p.setHeureFin(rs.getString("heure_fin"));
            pauses.add(p);
        }
        rs.close();
        return pauses;
    }
    
    /**
     * Récupère les pauses d'une route spécifique
     */
    public static List<Pause> getPausesByRoute(Connection conn, int idRoute) throws SQLException {
        List<Pause> pauses = new ArrayList<>();
        String sql = "SELECT id, id_route, position_km, heure_debut, heure_fin FROM pause WHERE id_route = ? ORDER BY position_km";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idRoute);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            Pause p = new Pause();
            p.setId(rs.getInt("id"));
            p.setIdRoute(rs.getInt("id_route"));
            p.setPositionKm(rs.getDouble("position_km"));
            p.setHeureDebut(rs.getString("heure_debut"));
            p.setHeureFin(rs.getString("heure_fin"));
            pauses.add(p);
        }
        rs.close();
        ps.close();
        return pauses;
    }
    
    /**
     * Ajoute une nouvelle pause
     */
    public static void ajouterPause(Connection conn, Pause pause) throws SQLException {
        String sql = "INSERT INTO pause (id_route, position_km, heure_debut, heure_fin) VALUES (?, ?, ?, ?)";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, pause.getIdRoute());
        ps.setDouble(2, pause.getPositionKm());
        ps.setString(3, pause.getHeureDebut());
        ps.setString(4, pause.getHeureFin());
        ps.executeUpdate();
        ps.close();
    }
    
    /**
     * Supprime une pause
     */
    public static void supprimerPause(Connection conn, int idPause) throws SQLException {
        String sql = "DELETE FROM pause WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idPause);
        ps.executeUpdate();
        ps.close();
    }
    
    /**
     * Calcule la durée totale des pauses pour une liste de routes
     */
    public static double calculerDureeTotalePauses(Connection conn, List<Integer> idsRoutes) throws SQLException {
        if (idsRoutes.isEmpty()) return 0.0;
        
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < idsRoutes.size(); i++) {
            placeholders.append("?");
            if (i < idsRoutes.size() - 1) placeholders.append(",");
        }
        
        String sql = "SELECT heure_debut, heure_fin FROM pause WHERE id_route IN (" + placeholders + ")";
        PreparedStatement ps = conn.prepareStatement(sql);
        
        for (int i = 0; i < idsRoutes.size(); i++) {
            ps.setInt(i + 1, idsRoutes.get(i));
        }
        
        ResultSet rs = ps.executeQuery();
        double dureeTotale = 0.0;
        
        while (rs.next()) {
            Pause p = new Pause();
            p.setHeureDebut(rs.getString("heure_debut"));
            p.setHeureFin(rs.getString("heure_fin"));
            dureeTotale += p.getDureeHeures();
        }
        
        rs.close();
        ps.close();
        return dureeTotale;
    }
}