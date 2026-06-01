package com.fauzan.mygalon.controller;

import com.fauzan.mygalon.DatabaseConn;
import com.fauzan.mygalon.model.Transaksi;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.lowagie.text.Document;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
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

    @FXML
    DatePicker startDate;

    @FXML
    Label labelStatus;

    @FXML
    DatePicker endDate;

    ArrayList<Transaksi> laporanData = new ArrayList<>();

    //format rupiah
    NumberFormat rupiah =
            NumberFormat.getCurrencyInstance(
                    new Locale("id", "ID")
            );

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
        totalPendapatanLabel.setText(rupiah.format(jumlah));
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

    public void setStatusLabel(boolean status, String statusText){
        //Tampilkan
        labelStatus.setManaged(true);
        labelStatus.setVisible(true);

        labelStatus.getStyleClass().removeAll("status-success","status-error");

        if(status){
            labelStatus.getStyleClass().addAll("status-success");
            labelStatus.setText("Succes! " + statusText);
        }
        else{
            labelStatus.getStyleClass().addAll("status-error");
            labelStatus.setText("Error! " + statusText);
        }
    }

    //membuat table header
    private PdfPCell createTHead(String text){
        PdfPCell cell =
                new PdfPCell(new Paragraph(text));

        cell.setBackgroundColor(
                new Color(37, 99, 235)
        );

        //atur alignment ke tengah
        cell.setHorizontalAlignment(
                PdfPCell.ALIGN_CENTER
        );

        cell.setPadding(8);

        return cell;
    }

    //membuat kolom table ringkasan
    private PdfPCell cell(String text){
        PdfPCell cell =
                new PdfPCell(
                        new Phrase(text)
                );

        //hilangkan border
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public void buatPdf(){
        Document document = new Document();

        //ringkasan
        int totalTransaksi = laporanData.size();
        int totalPenjualan = 0;
        int totalPemasukan = 0;
        for(Transaksi data : laporanData){
            totalPenjualan += data.getJumlahGalon();
            totalPemasukan += data.getTotalTransaksi();
        }

        //membuat table ringkasan
        PdfPTable Tringkasan = new PdfPTable(3);

        Tringkasan.setWidthPercentage(60);
        Tringkasan.setHorizontalAlignment(0);
        Tringkasan.setWidths(
                new float[]{
                        2.5f,
                        0.3f,
                        3.5f
                }
        );

        Tringkasan.addCell(cell("Total Transaksi"));
        Tringkasan.addCell(cell(":"));
        Tringkasan.addCell(cell("" + totalTransaksi));

        Tringkasan.addCell(cell("Total Galon Terjual"));
        Tringkasan.addCell(cell(":"));
        Tringkasan.addCell(cell("" + totalPenjualan));

        Tringkasan.addCell(cell("Total Pendapatan"));
        Tringkasan.addCell(cell(":"));
        Tringkasan.addCell(cell(rupiah.format(totalPemasukan)));

        Tringkasan.addCell(cell("Periode Laporan"));
        Tringkasan.addCell(cell(":"));
        Tringkasan.addCell(
                cell(startDate.getValue()
                        + " s/d "
                        + endDate.getValue())
        );

        //membuat table transaksi
        PdfPTable table = new PdfPTable(5);

        //setting ukuran table
        table.setWidths(
                new float[]{
                        1f, // No
                        3f, // Nama
                        1.7f, // Jumlah
                        3.8f, // Tanggal
                        3f // Total
                }
        );
        table.setWidthPercentage(100);

        //buat table head
        table.addCell(createTHead("No"));
        table.addCell(createTHead("Nama"));
        table.addCell(createTHead("Jumlah"));
        table.addCell(createTHead("Tanggal Transaksi"));
        table.addCell(createTHead("Total"));

        //mekanisme save as
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Simpan Laporan sebagai PDF");

        //buat default format PDF
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );

        //buat default name
        fileChooser.setInitialFileName("Laporan Transaksi.pdf");

        //ambil windows sekarang
        File file = fileChooser.showSaveDialog(globalFilter.getScene().getWindow());

        if(file == null){
            return;
        }

        try{
            //buat title pdf
            PdfPTable tableTitle = new PdfPTable(2);
            tableTitle.setWidthPercentage(100);
            tableTitle.setWidths(
                    new float[]{
                            9f,
                            1f
                    }
            );

            //title
            PdfPCell titleCell = new PdfPCell(Phrase.getInstance("Laporan Transaksi Depot Air Minum Isi Ulang Prabu"));
            titleCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            titleCell.setBorder(PdfPCell.NO_BORDER);

            //buat logo/watermark
            URL url = getClass().getResource("/com/fauzan/mygalon/assets/logo-horizontal.png");

            if(url != null){
                com.lowagie.text.Image logo =
                        com.lowagie.text.Image.getInstance(url.toExternalForm());
                logo.scaleToFit(50, 30);

                //logo
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(PdfPCell.NO_BORDER);
                logoCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

                tableTitle.addCell(titleCell);
                tableTitle.addCell(logoCell);
            }
            else {
                //logo
                PdfPCell emptyCell = new PdfPCell();
                emptyCell.setBorder(PdfPCell.NO_BORDER);

                tableTitle.addCell(titleCell);
                tableTitle.addCell(emptyCell);
            }

            //simpan file
            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(file)
            );

            document.open();

            //buat paragraft
            document.add(tableTitle);

            //buat garis
            document.add(new Paragraph(" "));
            document.add(new LineSeparator());
            document.add(new Paragraph(" "));

            document.add(Tringkasan);

            document.add(new Paragraph(" "));
            document.add(new LineSeparator());
            document.add(new Paragraph(" "));

            //no
            int i = 1;

            for(Transaksi data : laporanData){
                table.addCell(""+ i);
                table.addCell(data.getNamaPembeli());
                table.addCell("" + data.getJumlahGalon());
                table.addCell(data.getTanggalTransaksi());
                table.addCell(rupiah.format(data.getTotalTransaksi()));
                i++;
            }
            document.add(table);
            document.close();

            setStatusLabel(true, "Laporan berhasil dicetak!");


        }
        catch(Exception e){
            e.printStackTrace();
            setStatusLabel(false, "Gagal membuat PDF");
        }

    }

    public void cetakLaporan(){
        //konversi
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();

        if(start == null || end == null){
            setStatusLabel(false, "Kolom Periode wajib diisi");
            return;
        }

        if(start.isAfter(end)){
            setStatusLabel(false, "Priode awal tidak boleh lebih besar dari priode akhir");
            return;
        }

        //sql
        String sql = """
            SELECT * FROM transaksi
            WHERE date(tanggalTransaksi)
            BETWEEN '%s' AND '%s'
            ORDER BY tanggalTransaksi DESC
            """.formatted(start, end);

        try(
                Connection connection = DatabaseConn.connect();
                Statement stmt = connection.createStatement();
                ResultSet data = stmt.executeQuery(sql);
                ){

            laporanData.clear();

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

                laporanData.add(transaksi);
            }
            buatPdf();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reset(){
        startDate.setValue(null);
        endDate.setValue(null);
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
