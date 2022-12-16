package chat.Main.chatClient.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class SceneChanger {
    public static Parent changeScene(String fileName) {
        try {
            File file = new File("resources/" + fileName);
            URL url = new URL("file:/" + file.getAbsolutePath());
            return FXMLLoader.load(Objects.requireNonNull(url));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
