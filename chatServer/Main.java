package chatServer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        Message message1 = new Message(1, "admin", "Hello, world!", date);
        System.out.println("Start: " + message1);
        String encoded1 = message1.encodeMessage();
        System.out.println("Encoded: " + encoded1);
        Message decoded1 = Message.decodeMessage(encoded1);
        System.out.println("Decoded: " + decoded1);
        System.out.println(message1.equals(decoded1));

        Message message2 = new Message(2, "test", "test", date);

        List<Message> messageList = new ArrayList<>();
        messageList.add(message1);
        messageList.add(message2);
        System.out.println("Initial list of messages: " + messageList);
        String encodedMessages = Message.encodeMessages(messageList);
        System.out.println("Encoded list of messages: " + encodedMessages);
        List<Message> decodedMessages = Message.decodeMessages(encodedMessages);
        System.out.println("Decoded list of messages: " + decodedMessages);
        System.out.println(messageList.equals(decodedMessages));
        System.out.println(DBConnect.getUser(1));

    }
}

