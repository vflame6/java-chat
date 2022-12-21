package chat.Main.chatClient.util;

import chat.Main.Cookies;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ClientCookies implements Cookies {

    // Проверка есть ли куки-файл
    public boolean isCookieExists() {
        File file = new File("cookie.txt");
        return file.exists() && !file.isDirectory();
    }

    // Прочитать куки из файла и вернуть строку содержащую куку
    public String getCookie() {
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

    // Создать куки-файл
    public void createCookie(String cookie) {
        FileWriter cookieWriter;
        try {
            cookieWriter = new FileWriter("cookie.txt");
            cookieWriter.write(cookie);
            cookieWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Удалить куки-файл
    public void deleteCookie() {
        File cookieFile = new File("cookie.txt");
        cookieFile.delete();
    }
}
