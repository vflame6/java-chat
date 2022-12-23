package chat.Main.chatClient.controllers;

import chat.Main.Message;
import chat.Main.chatClient.ClientFunctional;
import chat.Main.chatClient.util.AuthenticationRequiredException;
import chat.Main.chatClient.util.ClientHolder;
import chat.Main.chatClient.util.SceneChanger;
import chat.Main.chatClient.util.TextProcessor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ChatChatController {
    ClientFunctional clientFunctional = ClientHolder.getInstance().getClient();
    @FXML
    private AnchorPane chat;
    @FXML
    private AnchorPane chatPane;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchString;
    @FXML
    private Button changeNameButton;
    @FXML
    private Label userTimeString;
    @FXML
    private Label chatName;
    @FXML
    private Label chatName1;
    @FXML
    private TextField enterMessage;
    @FXML
    private Button sendButton;
    @FXML
    private Button darkButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button logOutButton;
    @FXML
    private Button banButton;
    @FXML
    private Button deleteButton;
    private int miny = 15;
    private Timestamp time;
    private KeyCode lastKey;
    private final List<Label> messageLabels = new ArrayList<>();
    private Timeline updateActionsEvery5Seconds;
    private boolean loggedIn;

    @FXML
    void initialize() {
        loggedIn = true;
        try {
            loadButtonImages();
            loadConfig();
            loadMessages();
            clientFunctional.getLastMessageTimestamp();
            time = clientFunctional.lastMessageTimestamp;
            scheduleUpdateActions();
        } catch (AuthenticationRequiredException e) {
            logoutAction();
        }

        // Чтение ввода кнопок с поля ввода сообщений
        // Enter-отправить сообщение
        // Control+R- обновить список сообщений
        // Escape-выйти из аккаунта
        enterMessage.setOnKeyPressed(new EventHandler<>() {
            @FXML
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER -> sendButtonAction();
                    case ESCAPE -> logoutButtonAction();
                    case R -> {
                        if (lastKey == KeyCode.CONTROL) {
                            updateAction();
                        }
                    }
                    default -> lastKey = event.getCode();
                }
            }
        });

        searchButton.setOnAction((event) -> searchButtonAction());
        updateButton.setOnAction((event) -> updateButtonAction());
        sendButton.setOnAction((event) -> sendButtonAction());
        logOutButton.setOnAction((event) -> logoutButtonAction());
        changeNameButton.setOnAction((event) -> changeNameButtonAction());
        darkButton.setOnAction((event) -> darkButtonAction());

        if (clientFunctional.username.equals("admin")) {
            loadAdminInterface();
        }
    }

    // Отобразить сообщение
    public void displayMessage(Message message) {
        int lengthAddress = message.getFrom().length() + message.getFullFormattedDate().length() + 3;
        int rows = message.getContent().length() / 44 + 1;
        Label messageLabel = new Label();

        if (message.getFrom().equals(clientFunctional.username)) {
            messageLabel.setStyle("-fx-background-color:  #77ACA5; -fx-background-radius: 10");
        } else {
            messageLabel.setStyle("-fx-background-color: #B7D8CF; -fx-background-radius: 10");
        }

        messageLabel.setMinSize(420, 35 * rows);
        int minx = 5;
        messageLabel.setLayoutX(minx);
        messageLabel.setLayoutY(miny);
        messageLabel.setText(TextProcessor.processMessage(message.getContent()));

        Label timeLabel = new Label();
        timeLabel.setMinSize(lengthAddress * 6, 20);
        timeLabel.setLayoutY(miny + 35 * rows);
        timeLabel.setLayoutX(435 - lengthAddress * 6);
        timeLabel.setText(message.getId() + ": " + message.getFrom() + ": " + message.getFullFormattedDate());
        miny += (35 * rows) + 30;
        if (miny + 35 > chat.getPrefHeight()) {
            chat.setPrefHeight(chat.getPrefHeight() + 400);
        }
        chat.getChildren().add(messageLabel);
        messageLabels.add(messageLabel);
        chat.getChildren().add(timeLabel);
        messageLabels.add(timeLabel);
    }

    // Стереть все отрисованные сообщения
    public void clearMessages() {
        chat.getChildren().removeAll(messageLabels);
        messageLabels.clear();
    }

    // Загрузить и отобразить картинки на кнопках
    private void loadButtonImages() {
        ImageView update = new ImageView(new Image("/resources/update.png", 32, 28, false, false));
        updateButton.graphicProperty().setValue(new ImageView(update.getImage()));
        ImageView home = new ImageView(new Image("/resources/home.png", 32, 28, false, false));
        logOutButton.graphicProperty().setValue(new ImageView(home.getImage()));
        ImageView theme = new ImageView(new Image("/resources/theme.png", 32, 28, false, false));
        darkButton.graphicProperty().setValue(new ImageView(theme.getImage()));
        ImageView send = new ImageView(new Image("/resources/send.png", 32, 28, false, false));
        sendButton.graphicProperty().setValue(new ImageView(send.getImage()));
        ImageView search = new ImageView(new Image("/resources/search.png", 25, 25, false, false));
        searchButton.graphicProperty().setValue(new ImageView(search.getImage()));
    }

    // Загрузить и отобразить имя чата
    private void loadConfig() {
        if (clientFunctional.getConfig(1)) {
            chatName.setText(clientFunctional.chatName);
            chatName1.setText(clientFunctional.chatName);
        }
    }

    private void loadMessages() {
        if (clientFunctional.getMessages()) {
            List<Message> messageList = clientFunctional.messageList;
            Message lastMessage = messageList.get((messageList.size() - 1));

            for (Message message : messageList) {
                displayMessage(message);
            }

            miny = 15;
            userTimeString.setText("  " + lastMessage.getFrom() + ": " + lastMessage.getMinFormattedDate());
        }
    }

    private void loadAdminInterface() {
        deleteButton.setOnAction((event) -> deleteButtonAction());
        banButton.setOnAction((event) -> banButtonAction());
    }

    private void changeTheme(Stage stage) {
        Parent root;
        if (stage.getTitle().equals("Chat")) {
            if (clientFunctional.username.equals("admin")) {
                root = SceneChanger.changeScene("ChatAdminDarkChat.fxml");
            } else {
                root = SceneChanger.changeScene("ChatDarkChat.fxml");
            }
            stage.setTitle("DarkChat");
        } else {
            if (clientFunctional.username.equals("admin")) {
                root = SceneChanger.changeScene("ChatAdminChat.fxml");
            } else {
                root = SceneChanger.changeScene("ChatChat.fxml");
            }
            stage.setTitle("Chat");
        }
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void searchButtonAction() {
        if ((searchString.getText()).equals(clientFunctional.chatName)) {
            chatPane.setStyle("-fx-background-color: #D1E8E2;-fx-background-radius:10; -fx-border-width: 3; -fx-border-color: #116466; -fx-border-radius: 10;");
        }
        searchString.clear();
    }

    // Метод для выхода из текущей сессии (при ошибке AuthenticationRequiredException).
    private void logoutAction() {
        stopUpdateActions();
        if (!loggedIn) {
            return;
        }

        Stage stage = (Stage) logOutButton.getScene().getWindow();
        if (Objects.isNull(stage)) {
            return;
        }
        stage.close();
        Parent root;
        root = SceneChanger.changeScene("ChatLogIn.fxml");
        stage.setScene(new Scene(root));
        clientFunctional.clientCookies.deleteCookie();
        loggedIn = false;
        stage.show();
    }


    private void logoutButtonAction() {
        stopUpdateActions();
        if (!loggedIn) {
            return;
        }

        Stage stage = (Stage) logOutButton.getScene().getWindow();
        if (Objects.isNull(stage)) {
            return;
        }
        stage.close();
        Parent root;
        root = SceneChanger.changeScene("ChatLogIn.fxml");
        stage.setScene(new Scene(root));
        String cookie = clientFunctional.clientCookies.getCookie();
        clientFunctional.logout(cookie);
        loggedIn = false;
        stage.show();
    }

    // Метод для автоматического обновления чата (с проверкой времени последнего изменения на сервере).
    private void updateAction() {
        if (!loggedIn) {
            return;
        }
        try {
            clientFunctional.getLastMessageTimestamp();
            if (clientFunctional.lastMessageTimestamp.compareTo(time) != 0) {
                loadConfig();
                clearMessages();
                loadMessages();
                time = clientFunctional.lastMessageTimestamp;
            }
        } catch (AuthenticationRequiredException e) {
            logoutAction();
        }
    }

    // Метод для принудительного обновления чата. Вызывается кнопкой и при вызове методов, делающих изменения на сервере.
    private void updateButtonAction() {
        try {
            clientFunctional.getLastMessageTimestamp();
            time = clientFunctional.lastMessageTimestamp;
            loadConfig();
            clearMessages();
            loadMessages();
        } catch (AuthenticationRequiredException e) {
            logoutAction();
        }
    }

    // Метод для отправки сообщений на сервер. Вызывается кнопкой sendButton.
    private void sendButtonAction() {
        try {
            String message = enterMessage.getText();
            if (!message.equals("")) {
                for (String singleMessage : TextProcessor.separateMessages(message)) {
                    clientFunctional.sendMessage(singleMessage);
                }
            }
            enterMessage.clear();
            updateButtonAction();
        } catch (AuthenticationRequiredException e) {
            logoutAction();
        }
    }

    private void changeNameButtonAction() {
        try {
            TextInputDialog name = new TextInputDialog();
            name.setTitle("Изменить название беседы");
            name.setContentText("Пожалуйста, введите новое название:");
            Optional<String> result = name.showAndWait();
            if (result.isPresent()) {
                clientFunctional.updateConfig(1, result.get());
            }
            updateButtonAction();
        } catch (AuthenticationRequiredException e) {
            logoutAction();
        }
    }

    private void darkButtonAction() {
        Stage stage = (Stage) darkButton.getScene().getWindow();
        stage.close();
        changeTheme(stage);
        updateButtonAction();
    }



    private void deleteButtonAction() {
        TextInputDialog name = new TextInputDialog();
        name.setTitle("Удалить сообщение");
        name.setContentText("Пожалуйста, напишите id сообщения, которое хотите удалить:");
        Optional<String> result = name.showAndWait();
        if (result.isPresent()) {
            clientFunctional.deleteMessage(Integer.parseInt(result.get()));
        }
        updateButtonAction();
    }

    private void banButtonAction() {
        TextInputDialog name = new TextInputDialog();
        name.setTitle("Удалить пользователя");
        name.setContentText("Пожалуйста, напишите имя пользователя, которого хотите удалить:");
        Optional<String> result = name.showAndWait();
        if (result.isPresent()) {
            clientFunctional.deleteUser(result.get());
        }
        updateButtonAction();
    }

    private void scheduleUpdateActions() {
         updateActionsEvery5Seconds = new Timeline(
                new KeyFrame(Duration.seconds(5),
                        e -> updateAction()
                ));
        updateActionsEvery5Seconds.setCycleCount(Timeline.INDEFINITE);
        updateActionsEvery5Seconds.play();
    }

    private void stopUpdateActions() {
        if (updateActionsEvery5Seconds != null) {
            updateActionsEvery5Seconds.stop();
            updateActionsEvery5Seconds = null;
        }
    }
}
