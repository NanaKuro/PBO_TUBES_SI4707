import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class PilihanController {

    @FXML
    private Button cari;

    @FXML
    private Button kembali;

    @FXML
    private ChoiceBox<String> pilihanFilm;

    @FXML
    public void initialize() {
        // Inisialisasi pilihan filter pencarian
        pilihanFilm.setItems(FXCollections.observableArrayList("Genre", "Tahun"));
        pilihanFilm.setValue("Genre"); // Pilihan default
    }

    @FXML
    void cariFilm(ActionEvent event) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Mendapatkan koneksi ke database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3308/db_tubes_film", "root", "");

            // Mendapatkan pilihan dari ChoiceBox
            String pilihan = pilihanFilm.getValue();
            if (pilihan == null || pilihan.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setHeaderText("Pilihan Kosong");
                alert.setContentText("Silakan pilih genre atau tahun terlebih dahulu.");
                alert.showAndWait();
                return;
            }

            // Menentukan query SQL berdasarkan input
            String sql;
            if (pilihan.matches("\\d+")) { // Jika pilihan berupa angka, maka cari berdasarkan tahun
                sql = "SELECT * FROM data_film WHERE tahun = ?";
            } else { // Jika bukan angka, maka cari berdasarkan genre
                sql = "SELECT * FROM data_film WHERE genre = ?";
            }

            // Mempersiapkan statement
            pst = conn.prepareStatement(sql);
            pst.setString(1, pilihan);
            rs = pst.executeQuery();

            // Membaca hasil pencarian
            StringBuilder hasil = new StringBuilder();
            while (rs.next()) {
                String judul = rs.getString("judul");
                String genre = rs.getString("genre");
                int tahun = rs.getInt("tahun");

                hasil.append("Judul: ").append(judul)
                    .append(", Genre: ").append(genre)
                    .append(", Tahun: ").append(tahun).append("\n");
            }

            // Menampilkan notifikasi berdasarkan hasil pencarian
            if (hasil.length() > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Hasil Pencarian");
                alert.setHeaderText("Film Ditemukan");
                alert.setContentText(hasil.toString());
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Hasil Pencarian");
                alert.setHeaderText("Tidak Ada Film");
                alert.setContentText("Tidak ada film dengan kriteria yang dipilih.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kesalahan");
            alert.setHeaderText("Gagal Mencari Film");
            alert.setContentText("Terjadi kesalahan saat mencari film. Pastikan koneksi ke database tersedia.");
            alert.showAndWait();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
            }
        }
    }

    @FXML
    void kembaliHome(ActionEvent event) throws IOException {
        Stage stage = (Stage) kembali.getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(javafx.fxml.FXMLLoader.load(getClass().getResource("MainGUI.fxml"))));
    }
}