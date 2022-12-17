package chat.Main.chatClient.controllers;

import chat.Main.chatClient.ClientFunctional;
import chat.Main.chatClient.util.ClientHolder;
import chat.Main.chatClient.util.SceneChanger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
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
        // Чтение ввода кнопок с поля ввода ip-адреса
        // Enter-отправить запрос на соединение с введеным ip
        // Escape- выйти из программы

        ipString.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @FXML
            public void handle(KeyEvent event) {

                switch(event.getCode()) {
                    case ENTER:
                        connectButtonAction();
                        break;
                    case ESCAPE:
                        System.exit(0);
                    default:
                        break;
                }
            }
        });
        connectButton.setOnAction((event) -> connectButtonAction());
    }
    private void tryToLogInWithCookie(Stage stage, ClientFunctional clientFunctional) {
        String cookie = clientFunctional.clientCookies.getCookie();
        Parent root;
        if (clientFunctional.loginCookie(cookie)) {
            stage.close();
            if (clientFunctional.username.equals("admin")) {
                root = SceneChanger.changeScene("ChatAdminChat.fxml");
            } else {
                root = SceneChanger.changeScene("ChatChat.fxml");
            }
            stage.setScene(new Scene(root));
            stage.setTitle("Chat");
            stage.show();
        } else {
            stage.close();
            root = SceneChanger.changeScene("ChatLogIn.fxml");
            stage.setScene(new Scene(root));
            stage.show();
        }
    }
    private void connectButtonAction(){
        ip = ipString.getText();
        Stage stage = (Stage) connectButton.getScene().getWindow();
        Parent root = null;
        try {
            ClientFunctional clientFunctional = ClientFunctional.tryToConnect(ip);
            clientHolder.setClient(clientFunctional);

            if (clientFunctional.clientCookies.isCookieExists()) {
                tryToLogInWithCookie(stage, clientFunctional);
            } else {
                stage.close();
                root = SceneChanger.changeScene("ChatLogIn.fxml");
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (UnknownHostException e) {
            connectString.setText("                IP адресс не верный, попробуйте еще раз");
        }
    }
}
