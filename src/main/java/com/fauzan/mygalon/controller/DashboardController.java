package com.fauzan.mygalon.controller;

import com.fauzan.mygalon.DatabaseConn;
import com.fauzan.mygalon.model.Transaksi;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DashboardController {
    private int pendapatan;
    private int pengantaran;
    private int jumlahTransaksi;
    private String username;

    //tanggal hari ini
    String today;

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
    private TableColumn<Transaksi, String> colStatus;

    @FXML
    private TableColumn<Transaksi, String> colTanggal;

    @FXML
    private ComboBox<String> chartFilter;

    ObservableList<Transaksi> list =
            FXCollections.observableArrayList();

    private final XYChart.Series<String, Number> series =
            new XYChart.Series<>();

    @FXML
    private LineChart<String, Number> lineChart;

    //format rupiah
    NumberFormat rupiah =
            NumberFormat.getCurrencyInstance(
                    new Locale("id", "ID")
            );

    private void loadTable(){
        //set ukuran tabel
        tableTransaksi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        //set nomor otomatis
        colNum.setCellValueFactory(cellData ->

                new ReadOnlyObjectWrapper<>(
                        tableTransaksi.getItems().indexOf(
                                cellData.getValue()
                        ) + 1
                )
        );

        //connect kolom dengan getter
        colNama.setCellValueFactory(
                new PropertyValueFactory<>("namaPembeli")
        );

        colTotal.setCellValueFactory(
                new PropertyValueFactory<>("totalTransaksi")
        );

        colJumlah.setCellValueFactory(
                new PropertyValueFactory<>("jumlahGalon")
        );

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("statusTransaksi")
        );

        colTanggal.setCellValueFactory(
                new PropertyValueFactory<>("tanggalTransaksi")
        );

        try (
                Connection connection = DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery("SELECT * FROM transaksi ORDER BY tanggalTransaksi DESC LIMIT 15");
        ) {
            while(data.next()){
                Transaksi transaksi = new Transaksi(
                        data.getInt("idTransaksi"),
                        data.getString("namaPembeli"),
                        data.getInt("jumlahGalon"),
                        data.getInt("hargaGalon"),
                        data.getString("namaKurir"),
                        data.getString("metodePengiriman"),
                        data.getInt("hargaKirim"),
                        data.getString("alamatPengantaran"),
                        data.getString("tanggalTransaksi"),
                        data.getInt("totalTransaksi"),
                        data.getString("statusTransaksi")
                );

//                masukan ke list
                list.add(transaksi);
            }

//            tampilkan tabel & hitung total pendapatan
            tableTransaksi.setItems(list);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //set username
    public void setUsername(String nama) {
        username = nama;
        usernameLabel.setText(username);
    }

    //ambil transaksi hari ini
    public void setJumlahTransaksi(){
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

    public void setPendapatan(){
        String sql = "SELECT totalTransaksi FROM transaksi WHERE tanggalTransaksi LIKE '" + today + "%'";

        try(
                Connection connection = DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery(sql);
                ){
            while(data.next()){
                pendapatan += data.getInt("totalTransaksi");
            }
            setPendapatanLabel(pendapatan);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //ambil jumlah pengataran yang harus diproses
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

    public void setPendapatanLabel(int pendapatan) {
        pendapatanLabel.setText(rupiah.format(pendapatan));
    }

    public void loadChartTransaksi(){
        series.getData().clear();

        String currentWeek = LocalDate.now().minusDays(6).toString();

        String sql = """
            SELECT
            date(tanggalTransaksi) as tanggal,
            COUNT(*) as total
            FROM transaksi
            WHERE date(tanggalTransaksi) >= '%s'
            GROUP BY date(tanggalTransaksi)
            ORDER BY tanggal
        """.formatted(currentWeek);

        try(
                Connection connection =  DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery(sql);
                ){

            series.setName("Transaksi");


            while(data.next()){

                String day = data.getString("tanggal");
                int harga = data.getInt("total");

                //convert jadi nama hari
                LocalDate localDate = LocalDate.parse(day);

                //ambil hari dalam bahasa indonesia
                String hari =
                        localDate.getDayOfWeek()
                                .getDisplayName(
                                        TextStyle.FULL,
                                        new Locale("id", "ID")
                                );

                series.getData().add(new XYChart.Data<>(
                        hari, data.getInt("total")));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        lineChart.setTitle("Transaksi Mingguan");
        lineChart.applyCss();
        lineChart.layout();
    }

    public void loadChartPemasukan(){
        series.getData().clear();

        String currentWeek = LocalDate.now().minusDays(6).toString();

        String sql = """
            SELECT
            date(tanggalTransaksi) as tanggal,
            SUM(totalTransaksi) as total
            FROM transaksi
            WHERE date(tanggalTransaksi) >= '%s'
            GROUP BY date(tanggalTransaksi)
            ORDER BY tanggal
        """.formatted(currentWeek);

        try(
                Connection connection =  DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery(sql);
        ){

            series.setName("Transaksi");


            while(data.next()){

                String day = data.getString("tanggal");
                int harga = data.getInt("total");

                //convert jadi nama hari
                LocalDate localDate = LocalDate.parse(day);

                //ambil hari dalam bahasa indonesia
                String hari =
                        localDate.getDayOfWeek()
                                .getDisplayName(
                                        TextStyle.FULL,
                                        new Locale("id", "ID")
                                );

                series.getData().add(new XYChart.Data<>(
                        hari, data.getInt("total")));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        lineChart.setTitle("Pemasukan Mingguan");

        lineChart.applyCss();
        lineChart.layout();
    }

    public void applyFilter(){

        String filter = chartFilter.getValue();

        if(filter == null){
            return;
        }

        switch (filter){

            case "Transaksi Mingguan":
                loadChartTransaksi();
                break;

            case "Pemasukan Mingguan":
                loadChartPemasukan();
                break;


        }
    }

    public void setFilter(){
        chartFilter.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    applyFilter();
                });
    }

    public void  initialize(){


        //set tanggal
        today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //panggil tabel
        loadTable();

        //panggil setter
        setPendapatan();
        setPengantaran();
        setJumlahTransaksi();

        //panggil chart
        lineChart.setAnimated(false);
        lineChart.getXAxis().setAnimated(false);
        lineChart.getYAxis().setAnimated(false);
        lineChart.getData().add(series);
        setFilter();

        //filter set
        chartFilter.getItems().addAll(
                "Transaksi Mingguan",
                "Pemasukan Mingguan"
        );
        chartFilter.setValue("Transaksi Mingguan");



//
    }

    public void bukaTransaksi(){

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/fauzan/mygalon/transaksi.fxml"
                    )
            );

            Parent root = loader.load();

            TransaksiController controller =
                    loader.getController();

            controller.setUsername(username);


            Stage stage = (Stage) tableTransaksi.getScene().getWindow();

            stage.getScene().setRoot(root);


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void bukaHarga(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fauzan/mygalon/harga.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) tableTransaksi.getScene().getWindow();

            HargaController controller =
                    loader.getController();

            controller.setUsername(username);

            stage.getScene().setRoot(root);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void bukaLaporan(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fauzan/mygalon/laporan.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) tableTransaksi.getScene().getWindow();

            LaporanController controller =
                    loader.getController();

            controller.setUsername(username);

            stage.getScene().setRoot(root);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void logout(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fauzan/mygalon/login.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) tableTransaksi.getScene().getWindow();

            setUsername("");

            stage.getScene().setRoot(root);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
