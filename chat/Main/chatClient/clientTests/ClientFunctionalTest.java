package chat.Main.chatClient.clientTests;

import chat.Main.chatClient.util.InvalidTelephoneException;
import chat.Main.chatClient.util.AuthenticationRequiredException;
import chat.Main.chatClient.ClientFunctional;
import chat.Main.chatClient.util.NoAdminRightsException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class ClientFunctionalTest {
    ClientFunctional test;
    {
        try {
            test = new ClientFunctional("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void pingTest() {
        assertTrue(test.ping());
    }

    @Test
    void getMessagesTest() {
        assertThrows(AuthenticationRequiredException.class,()-> test.getMessages());
    }
    @Test
    void loginTest() {
        assertFalse(test.login("testsuser","testspassword"));
    }
    @Test
    void phoneNumberTest() {
        assertThrows(InvalidTelephoneException.class,()-> test.register("testuser","testpassword","+79"));
    }
    @Test
    void privateKeyExistsTest(){
        File keys=new File("encryption/private.key");
        assertTrue(keys.exists());
    }
    @Test
    void deleteMessageTestWithoutLogin() {
        assertThrows(AuthenticationRequiredException.class,()-> test.deleteMessage(1));
    }
    @Test
    void deleteMessageTestWithLogin() {
        assertThrows(NoAdminRightsException.class,()->{
            test.login("testuser","testpassword");
            test.deleteMessage(1);
        });
    }
}


