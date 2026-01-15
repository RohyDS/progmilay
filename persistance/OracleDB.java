package persistance;
import java.sql.*;
public class OracleDB {
      public static Connection getConnection() throws Exception{
        Class.forName("oracle.jdbc.OracleDriver");
        String url = "jdbc:oracle:thin:@localhost:1521:ORCL";
        String user = "voyage";
        String password = "voyage123";
        return DriverManager.getConnection(url,user,password);
    }

    public static void close(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connexion Oracle fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur fermeture connexion Oracle : " + e.getMessage());
        }
    }
    public static void main(String[] args) throws Exception {
    Connection c = OracleDB.getConnection();
    System.out.println("Connexion Oracle OK");
    OracleDB.close(c);
    System.out.println("Test de connexion Oracle terminé.");
    c.close();
}
}