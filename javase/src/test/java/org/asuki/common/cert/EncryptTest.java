package org.asuki.common.cert;

import org.testng.annotations.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.lang.System.out;

public class EncryptTest {
    // MD5(Message Digest Algorithm 5)
    private static final String KEY_MD5 = "MD5";
    // SHA(Secure Hash Algorithm)
    private static final String KEY_SHA = "SHA";
    // HMAC(Hash Message Authentication Code)
    private static final String KEY_MAC = "HmacMD5";

    @Test
    public void testEncrypt() throws Exception {
        final String inputStr = "test";

        out.println("encrypt by MD5: " + encryptByMd5(inputStr));
        out.println("encrypt by SHA: " + encryptBySha(inputStr));
        out.println("encrypt by HMAC: " + encryptByHmac(inputStr));
    }

    private static String encryptByMd5(String inputStr)
            throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance(KEY_MD5);
        md.update(inputStr.getBytes());

        BigInteger bigInteger = new BigInteger(md.digest());
        return bigInteger.toString(16);
    }

    private static String encryptBySha(String inputStr)
            throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance(KEY_SHA);
        md.update(inputStr.getBytes());

        BigInteger bigInteger = new BigInteger(md.digest());
        return bigInteger.toString(32);
    }

    private static String encryptByHmac(String inputStr) throws Exception {

        return encryptByHmac(inputStr.getBytes(), initMacKey());
    }

    @SuppressWarnings("restriction")
    private static String initMacKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);
        SecretKey secretKey = keyGenerator.generateKey();

        return (new BASE64Encoder()).encodeBuffer(secretKey.getEncoded());
    }

    @SuppressWarnings("restriction")
    private static String encryptByHmac(byte[] data, String key)
            throws Exception {

        SecretKey secretKey = new SecretKeySpec(
                (new BASE64Decoder()).decodeBuffer(key), KEY_MAC);

        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return new String(mac.doFinal(data));
    }
}
