package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Obstacle;

public class ObstacleDAO {
     public static List<Obstacle> getAllObstacles(Connection conn) throws SQLException {
        List<Obstacle> obstacles = new ArrayList<>();
        String sql = "SELECT id, taux_ralentissement, km_depart, km_arrivee, idroute FROM obstacle";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Obstacle o = new Obstacle();
            o.setId(rs.getInt("id"));
            o.setTauxRetablissement(rs.getDouble("taux_ralentissement"));
            o.setKilometreDepart(rs.getDouble("km_depart"));
            o.setKilometreArrivee(rs.getDouble("km_arrivee"));
            o.setIdRoute(rs.getInt("idroute"));
            obstacles.add(o);
        }
        rs.close();
        stmt.close();
        return obstacles;
    }
    public static List<Obstacle> getObstaclesByRoute(Connection conn, int idRoute) throws SQLException {
    List<Obstacle> list = new ArrayList<>();
    String sql = "SELECT * FROM obstacle WHERE idroute = ?";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setInt(1, idRoute);
    ResultSet rs = ps.executeQuery();

    while (rs.next()) {
        Obstacle o = new Obstacle();
        o.setId(rs.getInt("id"));
        o.setTauxRetablissement(rs.getDouble("taux_ralentissement"));
        o.setKilometreDepart(rs.getDouble("km_depart"));
        o.setKilometreArrivee(rs.getDouble("km_arrivee"));
        o.setIdRoute(idRoute);
        list.add(o);
    }
    return list;
}

    public static void ajouterObstacle(Connection conn, Obstacle o) throws SQLException {
    String sql = "INSERT INTO obstacle (id, taux_ralentissement, km_depart, km_arrivee, idroute) " +
                 "VALUES (seq_obstacle.NEXTVAL, ?, ?, ?, ?)";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setDouble(1, o.getTauxRetablissement());
    ps.setDouble(2, o.getKilometreDepart());
    ps.setDouble(3, o.getKilometreArrivee());
    ps.setInt(4, o.getIdRoute());
    ps.executeUpdate();
    ps.close();
}


}
