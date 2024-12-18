import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DatabaseFilm {
    PreparedStatement pst;
    Connection conn;
    public static void main(String[] args) {
        final String URL = "jdbc:mysql://localhost:3308/db_tubes_film";
        final String USER = "root";
        final String PASSWORD = "";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Koneksi berhasil!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}