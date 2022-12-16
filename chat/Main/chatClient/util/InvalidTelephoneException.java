package chat.Main.chatClient.util;

public class InvalidTelephoneException extends RuntimeException {
    public InvalidTelephoneException(String errorMessage) {
        super(errorMessage);
    }
}
