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

public class ChatLogInController {
    ClientFunctional clientFunctional = ClientHolder.getInstance().getClient();
    @FXML
    private Button logInButton;
    @FXML
    private Button backButton;
    @FXML
    private Label problemString;
    @FXML
    private TextField loginString;
    @FXML
    private TextField passwordString;
    @FXML
    private Button signUpButton;
    private String login;
    private String password;

    @FXML
    void initialize() {

        passwordString.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @FXML
            public void handle(KeyEvent event) {

                switch (event.getCode()) {
                    case ENTER:
                        logInButtonAction();
                        break;
                    case ESCAPE:
                        backButtonAction();
                        break;
                    default:
                        break;
                }
            }
        });

        logInButton.setOnAction((event) -> logInButtonAction());

        signUpButton.setOnAction((event) -> signUpButtonAction());

        backButton.setOnAction((event) -> backButtonAction());
    }

    private void logInButtonAction() {
        login = loginString.getText();
        password = passwordString.getText();
        Stage stage = (Stage) logInButton.getScene().getWindow();
        Parent root;
        if (clientFunctional.login(login, password)) {
            stage.close();
            if (clientFunctional.username.equals("admin")) {
                root = SceneChanger.changeScene("ChatAdminChat.fxml");
            } else {
                root = SceneChanger.changeScene("ChatChat.fxml");
            }
            stage.setTitle("Chat");
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            problemString.setText("                    Логин или пароль были введены неверно");
        }
    }

    private void backButtonAction() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
        clientFunctional.closeConnection();
        Parent root;
        root = SceneChanger.changeScene("ChatConnect.fxml");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void signUpButtonAction() {
        Stage stage = (Stage) signUpButton.getScene().getWindow();
        stage.close();
        Parent root;
        root = SceneChanger.changeScene("ChatRegistration.fxml");
        stage.setScene(new Scene(root));
        stage.show();
    }
}

