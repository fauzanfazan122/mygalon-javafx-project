package com.fauzan.mygalon.controller;

import com.fauzan.mygalon.DatabaseConn;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.swing.text.html.HTMLDocument;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class LaporanController {
    private String username;
    private int totalPendapatan;
    private int totalPenjualan;
    private int totalTransaksi;

    //ambil waktu
    String today;
    String currentMonth;
    String currentYear;
    String currentWeek;

    @FXML
    ComboBox<String> globalFilter;

    @FXML
    Label totalTransaksiLabel;

    @FXML
    Label totalPenjualanLabel;

    @FXML
    Label totalPendapatanLabel;

    @FXML
    LineChart<String, Number> transaksiChart;

    @FXML
    LineChart<String, Number> pemasukanChart;

    public void initialize(){
        today = LocalDate.now().toString();
        currentMonth = LocalDate.now().getYear() + "-" +String.format("%02d", LocalDate.now().getMonthValue());
        currentYear = String.valueOf(LocalDate.now().getYear());
        currentWeek = LocalDate.now().minusDays(6).toString();

        setTransaksiLabel(0);
        setPenjualanLabel(0);
        setPendapatanLabel(0);

        //filter
        globalFilter.getItems().addAll(
                "Hari ini",
                "Minggu ini",
                "Bulan ini",
                "Tahun ini"
        );

        //panggil
        setGlobalFilter();
        globalFilter.setValue("Hari ini");

        //chart
        setTransaksiChart();
        setPendapatanChart();


    }

    public void setUsername(String nama){
        this.username = nama;
    }

    private void setTransaksiLabel(int jumlah){
        totalTransaksiLabel.setText(" "+ jumlah);
    }

    private void setPenjualanLabel(int jumlah){
        totalPenjualanLabel.setText(" " + jumlah);
    }

    private void setPendapatanLabel(int jumlah){
        totalPendapatanLabel.setText("Rp " + jumlah);
    }

    private void setTransaksiChart(){
        XYChart.Series<String, Number> dataChart = new XYChart.Series<>();
        String sql =    "SELECT date(tanggalTransaksi) as tanggal, " +
                        "COUNT(*) as total " +
                        "FROM transaksi " +
                        "WHERE date(tanggalTransaksi) >= '" + currentWeek + "' " +
                        "GROUP BY date(tanggalTransaksi) " +
                        "ORDER BY tanggal";
        try(
                Connection connection =  DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery(sql);
        ){

            dataChart.setName("Transaksi");

            while(data.next()){

                String day = data.getString("tanggal");

                //convert jadi nama hari
                LocalDate localDate = LocalDate.parse(day);

                //ambil hari dalam bahasa indonesia
                String hari =
                        localDate.getDayOfWeek()
                                .getDisplayName(
                                        TextStyle.FULL,
                                        new Locale("id", "ID")
                                );

                dataChart.getData().add(new XYChart.Data<>(
                        hari, data.getInt("total")));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        transaksiChart.getData().clear();
        transaksiChart.getData().add(dataChart);
    }

    private void setPendapatanChart(){
        XYChart.Series<String, Number> dataChart = new XYChart.Series<>();

        String sql =    "SELECT date(tanggalTransaksi) as tanggal, " +
                        "SUM(totalTransaksi) as total " +
                        "FROM transaksi " +
                        "WHERE date(tanggalTransaksi) >= '" + currentWeek + "' " +
                        "GROUP BY date(tanggalTransaksi) " +
                        "ORDER BY tanggal";
        try(
                Connection connection =  DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery(sql);
        ){
            dataChart.setName("pendapatan");

            while(data.next()){

                String day = data.getString("tanggal");

                //convert jadi nama hari
                LocalDate localDate = LocalDate.parse(day);

                //ambil hari dalam bahasa indonesia
                String hari =
                        localDate.getDayOfWeek()
                                .getDisplayName(
                                        TextStyle.FULL,
                                        new Locale("id", "ID")
                                );

                dataChart.getData().add(new XYChart.Data<>(
                        hari, data.getInt("total")));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        pemasukanChart.getData().clear();
        pemasukanChart.getData().add(dataChart);
    }

    private void getTransaksi(String waktu, boolean minggu){
        String sql;

        if(minggu){
            sql = "SELECT COUNT(*) AS total FROM transaksi WHERE date(tanggalTransaksi) >= '" + waktu + "'";
        }
        else {
            sql = "SELECT COUNT(*) AS total FROM transaksi WHERE tanggalTransaksi LIKE '" + waktu + "%'";
        }

        try(
                Connection connection = DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery(sql);
                ){

            //reset
            totalTransaksi = 0;
            while(data.next()){
                totalTransaksi =  data.getInt("total");
            }
            setTransaksiLabel(totalTransaksi);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPenjualan(String waktu, boolean minggu){
        String sql;

        if(minggu){
            sql = "SELECT jumlahGalon FROM transaksi WHERE date(tanggalTransaksi) >= '" + waktu + "'";
        }
        else {
            sql = "SELECT jumlahGalon FROM transaksi WHERE tanggalTransaksi LIKE '" + waktu + "%'";
        }

        try(
                Connection connection = DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery(sql);
        ){

            //reset
            totalPenjualan = 0;
            while(data.next()){
                totalPenjualan +=  data.getInt("jumlahGalon");
            }
            setPenjualanLabel(totalPenjualan);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPendapatan(String waktu, boolean minggu){
        String sql;

        if(minggu){
            sql = "SELECT totalTransaksi FROM transaksi WHERE date(tanggalTransaksi) >= '" + waktu + "'";
        }
        else {
            sql = "SELECT totalTransaksi FROM transaksi WHERE tanggalTransaksi LIKE '" + waktu + "%'";
        }

        try(
                Connection connection = DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery(sql);
        ){

            //reset
            totalPendapatan = 0;
            while(data.next()){
                totalPendapatan +=  data.getInt("totalTransaksi");
            }
            setPendapatanLabel(totalPendapatan);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyFilter(){

        String filter = globalFilter.getValue();

        if(filter == null){
            return;
        }

        switch (filter){

            case "Hari ini":
                getPendapatan(today, false);
                getTransaksi(today, false);
                getPenjualan(today, false);
                break;

            case "Minggu ini":
                getPendapatan(currentWeek, true);
                getTransaksi(currentWeek, true);
                getPenjualan(currentWeek, true);
                break;

            case "Bulan ini":
                getPendapatan(currentMonth, false);
                getTransaksi(currentMonth, false);
                getPenjualan(currentMonth, false);
                break;

            case "Tahun ini":
                getPendapatan(currentYear, false);
                getTransaksi(currentYear, false);
                getPenjualan(currentYear, false);
                break;

        }
    }

    public void setGlobalFilter(){
        globalFilter.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                   applyFilter();
                });
    }

    public void bukaDashboard(){
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/fauzan/mygalon/dashboard.fxml"
                    )
            );

            Parent root = loader.load();

            DashboardController controller =
                    loader.getController();

            controller.setUsername(username);

            Stage stage = (Stage) globalFilter.getScene().getWindow();

            stage.getScene().setRoot(root);

        } catch (Exception e){
            e.printStackTrace();
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

            TransaksiController controller =
                    loader.getController();

            controller.setUsername(username);


            Stage stage = (Stage) globalFilter.getScene().getWindow();

            stage.getScene().setRoot(root);


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void bukaHarga(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fauzan/mygalon/harga.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) globalFilter.getScene().getWindow();

            HargaController controller =
                    loader.getController();

            controller.setUsername(username);

            stage.getScene().setRoot(root);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
