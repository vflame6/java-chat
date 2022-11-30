package chatServer;

import javax.crypto.Cipher;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Encryptor {
    private static final String publicKey = "encryption/public.key";
    private static final String privateKey = "encryption/private.key";
    private PublicKey pub;
    private PrivateKey pvt;

    public Encryptor() {
        pub = loadPublicKey();
        pvt = loadPrivateKey();
    }

    public static void generateKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            PublicKey pub = kp.getPublic();
            PrivateKey pvt = kp.getPrivate();

            FileOutputStream publicKeyFile = new FileOutputStream(publicKey);
            publicKeyFile.write(pub.getEncoded());
            publicKeyFile.close();

            FileOutputStream privateKeyFile = new FileOutputStream(privateKey);
            privateKeyFile.write(pvt.getEncoded());
            privateKeyFile.close();

            System.err.println("Private key format: " + pvt.getFormat());
            System.err.println("Public key format: " + pub.getFormat());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static PrivateKey loadPrivateKey() {
        try {
            Path path = Paths.get(privateKey);
            byte[] bytes = Files.readAllBytes(path);

            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(ks);
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PublicKey loadPublicKey() {
        try {
            Path path = Paths.get(publicKey);
            byte[] bytes = Files.readAllBytes(path);

            X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(ks);
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String encryptMessage(String msg)
    {
        try {
            byte[] msgBytes = msg.getBytes();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pub);
            byte[] encrypted = cipher.doFinal(msgBytes);
            return  encodeData(encrypted);
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }
    private String encodeData(byte[] data)
    {
        return Base64.getEncoder().encodeToString(data);
    }
    public String decodeMessage(String encMsg)
    {
        try {
            byte[] encMsgBytes = decodeData(encMsg);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pvt);
            byte[] decryptMsg = cipher.doFinal(encMsgBytes);

            return new String(decryptMsg, "UTF-8");
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }
    private byte[] decodeData(String data)
    {
        return Base64.getDecoder().decode(data);
    }
}
