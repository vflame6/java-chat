package chat.Main.chatClient;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.UnknownHostException;

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
                ClientFunctional clientFunctional = new ClientFunctional(ip);
                if (clientFunctional.ping()) {
                    clientHolder.setClient(clientFunctional);
                    if (clientFunctional.clientCookies.isCookieExists()) {
                        String cookie = clientFunctional.clientCookies.getCookie();
                        if (clientFunctional.loginCookie(cookie)) {
                            root = SceneChanger.changeScene("ChatChat.fxml");
                        } else {
                            root = SceneChanger.changeScene("ChatLogIn.fxml");
                        }
                    } else {
                        root = SceneChanger.changeScene("ChatLogIn.fxml");
                    }
                } else {
                    throw new UnknownHostException();
                }
            } catch (UnknownHostException e) {
                root = SceneChanger.changeScene("ChatConnectProblem.fxml");
            }

            stage.close();
            stage.setScene(new Scene(root));
            stage.show();
        });
    }
}