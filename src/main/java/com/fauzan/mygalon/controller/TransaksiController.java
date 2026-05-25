package com.fauzan.mygalon.controller;

import com.fauzan.mygalon.DatabaseConn;
import com.fauzan.mygalon.model.Transaksi;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;

public class TransaksiController {
//    sementara
    private final int hargaGalon = 5000;
    private final int hargaAntar = 1000;

    private int totalTransaksi = 0;

//    selected row tabel
    private int selectedId = -1;

//    update mode
    private boolean modeEdit = false;

    @FXML
    private TextField inputNama;

    @FXML
    private TextField inputJumlahGalon ;

    @FXML
    private TextField inputKurir ;

    @FXML
    private ComboBox<String> inputMetode;

    @FXML
    private TextArea inputAlamat;

    @FXML
    private ComboBox<String> inputStatus;

    @FXML
    private Label labelStatus;

    @FXML
    private Label totalLabel;

    // tabel
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

//    tombol update
    @FXML
    private Button kirimButton;

//    tombol hapus
    @FXML
    private Button hapusButton;

//    sub tombol
    @FXML
    private Button subButton;

    ObservableList<Transaksi> list =
            FXCollections.observableArrayList();

    public void loadTable(){
        // tabel

//        atur ukuran colom
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
//            bersihkan tabel
            list.clear();

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
            }

//            tampilkan tabel
            tableTransaksi.setItems(list);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void bukaDashboard(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fauzan/mygalon/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) inputNama.getScene().getWindow();

            Scene scene = new Scene(root);

            stage.setScene(scene);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initialize(){
        totalLabel.setText("" + totalTransaksi);


        inputStatus.getItems().addAll(
                "diproses",
                "selesai"
        );

        inputMetode.getItems().addAll(
                "ambil",
                "diantar"
        );

        // default value
        inputMetode.setValue("ambil");
        inputStatus.setValue("diproses");

//        load table
        loadTable();
        getTableRow();
        setKirimButton();

    }

    public void setKirimButton(){
        if (modeEdit){
            kirimButton.setText("Perbarui");
            subButton.setText("Cancel");
            hapusButton.setManaged(true);
            hapusButton.setVisible(true);
        }
        else {
            kirimButton.setText("Buat");
            subButton.setText("Reset");
            hapusButton.setManaged(false);
            hapusButton.setVisible(false);
        }
    }

