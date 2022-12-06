package chat.Main.chatClient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.UnknownHostException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ChatConnectController {
    ClientHolder clientHolder = ClientHolder.getInstance();
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
            Parent root = null;
            try {
                Client client = new Client(ip);
                if (client.ping()) {
                    clientHolder.setClient(client);
                    try {
                        File file = new File("resources/ChatLogIn.fxml");
                        URL url1 = new URL("file:/" + file.getAbsolutePath());
                        root = FXMLLoader.load(Objects.requireNonNull(url1));
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new UnknownHostException();
                }
            } catch (UnknownHostException e) {
                try {
                    File file = new File("resources/ChatConnectProblem.fxml");
                    URL url2 = new URL("file:/" + file.getAbsolutePath());
                    root = FXMLLoader.load(Objects.requireNonNull(url2));
                } catch (IOException exception2) {
                    exception2.printStackTrace();
                    throw new RuntimeException(exception2);
                }
            }

            stage.close();
            stage.setScene(new Scene(root));
            stage.show();
        });
    }
}