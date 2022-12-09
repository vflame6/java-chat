package chat.Main;

import chat.Main.chatClient.ClientApplication;

public class ClientMain extends ClientApplication {
    // To launch:
    // Import libraries
    // javafx-sdk-19/lib
    // sqlite-jdbc-3.40.0.0.jar
    // VM options:
    // --module-path "\path\to\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml
    public static void main(String[] args) {
        launchClient();
    }
}
