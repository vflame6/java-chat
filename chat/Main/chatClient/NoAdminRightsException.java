package chat.Main.chatClient;

public class NoAdminRightsException extends RuntimeException {
    public NoAdminRightsException(String message) {
        super(message);
    }
}
