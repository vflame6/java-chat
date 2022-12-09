package chat.Main.chatClient;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChatRegistrationController {
    ClientFunctional clientFunctional = ClientHolder.getInstance().getClient();
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
            if (true) {
                root = SceneChanger.changeScene("ChatChat.fxml");
            } else {
                root = SceneChanger.changeScene("ChatRegistrationProblem.fxml");
            }
            stage.setScene(new Scene(root));
            stage.show();
        });
    }
}