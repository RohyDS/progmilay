package persistance;
import java.sql.*;
public class PostgresBD {
    public static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/voyage";
        String user = "postgres";
        String password = "admin";
        return DriverManager.getConnection(url, user, password);
    }
    public static void close(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connexion PostgreSQL fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur fermeture connexion PostgreSQL : " + e.getMessage());
        }
    }
    public static void main(String[] args) throws Exception {
        Connection c = PostgresBD.getConnection();
        System.out.println("Connexion PostgreSQL OK");
        PostgresBD.close(c);
        System.out.println("Test de connexion PostgreSQL terminé.");
        c.close();  }
}
