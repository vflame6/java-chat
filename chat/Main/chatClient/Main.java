package chat.Main.chatClient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

// To launch:
// Import libraries
// javafx-sdk-19/lib
// sqlite-jdbc-3.40.0.0.jar
// VM options:
// --module-path "\path\to\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml
public class Main extends javafx.application.Application {
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        File file = new File("resources/ChatConnect.fxml");
        URL url = new URL("file:/" + file.getAbsolutePath());
        fxmlLoader.setLocation(url);
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("KiChatApp");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
