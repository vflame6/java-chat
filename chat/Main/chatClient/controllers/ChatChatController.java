package chat.Main.chatClient.controllers;
import chat.Main.Message;
import chat.Main.chatClient.ClientFunctional;
import chat.Main.chatClient.ClientHolder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.Timestamp;
import java.util.List;

public class ChatChatController {
    ClientFunctional clientFunctional = ClientHolder.getInstance().getClient();
    @FXML
    private AnchorPane chat;
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

    private int minx = 5;
    private int miny = 15;
    private int distance = 0;
    private Timestamp time;
    private List<Message> messageList;


    @FXML
    void initialize() {
        if (clientFunctional.getMessages()) {
            messageList = clientFunctional.messageList;
            time = messageList.get(messageList.size() - 1).getDate();
            for (Message message : messageList) {
                displayMessage(message);
                distance += 5;
                }
            miny = 15;
            distance = 0;
            }

        updateButton.setOnAction((event) -> {
            clientFunctional.getLastMessageTimestamp();
            if (clientFunctional.lastMessageTimestamp.compareTo(time) != 0) {
                if (clientFunctional.getMessages()) {
                    chat.getChildren().clear();
                    messageList = clientFunctional.messageList;
                    time = messageList.get(messageList.size() - 1).getDate();
                    for (Message message : messageList) {
                        displayMessage(message);
                        distance += 5;
                    }
                }
                miny = 15;
                distance = 0;
            }
        });

            sendButton.setOnAction((event) -> {
                String message = enterMessage.getText();
                if (!message.equals("")) {
                    clientFunctional.sendMessage(message);
                }
                enterMessage.clear();
            });
        }
        public void displayMessage(Message message){
            int rows = message.getContent().length() / 44 + 1;
            Label lbl = new Label();
            lbl.setMinHeight(35 * rows);
            lbl.setMinWidth(420);
            lbl.setLayoutX(minx);
            lbl.setLayoutY(miny + distance);
            lbl.setText(" " + message.getContent());
            miny += 35 * rows;
            chat.getChildren().add(lbl);
        }
    }

