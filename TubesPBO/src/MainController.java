import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private Button cariFilm;

    @FXML
    private Button editFilm;

    @FXML
    private Button hapusFilm;

    @FXML
    private TextField inputGenre;

    @FXML
    private TextField inputJudul;

    @FXML
    private TextField inputTahun;

    @FXML
    private TableView<Film> tabelFilm;

    @FXML
    private Button tambahFilm;

    @FXML
    private TableColumn<Film, String> tblGenre;

    @FXML
    private TableColumn<Film, String> tblJudul;

    @FXML
    private TableColumn<Film, Integer> tblTahun;

    ObservableList<Film> filmArray = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up kolom tabel
        tblJudul.setCellValueFactory(new PropertyValueFactory<>("judulFilm"));
        tblGenre.setCellValueFactory(new PropertyValueFactory<>("genreFilm"));
        tblTahun.setCellValueFactory(new PropertyValueFactory<>("tahunRilis"));

        // Muat data dari database ke tabel
        loadFilmData();
    }

    @FXML
    void tambahFilm(ActionEvent event) {
        PreparedStatement pst;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3308/db_tubes_film", "root", "");
            String judul = inputJudul.getText();
            String genre = inputGenre.getText();
            int tahun = Integer.valueOf(inputTahun.getText());

            pst = conn.prepareStatement("INSERT INTO data_film (judul, genre, tahun) VALUES (?, ?, ?)");
            pst.setString(1, judul);
            pst.setString(2, genre);
            pst.setInt(3, tahun);
            pst.executeUpdate();

            // Tambahkan film baru ke filmArray
            Film film = new Film(judul, genre, tahun);
            filmArray.add(film);
            tabelFilm.setItems(filmArray); // Tampilkan filmArray yang sudah diperbarui

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Film Berhasil Ditambahkan");
            alert.setContentText("Film " + judul + " berhasil ditambahkan ke dalam list rekomendasi");
            alert.showAndWait();
            clear();
        } catch (SQLException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Film Gagal Ditambahkan");
            alert.setContentText("Masukkan data Film dengan benar");
            alert.showAndWait();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void editFilm(ActionEvent event) {
        PreparedStatement pst = null;
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3308/db_tubes_film", "root", "");

            // Ambil film yang dipilih dari tabel
            Film selectedFilm = tabelFilm.getSelectionModel().getSelectedItem();
            if (selectedFilm != null) {
                // Ambil data dari input
                String judul = inputJudul.getText();
                String genre = inputGenre.getText();
                String tahunInput = inputTahun.getText();

                // Validasi input tahun
                if (tahunInput.isEmpty()) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Peringatan");
                    alert.setContentText("Tahun tidak boleh kosong.");
                    alert.showAndWait();
                    return; // Keluar dari metode jika tahun kosong
                }

                int tahun;
                try {
                    tahun = Integer.parseInt(tahunInput); // Coba konversi ke integer
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Kesalahan");
                    alert.setContentText("Tahun harus berupa angka.");
                    alert.showAndWait();
                    return; // Keluar dari metode jika konversi gagal
                }

                // Update data di database
                pst = conn.prepareStatement("UPDATE data_film SET judul = ?, genre = ?, tahun = ? WHERE id = ?");
                pst.setString(1, judul);
                pst.setString(2, genre);
                pst.setInt(3, tahun);
                pst.setInt(4, selectedFilm.getId()); // Gunakan ID film yang dipilih

                int k = pst.executeUpdate();

                if (k == 1) {
                    // Update film di filmArray
                    selectedFilm.setJudulFilm(judul);
                    selectedFilm.setGenreFilm(genre);
                    selectedFilm.setTahunRilis(tahun);
                    tabelFilm.refresh(); // Refresh tabel untuk menampilkan data yang diperbarui

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Film Berhasil Diedit");
                    alert.setContentText("Film " + judul + " berhasil diedit");
                    alert.showAndWait();
                    clear(); // Bersihkan input setelah edit
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Kesalahan dalam mengedit data Film");
                    alert.setContentText("Silahkan cek data yang anda masukan");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setContentText("Silahkan pilih film yang ingin diedit");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Kesalahan");
            alert.setContentText("Terjadi kesalahan saat mengedit film");
            alert.showAndWait();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void hapusFilm(ActionEvent event) {
        PreparedStatement pst = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3308/db_tubes_film", "root", "");

            // Ambil film yang dipilih dari tabel
            Film selectedFilm = tabelFilm.getSelectionModel().getSelectedItem();
            if (selectedFilm != null) {
                // Hapus data dari database
                pst = conn.prepareStatement("DELETE FROM data_film WHERE id = ?");
                pst.setInt(1, selectedFilm.getId()); // Gunakan ID film yang dipilih

                int k = pst.executeUpdate();
                if (k == 1) {
                    filmArray.remove(selectedFilm); // Hapus dari ObservableList
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Film berhasil dihapus");
                    alert.setContentText("Film yang berjudul " + selectedFilm.getJudulFilm() + " berhasil dihapus");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Kesalahan");
                    alert.setContentText("Terjadi kesalahan saat menghapus film");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setContentText("Silahkan pilih film yang ingin dihapus");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Kesalahan");
            alert.setContentText("Terjadi kesalahan saat menghapus film");
            alert.showAndWait();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void cariFilm(ActionEvent event) throws IOException {
        Stage stage = (Stage) cariFilm.getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(javafx.fxml.FXMLLoader.load(getClass().getResource("PilihanGUI.fxml"))));
    }

    @FXML
    void select(MouseEvent event) {
        try {
            if (event.getClickCount() == 1) {
                Film selectedFilm = tabelFilm.getSelectionModel().getSelectedItem();
                if (selectedFilm != null) {
                    inputJudul.setText(selectedFilm.getJudulFilm());
                    inputGenre.setText(selectedFilm.getGenreFilm());
                    inputTahun.setText(String.valueOf(selectedFilm.getTahunRilis()));
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("No data");
            alert.setContentText("Please select a valid row");
            alert.showAndWait();
        }
    }

    private void loadFilmData() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3308/db_tubes_film", "root", "");
            String sql = "SELECT * FROM data_film";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            // Bersihkan daftar sebelumnya
            filmArray.clear(); // Kosongkan filmArray sebelum memuat data baru

            // Ambil data dari ResultSet dan tambahkan ke ObservableList
            while (rs.next()) {
                int id = rs.getInt("id"); // Ambil ID dari database
                String judul = rs.getString("judul");
                String genre = rs.getString("genre");
                int tahun = rs.getInt("tahun");

                Film film = new Film(judul, genre, tahun); // Pastikan Film memiliki constructor yang sesuai
                filmArray.add(film);
            }

            tabelFilm.setItems(filmArray); // Set items setelah mengisi filmArray

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    void clear() {
        inputJudul.clear();
        inputGenre.clear();
        inputTahun.clear();
    }
} 
