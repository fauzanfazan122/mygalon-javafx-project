package com.fauzan.mygalon.controller;

import com.fauzan.mygalon.DatabaseConn;
import com.fauzan.mygalon.model.Transaksi;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {
    private int pendapatan;
    private int pengantaran;
    private int jumlahTransaksi;

    @FXML
    private Label pendapatanLabel;

    @FXML
    private Label jumlahTransaksiLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label pengantaranLabel;

    @FXML
    private TableView<Transaksi> tableTransaksi;

    @FXML
    private TableColumn<Transaksi, Integer> colNum;

    @FXML
    private TableColumn<Transaksi, String> colNama;

    @FXML
    private TableColumn<Transaksi, Integer> colTotal;

    @FXML
    private TableColumn<Transaksi, Integer> colJumlah;

    @FXML
    private TableColumn<Transaksi, String> colKurir;

    @FXML
    private TableColumn<Transaksi, String> colAlamat;

    @FXML
    private TableColumn<Transaksi, String> colStatus;

    @FXML
    private TableColumn<Transaksi, String> colTanggal;

    @FXML
    private TableColumn<Transaksi, String> colMetode;

    ObservableList<Transaksi> list =
            FXCollections.observableArrayList();

    public void  setUsernameLabel(String nama) {
        usernameLabel.setText(nama);
    }

//    ambil transaksi hari ini
    public void setJumlahTransaksi(){

//            ambil tanggal hari ini
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try (
                Connection connection = DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery(
                        "SELECT COUNT(*) AS total " +
                                "FROM transaksi " +
                                "WHERE tanggalTransaksi LIKE '" + today + "%'"
                );
                )
        {
            if(data.next()){
                jumlahTransaksi = data.getInt("total");
                jumlahTransaksiLabel.setText("" + jumlahTransaksi);
            }
            else {
                jumlahTransaksi = 0;
                jumlahTransaksiLabel.setText("" + jumlahTransaksi);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

//    ambil jumlah pengataran yang harus diproses
    public void setPengantaran(){

        try(
                Connection connection = DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery("SELECT COUNT(*) AS total FROM transaksi WHERE metodePengiriman = 'diantar' AND statusTransaksi = 'diproses'");
                )
        {
            if(data.next()){
                pengantaran = data.getInt("total");
                pengantaranLabel.setText("" + pengantaran);
            }
            else {
                pengantaran = 0;
                pengantaranLabel.setText("" + pengantaran);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void  initialize(){
//        panggil setter
        setPengantaran();
        setJumlahTransaksi();

//        biar pas ukuran colomnya
        tableTransaksi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

//        set nomor otomatis
        colNum.setCellValueFactory(cellData ->

                new ReadOnlyObjectWrapper<>(
                        tableTransaksi.getItems().indexOf(
                                cellData.getValue()
                        ) + 1
                )
        );

//        connect kolom dengan getter
        colNama.setCellValueFactory(
                new PropertyValueFactory<>("namaPembeli")
        );

        colTotal.setCellValueFactory(
                new PropertyValueFactory<>("totalTransaksi")
        );

        colJumlah.setCellValueFactory(
                new PropertyValueFactory<>("jumlahGalon")
        );

        colKurir.setCellValueFactory(
                new PropertyValueFactory<>("namaKurir")
        );

        colAlamat.setCellValueFactory(
                new PropertyValueFactory<>("alamatPengantaran")
        );

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("statusTransaksi")
        );

        colTanggal.setCellValueFactory(
                new PropertyValueFactory<>("tanggalTransaksi")
        );

        colMetode.setCellValueFactory(
                new PropertyValueFactory<>("metodePengiriman")
        );

        try (
                Connection connection = DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery("SELECT * FROM transaksi");
             ) {
            while(data.next()){
                Transaksi transaksi = new Transaksi(
                        data.getInt("idTransaksi"),
                        data.getString("namaPembeli"),
                        data.getInt("jumlahGalon"),
                        data.getString("namaKurir"),
                        data.getString("metodePengiriman"),
                        data.getString("alamatPengantaran"),
                        data.getString("tanggalTransaksi"),
                        data.getInt("totalTransaksi"),
                        data.getString("statusTransaksi")
                );
//                masukan ke list
                list.add(transaksi);
                pendapatan += transaksi.getTotalTransaksi();
            }

//            tampilkan tabel & hitung total pendapatan
            tableTransaksi.setItems(list);
            pendapatanLabel.setText("Rp " + pendapatan);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void bukaTransaksi(){

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/fauzan/mygalon/transaksi.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage) tableTransaksi.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);


        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
