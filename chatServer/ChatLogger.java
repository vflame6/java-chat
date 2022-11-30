package chatServer;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatLogger {
    private static final String messageLogFile = "log/message_log.txt";
    private static final String accessLogFile = "log/access_log.txt";
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private ChatLogger() {}

    public static void logMessage(Message message) {
        try (FileWriter fileWriter = new FileWriter(messageLogFile, true)) {
            fileWriter.write(message.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logAccess(InetAddress inetAddress, String command) {
        try (FileWriter fileWriter = new FileWriter(accessLogFile, true)) {
            LocalDateTime now = LocalDateTime.now();
            fileWriter.write(now.format(format) +
                    " " + inetAddress.toString() +
                    " " + command
                    + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
