import java.util.Base64;
import javax.crypto.Cipher;

public class AES {

    public static String encrypt(String plainText, Cipher cip) throws Exception {
        byte[] plainTextByte = plainText.getBytes();
        byte[] encryptedByte = cip.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }

    public static String decrypt(String encryptedText, Cipher cip) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        byte[] decryptedByte = cip.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }

}