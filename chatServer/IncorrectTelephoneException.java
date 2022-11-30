package chatServer;

public class IncorrectTelephoneException extends RuntimeException {
    public IncorrectTelephoneException(String errorMessage) {
        super(errorMessage);
    }
}
