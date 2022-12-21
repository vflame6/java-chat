package chat.Main.chatClient.clientTests;

import chat.Main.chatClient.ClientFunctional;
import chat.Main.chatClient.util.AuthenticationRequiredException;
import chat.Main.chatClient.util.InvalidTelephoneException;
import chat.Main.chatClient.util.NoAdminRightsException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class ClientFunctionalTest {
    // Создание объекта клиента test с подключением к серверу
    ClientFunctional test;

    {
        try {
            test = new ClientFunctional("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    // Тестирование метода ping
    @Test
    void pingTest() {
        assertTrue(test.ping());
    }

    // Тестирование метода getMessages
    @Test
    void getMessagesTest() {
        assertThrows(AuthenticationRequiredException.class, () -> test.getMessages());
    }

    // Тестирование метода login
    @Test
    void loginTest() {
        assertFalse(test.login("testsuser", "testspassword"));
    }

    // Тестирование исключения InvalidTelephoneException
    @Test
    void phoneNumberTest() {
        assertThrows(InvalidTelephoneException.class, () -> test.register("testuser", "testpassword", "+79"));
    }

    // Тестирование на наличие приватного ключа в папке с проектом
    @Test
    void privateKeyExistsTest() {
        File keys = new File("encryption/private.key");
        assertTrue(keys.exists());
    }

    // Тестирование исключения AuthenticationRequiredException при удалении сообщения
    @Test
    void deleteMessageTestWithoutLogin() {
        assertThrows(AuthenticationRequiredException.class, () -> test.deleteMessage(1));
    }

    // Тестирование исключения NoAdminRightsException при удалении сообщения
    @Test
    void deleteMessageTestWithLogin() {
        assertThrows(NoAdminRightsException.class, () -> {
            test.login("testuser", "testpassword");
            test.deleteMessage(9);
        });
    }
}


