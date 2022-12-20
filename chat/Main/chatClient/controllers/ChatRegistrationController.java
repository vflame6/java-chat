package chat.Main.chatClient.controllers;

import chat.Main.chatClient.util.InvalidTelephoneException;
import chat.Main.chatClient.ClientFunctional;
import chat.Main.chatClient.util.ClientHolder;
import chat.Main.chatClient.util.SceneChanger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ChatRegistrationController {
    ClientFunctional clientFunctional = ClientHolder.getInstance().getClient();
    @FXML
    private Label problemString;
    @FXML
    private Button backButton;
    @FXML
    private TextField loginString;
    @FXML
    private TextField passwordString;
    @FXML
    private PasswordField passwordRepeatString;
    @FXML
    private Button signUpButton;
    @FXML
    private TextField telephoneString;
    private String telephone;
    private String password;
    private String login;
    private String passwordrep;

    @FXML
    void initialize() {

        // Чтение ввода кнопок с поля ввода пароля
        // Enter-отправить запрос на регистрацию с введенными данными
        // Escape- выйти к логину
        passwordRepeatString.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @FXML
            public void handle(KeyEvent event) {

                switch (event.getCode()) {
                    case ENTER -> signUpButtonAction();

                    case ESCAPE -> backButtonAction();

                    default -> {
                    }
                }
            }
        });
        signUpButton.setOnAction((event) -> signUpButtonAction());

        backButton.setOnAction((event) -> backButtonAction());
    }

    private void signUpButtonAction() {
        telephone = telephoneString.getText();
        password = passwordString.getText();
        passwordrep = passwordRepeatString.getText();
        login = loginString.getText();
        Stage stage = (Stage) signUpButton.getScene().getWindow();
        Parent root;

        if (!password.equals(passwordrep)) {
            problemString.setText("                                                                 Пароли не совпадают");
        }
        if (password.equals("")) {
         problemString.setText("                                                             Пароль не может быть пустым");
        }
        else {
            try {
                if (clientFunctional.register(login, password, telephone)) {
                    stage.close();
                    root = SceneChanger.changeScene("ChatLogIn.fxml");
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    problemString.setText("                                                          Имя пользователя уже занято");
                }
            } catch (InvalidTelephoneException e) {
                problemString.setText("                                                          Телефон был введен неверно");
            }
        }
    }

    private void backButtonAction() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
        Parent root;
        root = SceneChanger.changeScene("ChatLogIn.fxml");
        stage.setScene(new Scene(root));
        stage.show();
    }
}