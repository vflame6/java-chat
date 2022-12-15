package chat.Main.chatServer.util;

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

    public static void logMessage(String from, String content) {
        try (FileWriter fileWriter = new FileWriter(messageLogFile, true)) {
            LocalDateTime now = LocalDateTime.now();
            fileWriter.write(now.format(format) +
                    " " + from +
                    " " + content +
                    "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logAccess(InetAddress inetAddress, String fullCommand) {
        try (FileWriter fileWriter = new FileWriter(accessLogFile, true)) {
            LocalDateTime now = LocalDateTime.now();
            String command = fullCommand.split(";")[0];
            fileWriter.write(now.format(format) +
                    " " + inetAddress.toString() +
                    " " + command +
                    "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
