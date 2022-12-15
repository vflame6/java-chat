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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.util.List;

public class ChatChatController {
    ClientFunctional clientFunctional = ClientHolder.getInstance().getClient();
    @FXML
    private AnchorPane chat;
    @FXML
    private Label userTimeLabel;
    @FXML
    private Button logOutButton;
    @FXML
    private Button updateButton;
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

        if (clientFunctional.getMessages()) {
            messageList = clientFunctional.messageList;
            time = messageList.get(messageList.size() - 1).getTimestamp();
            user = messageList.get(messageList.size() - 1).getFrom();
            for (Message message : messageList) {
                displayMessage(message);
                }
            miny = 15;
            userTimeLabel.setText("  " + user + ": " + messageList.get(messageList.size() - 1).getMinFormattedDate());
            }

        updateButton.setOnAction((event) -> {
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
                userTimeLabel.setText("  " + user + ": " + messageList.get(messageList.size() - 1).getMinFormattedDate());
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

        darkButton.setOnAction((event) -> {
            Stage stage = (Stage) darkButton.getScene().getWindow();
            stage.close();
            Parent root = null;
            if(stage.getTitle().equals("Chat")){
                root = SceneChanger.changeScene("ChatDarkChat.fxml");
                stage.setTitle("DarkChat");
            } else {
                root = SceneChanger.changeScene("ChatChat.fxml");
                stage.setTitle("Chat");
            }
            stage.setScene(new Scene(root));
            stage.show();
        });
        }

        public void displayMessage(Message message) {
            int lengthAddress = message.getFrom().length() + message.getFullFormattedDate().length() + 1;
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
            timeLabel.setText(message.getFrom() + ": " + message.getFullFormattedDate());
            miny += (35 * rows) + 30;
            if ((miny - 15) % 6 == 0){
                chat.setPrefHeight(chat.getPrefHeight() + 400);
            }
            chat.getChildren().add(messageLabel);
            chat.getChildren().add(timeLabel);
        }
    }

