package chat.Main.chatClient.util;

import chat.Main.chatClient.ClientFunctional;

public class ClientHolder {
    private ClientFunctional clientFunctional;
    private final static ClientHolder INSTANCE = new ClientHolder();

    private ClientHolder() {
    }

    public static ClientHolder getInstance() {
        return INSTANCE;
    }

    public void setClient(ClientFunctional c) {
        this.clientFunctional = c;
    }

    public ClientFunctional getClient() {
        return this.clientFunctional;
    }
}

