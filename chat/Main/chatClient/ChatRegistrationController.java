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

public class ChatRegistrationController {
    @FXML
    private TextField LoginString;
    @FXML
    private TextField PasswordString;
    @FXML
    private Button SignUpButton;
    @FXML
    private TextField TelephoneString;
    private String telephone;
    private String password;
    private String login;

    @FXML
    void initialize() {
        SignUpButton.setOnAction((event) -> {
            telephone = TelephoneString.getText();
            password = PasswordString.getText();
            login = LoginString.getText();
            Stage stage = (Stage) SignUpButton.getScene().getWindow();
            stage.close();
            Parent root = null;
            try {
                if (true) {
                    File file = new File("resources/ChatChat.fxml");
                    URL url1 = new URL("file:/" + file.getAbsolutePath());
                    root = FXMLLoader.load(url1);
                } else {
                    File file = new File("resources/ChatRegistrationProblem.fxml");
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