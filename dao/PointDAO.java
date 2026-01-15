package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Point;

public class PointDAO {

    public static List<Point> getAllPoints(Connection conn) throws SQLException {
        List<Point> points = new ArrayList<>();
        String sql = "SELECT id, nom, x, y FROM point";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Point p = new Point();
            p.setId(rs.getInt("id"));
            p.setNom(rs.getString("nom"));
            p.setX(rs.getDouble("x"));
            p.setY(rs.getDouble("y"));
            points.add(p);
        }

        rs.close();
        stmt.close();
        return points;
    }

    public static Point getPointById(Connection conn, int id) throws SQLException {
        Point p = null;
        String sql = "SELECT id, nom, x, y FROM point WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            p = new Point();
            p.setId(rs.getInt("id"));
            p.setNom(rs.getString("nom"));
            p.setX(rs.getDouble("x"));
            p.setY(rs.getDouble("y"));
        }

        rs.close();
        ps.close();
        return p;
    }
}
