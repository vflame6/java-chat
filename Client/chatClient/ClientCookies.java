package chatClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ClientCookies implements Cookies {
    public String getCookie(){
            try {
                FileReader cookieFile = new FileReader("cookie.txt");
                char[] buffer = new char[8096];
                int chars = cookieFile.read(buffer);
                String cookie = "";
                while (chars != -1) {
                    cookie += (String.valueOf(buffer, 0, chars));
                    chars = cookieFile.read(buffer);
                }

                cookieFile.close();
                return cookie;
        } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
    }

    public void createCookie(String cookie) {
        File cookieFile = new File("cookie.txt");
        FileWriter cookieWriter = null;
        try {
            cookieWriter = new FileWriter("cookie.txt");
            cookieWriter.write(cookie);
            cookieWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
