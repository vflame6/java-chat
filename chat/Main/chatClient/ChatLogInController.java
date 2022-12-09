package chat.Main.chatClient;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChatLogInController {
    ClientFunctional clientFunctional = ClientHolder.getInstance().getClient();
    @FXML
    private Button LogInButton;
    @FXML
    private TextField LoginString;
    @FXML
    private TextField PasswordString;
    @FXML
    private Button SignUpButton;
    private String login;
    private String password;

    @FXML
    void initialize() {
        LogInButton.setOnAction((event) -> {
            login = LoginString.getText();
            password = PasswordString.getText();
            Stage stage = (Stage) LogInButton.getScene().getWindow();
            stage.close();
            Parent root = null;
            if ((login.equals("nova")) && (password.equals("nova16"))) {
                root = SceneChanger.changeScene("ChatChat.fxml");
            } else {
                root = SceneChanger.changeScene("ChatLogInProblem.fxml");
            }

            stage.setScene(new Scene(root));
            stage.show();
        });

        SignUpButton.setOnAction((event) -> {
            Stage stage = (Stage) SignUpButton.getScene().getWindow();
            stage.close();
            Parent root = null;
            root = SceneChanger.changeScene("ChatRegistration.fxml");
            stage.setScene(new Scene(root));
            stage.show();
        });
    }
}
