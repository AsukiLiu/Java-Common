package org.asuki.common.serialize;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.Getter;

import com.google.common.base.Charsets;

public class ProtectedSecret implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    private final String secret;

    public ProtectedSecret(String secret) {
        this.secret = secret;
    }

    private Object writeReplace() {
        return new ProtectedSecretProxy(this);
    }

    private void writeObject(ObjectOutputStream stream)
            throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    private void readObject(ObjectInputStream stream)
            throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    private static class ProtectedSecretProxy implements Serializable {
        private static final long serialVersionUID = 1L;

        private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
        private static final String HMAC_ALGORITHM = "HmacSHA256";

        private static transient SecretKeySpec cipherKey;
        private static transient SecretKeySpec hmacKey;

        private String secret;

        static {
            // dummy
            final byte[] aes_key = "1234567890abcdef".getBytes();
            final byte[] hmac_key = "abcd123".getBytes();

            cipherKey = new SecretKeySpec(aes_key, "AES");
            hmacKey = new SecretKeySpec(hmac_key, HMAC_ALGORITHM);
        }

        ProtectedSecretProxy(ProtectedSecret protectedSecret) {
            this.secret = protectedSecret.secret;
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {

            oos.defaultWriteObject();

            try {
                encrypt(oos);
            } catch (Exception e) {
                throw new InvalidObjectException("unable to encrypt value:"
                        + e.getMessage());
            }
        }

        private void readObject(ObjectInputStream ois)
                throws ClassNotFoundException, IOException,
                InvalidObjectException {

            ois.defaultReadObject();

            try {
                decrypt(ois);
            } catch (Exception e) {
                throw new InvalidObjectException("unable to decrypt value:"
                        + e.getMessage());
            }
        }

        private Object readResolve() {
            return new ProtectedSecret(secret);
        }

        private void encrypt(ObjectOutputStream oos) throws Exception {
            Cipher encrypt = Cipher.getInstance(CIPHER_ALGORITHM);
            encrypt.init(Cipher.ENCRYPT_MODE, cipherKey);
            byte[] ciphertext = encrypt
                    .doFinal(secret.getBytes(Charsets.UTF_8));
            byte[] iv = encrypt.getIV();

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(hmacKey);
            mac.update(iv);
            byte[] hmac = mac.doFinal(ciphertext);

            oos.writeInt(iv.length);
            oos.write(iv);
            oos.writeInt(ciphertext.length);
            oos.write(ciphertext);
            oos.writeInt(hmac.length);
            oos.write(hmac);
        }

        private void decrypt(ObjectInputStream ois) throws Exception {
            byte[] iv = new byte[ois.readInt()];
            ois.read(iv);
            byte[] ciphertext = new byte[ois.readInt()];
            ois.read(ciphertext);
            byte[] hmac = new byte[ois.readInt()];
            ois.read(hmac);

            // verify HMAC
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(hmacKey);
            mac.update(iv);
            byte[] signature = mac.doFinal(ciphertext);
            if (!Arrays.equals(hmac, signature)) {
                throw new InvalidObjectException("failed to verify HMAC");
            }

            Cipher decrypt = Cipher.getInstance(CIPHER_ALGORITHM);
            decrypt.init(Cipher.DECRYPT_MODE, cipherKey,
                    new IvParameterSpec(iv));
            byte[] data = decrypt.doFinal(ciphertext);
            secret = new String(data, Charsets.UTF_8);
        }
    }
}
