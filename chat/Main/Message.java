package chat.Main;

import java.io.*;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Message implements Serializable {
    private static final DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("E, dd MMM HH:mm").withZone(TimeZone.getDefault().toZoneId());
    private static final DateTimeFormatter minDateFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(TimeZone.getDefault().toZoneId());
    private final int id;
    private final String from;
    private final String content;
    private final Timestamp timestamp;

    // Класс оболочка для каждого сообщения. Хранит в себе:
    // int id поля в базе
    // String from имя пользователя отправителя
    // String content само сообщение
    // Timestamp date время отправки сообщения
    public Message(int id, String from, String content, Timestamp timestamp) {
        this.id = id;
        this.from = from;
        this.content = content;
        this.timestamp = timestamp;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    // Форматирование времени отправки для нормальной читаемости, без миллисекунд
    public String getFullFormattedDate() {
        return timestamp.toLocalDateTime().format(fullDateFormatter);
    }

    public String getMinFormattedDate() {
        return timestamp.toLocalDateTime().format(minDateFormatter);
    }

    // Удобочитаемый вид, возвращает строку
    @Override
    public String toString() {
        return String.format("Message(%d, %s, %s, %s)", id, from, content, timestamp);
    }

    // Метод для сравнения объектов.
    // Чтобы считать объекты одинаковыми, проверяется ссылка, принадлежность к классу и хэш-значение объектов.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id && Objects.equals(from, message.from) && Objects.equals(content, message.content) && Objects.equals(timestamp, message.timestamp);
    }

    // Метод для получения хэш-значения объектов.
    @Override
    public int hashCode() {
        return Objects.hash(id, from, content, timestamp);
    }

    // Методы (де)сериализации. Нужны для преобразования объектов в строку Base64 и передачи между клиентом и сервером.
    // Сериализация объекта сообщения в строку Base64.
    public String encodeMessage() {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    // Статический метод для сериализации списка сообщений в строку Base64.
    public static String encodeMessages(List<Message> messageList) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(messageList);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    // Статический метод для десериализации сообщений в виде строк Base64.
    public static Message decodeMessage(String encodedMessage) {
        try {
            byte[] base64DecodedBytes = Base64.getDecoder().decode(encodedMessage);
            InputStream in = new ByteArrayInputStream(base64DecodedBytes);
            ObjectInputStream obin = new ObjectInputStream(in);
            Object object = obin.readObject();
            return (Message) object;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Статический метод для десериализации списка сообщений.
    public static List<Message> decodeMessages(String encodedMessages) {
        try {
            byte[] base64DecodedBytes = Base64.getDecoder().decode(encodedMessages);
            InputStream in = new ByteArrayInputStream(base64DecodedBytes);
            ObjectInputStream obin = new ObjectInputStream(in);
            Object object = obin.readObject();
            return (List<Message>) object;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
