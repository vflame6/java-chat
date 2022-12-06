package chat.Main.chatClient;

public class ClientHolder {
    private Client client;
    private final static ClientHolder INSTANCE = new ClientHolder();

    private ClientHolder() {}

    public static ClientHolder getInstance(){
        return INSTANCE;
    }

    public void setClient(Client c) {
        this.client = c;
    }

    public Client getClient() {
        return this.client;
    }
}

