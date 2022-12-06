package chat.Main.chatClient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ChatConnectController {
    @FXML
    private Button ConnectButton;
    @FXML
    private TextField IPString;
    private String ip;
    @FXML
    void initialize() {
        ConnectButton.setOnAction((event) -> {
            ip = IPString.getText();
            Stage stage = (Stage) ConnectButton.getScene().getWindow();
            stage.close();
            Parent root = null;
            try {
                if (ip.equals("858545")) {
                    File file = new File("resources/ChatLogIn.fxml");
                    URL url1 = new URL("file:/" + file.getAbsolutePath());
                    root = FXMLLoader.load(Objects.requireNonNull(url1));
                } else {
                    File file = new File("resources/ChatConnectProblem.fxml");
                    URL url2 = new URL("file:/" + file.getAbsolutePath());
                    root = FXMLLoader.load(Objects.requireNonNull(url2));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            stage.setScene(new Scene(root));
            stage.show();
        });
    }
}