package chat.Main.chatClient;

class IncorrectUsernameException extends RuntimeException {
    public IncorrectUsernameException(String _username) {
        super(_username + "is already used!");
    }
}
