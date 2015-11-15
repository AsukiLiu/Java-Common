package org.asuki.tool.bouncycastle;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CryptTest {

    private static final String PLAIN_TEXT = "hello";

    private static final String PUBLIC_KEY_PATH = "pem/public_key.pem";
    private static final String PRIVATE_KEY_PATH = "pem/private_key.pem";

    private static PKCS1Encoding rsa;

    static {
        Security.addProvider(new BouncyCastleProvider());

        rsa = new PKCS1Encoding(new RSAEngine());
    }

    @Test
    public void test() throws Exception {

        byte[] encrypted = encrypt(PLAIN_TEXT);

        String decrypted = decrypt(encrypted);

        assertThat(decrypted, is(PLAIN_TEXT));
    }

    private static byte[] encrypt(String target) throws IOException, InvalidCipherTextException {

        // "true" means encryption
        rsa.init(true, publicKeyParam());

        byte[] bytes = target.getBytes(Charsets.UTF_8);
        return rsa.processBlock(bytes, 0, bytes.length);
    }

    private static AsymmetricKeyParameter publicKeyParam() throws IOException {
        Reader publicKeyPem = new StringReader(loadPem(PUBLIC_KEY_PATH));

        PEMReader publicKeyReader = new PEMReader(publicKeyPem);

        RSAPublicKey publicKey = (RSAPublicKey) publicKeyReader.readObject();

        return new RSAKeyParameters(
                false,
                publicKey.getModulus(),
                publicKey.getPublicExponent()
        );
    }

    private static String decrypt(byte[] target) throws IOException, InvalidCipherTextException {

        // "false" means decryption
        rsa.init(false, privateKeyParam());

        byte[] decrypted = rsa.processBlock(target, 0, target.length);
        return new String(decrypted, Charsets.UTF_8);
    }

    private static AsymmetricKeyParameter privateKeyParam() throws IOException {
        Reader privateKeyPem = new StringReader(loadPem(PRIVATE_KEY_PATH));

        PEMReader privateKeyReader = new PEMReader(privateKeyPem);

        RSAPrivateKey privateKey = (RSAPrivateKey) ((KeyPair) privateKeyReader.readObject()).getPrivate();

        return new RSAKeyParameters(
                false,
                privateKey.getModulus(),
                privateKey.getPrivateExponent()
        );
    }

    private static String loadPem(String name) throws IOException {
        return Resources.toString(Resources.getResource(name), Charsets.UTF_8);
    }
}
