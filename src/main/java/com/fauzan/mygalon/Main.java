    package com.fauzan.mygalon;

    import javafx.application.Application;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Scene;
    import javafx.scene.image.Image;
    import javafx.stage.Stage;
    import java.io.IOException;

    public class Main extends Application {
        @Override
        public void start(Stage stage) throws IOException {
    //        fxml
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("login.fxml"));

            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());

            stage.setTitle("MyGalon - Sistem Pendataan Transaksi - RM. Fauzan");
            stage.setScene(scene);
            stage.show();
            stage.getIcons().add(
                    new Image(Main.class.getResourceAsStream("assets/logo.png"))
            );

        }
        public static void main(String[] args) {
            Application.launch(Main.class, args);
        }
    }