    //    select tabel row
    public void getTableRow(){
        tableTransaksi.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedData) -> {

                    if (selectedData != null) {
                        selectedId = selectedData.getIdTransaksi();
                        inputNama.setText(
                                selectedData.getNamaPembeli()
                        );
                        inputJumlahGalon.setText(
                                String.valueOf(selectedData.getJumlahGalon())
                        );
                        inputKurir.setText(
                                selectedData.getNamaKurir()
                        );
                        inputAlamat.setText(
                                selectedData.getAlamatPengantaran()
                        );
                        inputMetode.setValue(
                                selectedData.getMetodePengiriman()
                        );
                        inputStatus.setValue(
                                selectedData.getStatusTransaksi()
                        );
                        modeEdit = true;
                        setKirimButton();
                    }
                });
    }


    public void tambahTransaksi(){

//        validasi
        if(
                inputJumlahGalon.getText().isEmpty() ||
                inputMetode.getValue() == null ||
                inputStatus.getValue() == null
        ){
            labelStatus.setVisible(true);
            labelStatus.setManaged(true);
            labelStatus.setText("Error! Kolom Jumlah galon, Metode, Status harus diisi");
            return;
        }

//        validasi inputJumlahGalon
        if(!inputJumlahGalon.getText().matches("\\d+")) {
            labelStatus.setVisible(true);
            labelStatus.setManaged(true);
            labelStatus.setText("Error! Jumlah galon harus angka");
            return;
        }
//        ambil nilai
        String status = inputStatus.getValue();
        String metode = inputMetode.getValue();

//        konversi
        int jumlahGalon = Integer.parseInt(inputJumlahGalon.getText());

//        hitung harga
        if(metode.equals("diantar")){
            totalTransaksi = jumlahGalon * (hargaGalon + hargaAntar);
        }
        else {
            totalTransaksi = jumlahGalon * hargaGalon;
        }

        Transaksi transaksi = new Transaksi(
                inputNama.getText(),
                jumlahGalon,
                inputKurir.getText(),
                metode,
                inputAlamat.getText(),
                totalTransaksi,
                status
        );

        //        query
        String sql = """
                INSERT INTO transaksi (
                    namaPembeli,
                    jumlahGalon,
                    namaKurir,
                    metodePengiriman,
                    alamatPengantaran,
                    totalTransaksi,
                    statusTransaksi
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try(
                Connection connection = DatabaseConn.connect();
                PreparedStatement statement = connection.prepareStatement(sql);
                )
        {

            statement.setString(1,
                    transaksi.getNamaPembeli());

            statement.setInt(2,
                    transaksi.getJumlahGalon());

            statement.setString(3,
                    transaksi.getNamaKurir());

            statement.setString(4,
                    transaksi.getMetodePengiriman());

            statement.setString(5,
                    transaksi.getAlamatPengantaran());

            statement.setInt(6,
                    transaksi.getTotalTransaksi());

            statement.setString(7,
                    transaksi.getStatusTransaksi());

            statement.executeUpdate();

//            update tabel

            loadTable();

//            status
            labelStatus.setText("Succes! Transaksi berhasil di tambahkan!");

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

    public void updateTransaksi(){

        if(selectedId == -1){
            labelStatus.setText("Pilih data dulu!");
            return;
        }

        String sql = """
            UPDATE transaksi
            SET
                namaPembeli = ?,
                jumlahGalon = ?,
                namaKurir = ?,
                metodePengiriman = ?,
                alamatPengantaran = ?,
                totalTransaksi = ?,
                statusTransaksi = ?
            WHERE idTransaksi = ?
            """;

        try(
                Connection connection = DatabaseConn.connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
        ){

            String metode = inputMetode.getValue();

            int jumlahGalon =
                    Integer.parseInt(
                            inputJumlahGalon.getText()
                    );

            if(metode.equals("diantar")){
                totalTransaksi =
                        jumlahGalon *
                                (hargaGalon + hargaAntar);
            }
            else{
                totalTransaksi =
                        jumlahGalon * hargaGalon;
            }

            statement.setString(1,
                    inputNama.getText());

            statement.setInt(2,
                    jumlahGalon);

            statement.setString(3,
                    inputKurir.getText());

            statement.setString(4,
                    metode);

            statement.setString(5,
                    inputAlamat.getText());

            statement.setInt(6,
                    totalTransaksi);

            statement.setString(7,
                    inputStatus.getValue());

            statement.setInt(8,
                    selectedId);

            statement.executeUpdate();

            loadTable();

            labelStatus.setText(
                    "Data berhasil diupdate!"
            );

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteTransaksi(){
        String sql = "DELETE FROM transaksi WHERE idTransaksi = ?";
        try (
                Connection connection = DatabaseConn.connect();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ){
            stmt.setInt(1, selectedId);
            stmt.executeUpdate();

            loadTable();
            cancelButton();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void kirimTransaksi(){
        if(modeEdit){
            updateTransaksi();
        }
        else {
            tambahTransaksi();
        }
    }

    public void cancelButton(){
        inputNama.clear();

        inputJumlahGalon.clear();

        inputKurir.clear();

        inputAlamat.clear();

        inputMetode.setValue("ambil");

        inputStatus.setValue("diproses");

        selectedId = -1;

        tableTransaksi.getSelectionModel().clearSelection();

        modeEdit = false;
        setKirimButton();
    }

}