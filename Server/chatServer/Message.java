package chatServer;

import java.sql.Timestamp;

public class Message {
    private final int id;
    private final String from;
    private final String content;
    private final Timestamp date;

    public Message(int id, String from, String content, Timestamp date) {
        this.id = id;
        this.from = from;
        this.content = content;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format("Message(%d, %s, %s, %s, %s, %d, %d)", id, from, content, date.toString());
    }
}
