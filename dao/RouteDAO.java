package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import model.Point;
import model.Route;

public class RouteDAO {

    public static List<Route> getAllRoutes(Connection conn, List<Point> points) throws SQLException {
        List<Route> routes = new ArrayList<>();
        Map<Integer, Point> mapPoint = new HashMap<>();

        for (Point p : points) {
            mapPoint.put(p.getId(), p);
        }

        String sql = "SELECT * FROM route";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Route r = new Route();
            r.setId(rs.getInt("id"));
            r.setNom(rs.getString("nom"));
            r.setDistance(rs.getDouble("distance"));
            r.setLargeur(rs.getDouble("largeur"));

            Point depart  = mapPoint.get(rs.getInt("idpointdepart"));
            Point arrivee = mapPoint.get(rs.getInt("idpointdesti"));

            r.setDepart(depart);
            r.setArrivee(arrivee);

            // 🔁 IMPORTANT : route bidirectionnelle
            depart.addRoute(r);
            arrivee.addRoute(r);

            routes.add(r);
        }

        rs.close();
        ps.close();

        return routes;
    }
}
