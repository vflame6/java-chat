package chat.Main.chatClient.controllers;

import chat.Main.chatClient.ClientFunctional;
import chat.Main.chatClient.ClientHolder;
import chat.Main.chatClient.SceneChanger;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.UnknownHostException;

public class ChatConnectController {
    ClientHolder clientHolder = ClientHolder.getInstance();
    @FXML
    private Label connectString;
    @FXML
    private Button connectButton;
    @FXML
    private TextField ipString;
    private String ip;
    @FXML
    void initialize() {
        connectButton.setOnAction((event) -> {
            ip = ipString.getText();
            Stage stage = (Stage) connectButton.getScene().getWindow();
            Parent root;
            try {
                ClientFunctional clientFunctional = new ClientFunctional(ip);
                if (clientFunctional.ping()) {
                    clientHolder.setClient(clientFunctional);
                    if (clientFunctional.clientCookies.isCookieExists()) {
                        String cookie = clientFunctional.clientCookies.getCookie();
                        if (clientFunctional.loginCookie(cookie)) {
                            stage.close();
                            root = SceneChanger.changeScene("ChatChat.fxml");
                            stage.setScene(new Scene(root));
                            stage.show();
                        } else {
                            stage.close();
                            root = SceneChanger.changeScene("ChatLogIn.fxml");
                            stage.setScene(new Scene(root));
                            stage.show();
                        }
                    } else {
                        stage.close();
                        root = SceneChanger.changeScene("ChatLogIn.fxml");
                        stage.setScene(new Scene(root));
                        stage.show();
                    }

                } else {
                    throw new UnknownHostException();
                }
            } catch (UnknownHostException e) {
                connectString.setText("                IP адресс не верный, попробуйте еще раз");
            }
        });
    }
}
