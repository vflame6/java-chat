package chat.Main.chatClient;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends javafx.application.Application {
    public void start(Stage primaryStage) throws IOException {
        Parent root = SceneChanger.changeScene("ChatConnect.fxml");
        Scene scene = new Scene(root);
        primaryStage.setTitle("KiChatApp");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void launchClient() {
        launch();
    }
}
