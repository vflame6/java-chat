package chat.Main.chatClient.controllers;
import chat.Main.Message;
import chat.Main.chatClient.ClientFunctional;
import chat.Main.chatClient.ClientHolder;
import chat.Main.chatClient.SceneChanger;
import chat.Main.chatClient.util.TextProcessor;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.util.List;
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
    private int minx = 5;
    private int miny = 15;
    private Timestamp time;
    private String user;
    private List<Message> messageList;

    @FXML
    void initialize() {

        ImageView update = new ImageView(new Image("/resources/update.png",32,28,false,false));
        updateButton.graphicProperty().setValue(new ImageView(update.getImage()));
        ImageView home = new ImageView(new Image("/resources/home.png",32,28,false,false));
        logOutButton.graphicProperty().setValue(new ImageView(home.getImage()));
        ImageView theme = new ImageView(new Image("/resources/theme.png",32,28,false,false));
        darkButton.graphicProperty().setValue(new ImageView(theme.getImage()));
        ImageView send = new ImageView(new Image("/resources/send.png",32,28,false,false));
        sendButton.graphicProperty().setValue(new ImageView(send.getImage()));
        ImageView search = new ImageView(new Image("/resources/search.png",25,25,false,false));
        searchButton.graphicProperty().setValue(new ImageView(search.getImage()));

        if (clientFunctional.getConfig(1)) {
            chatName.setText(clientFunctional.chatName);
            chatName1.setText(clientFunctional.chatName);
        }

        if (clientFunctional.getMessages()) {
            messageList = clientFunctional.messageList;
            time = messageList.get(messageList.size() - 1).getTimestamp();
            user = messageList.get(messageList.size() - 1).getFrom();
            for (Message message : messageList) {
                displayMessage(message);
                }
            miny = 15;
            userTimeString.setText("  " + user + ": " + messageList.get(messageList.size() - 1).getMinFormattedDate());
            }

        searchButton.setOnAction((event) -> {
            if((searchString.getText()).equals(clientFunctional.chatName)){
                chatPane.setStyle("-fx-background-color: #D1E8E2;-fx-background-radius:10; -fx-border-width: 3; -fx-border-color: #116466; -fx-border-radius: 10;");
            }
            searchString.clear();
        });

        updateButton.setOnAction((event) -> {
            if (clientFunctional.getConfig(1)) {
                chatName.setText(clientFunctional.chatName);
                chatName1.setText(clientFunctional.chatName);
            }
            clientFunctional.getLastMessageTimestamp();
            if (clientFunctional.lastMessageTimestamp.compareTo(time) != 0) {
                if (clientFunctional.getMessages()) {
                    chat.getChildren().clear();
                    messageList = clientFunctional.messageList;
                    time = messageList.get(messageList.size() - 1).getTimestamp();
                    user = messageList.get(messageList.size() - 1).getFrom();
                    for (Message message : messageList) {
                        displayMessage(message);
                    }
                }
                miny = 15;
                userTimeString.setText("  " + user + ": " + messageList.get(messageList.size() - 1).getMinFormattedDate());
            }
        });

        sendButton.setOnAction((event) -> {
            String message = enterMessage.getText();
            if (!message.equals("")) {
                for (String singleMessage : TextProcessor.separateMessages(message)) {
                        clientFunctional.sendMessage(singleMessage);
                }
            }
            enterMessage.clear();
        });

        logOutButton.setOnAction((event) -> {
            Stage stage = (Stage) logOutButton.getScene().getWindow();
            stage.close();
            Parent root = null;
            root = SceneChanger.changeScene("ChatLogIn.fxml");
            stage.setScene(new Scene(root));
            String cookie = clientFunctional.clientCookies.getCookie();
            clientFunctional.logout(cookie);
            stage.show();
        });

        if (clientFunctional.username.equals("admin")) {
            deleteButton.setOnAction((event) -> {
                TextInputDialog name = new TextInputDialog();
                name.setTitle("Удалить сообщение");
                name.setContentText("Пожалуйста, напишите id сообщения, которое хотите удалить:");
                Optional<String> result = name.showAndWait();
                if (result.isPresent()) {
                    clientFunctional.deleteMessage(Integer.valueOf(result.get()));
                }
            });
        }

        changeNameButton.setOnAction((event) -> {
            TextInputDialog name = new TextInputDialog();
            name.setTitle("Изменить название беседы");
            name.setContentText("Пожалуйста, введите новое название:");
            Optional<String> result = name.showAndWait();
            if (result.isPresent()) {
                clientFunctional.updateConfig(1, result.get());
            }
        });

        darkButton.setOnAction((event) -> {
            Stage stage = (Stage) darkButton.getScene().getWindow();
            stage.close();
            Parent root = null;
            if(stage.getTitle().equals("Chat")){
                if(clientFunctional.username.equals("admin")){
                    root = SceneChanger.changeScene("ChatAdminDarkChat.fxml");
                } else {
                    root = SceneChanger.changeScene("ChatDarkChat.fxml");
                }
                stage.setTitle("DarkChat");
            } else {
                if(clientFunctional.username.equals("admin")){
                    root = SceneChanger.changeScene("ChatAdminChat.fxml");
                } else {
                    root = SceneChanger.changeScene("ChatChat.fxml");
                }
                stage.setTitle("Chat");
            }
            stage.setScene(new Scene(root));
            stage.show();
        });
        }

        public void displayMessage(Message message) {
            int lengthAddress = message.getFrom().length() + message.getFullFormattedDate().length() + 3;
            int rows = message.getContent().length() / 44 + 1;
            Label messageLabel = new Label();
            if(message.getFrom().equals(clientFunctional.username)){
                messageLabel.setStyle("-fx-background-color:  #77ACA5; -fx-background-radius: 10");
            } else {
                messageLabel.setStyle("-fx-background-color: #B7D8CF; -fx-background-radius: 10");
            }
            messageLabel.setMinSize(420, 35 * rows);
            messageLabel.setLayoutX(minx);
            messageLabel.setLayoutY(miny);
            messageLabel.setText(TextProcessor.processMessage(message.getContent()));
            Label timeLabel = new Label();
            timeLabel.setMinSize(lengthAddress * 6, 20);
            timeLabel.setLayoutY(miny + 35 * rows);
            timeLabel.setLayoutX(435 - lengthAddress * 6);
            timeLabel.setText(message.getId() + ": " + message.getFrom() + ": " + message.getFullFormattedDate());
            miny += (35 * rows) + 30;
            if (miny + 35> chat.getPrefHeight()) {
                chat.setPrefHeight(chat.getPrefHeight() + 400);
            }
            chat.getChildren().add(messageLabel);
            chat.getChildren().add(timeLabel);
        }
    }

