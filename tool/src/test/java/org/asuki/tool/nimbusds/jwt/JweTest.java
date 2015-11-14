package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import org.testng.annotations.Test;

import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JweTest {

    private static final String PAYLOAD = "Hello";

    @Test
    public void testWithSharedKeyEncryption() throws Exception {
        byte[] sharedSecret = Util.generateSharedSecret(16);

        String jweString = produceEncryptedJwe(new DirectEncrypter(sharedSecret));

        JWEObject jweObject = consumeEncryptedJwe(jweString, new DirectDecrypter(sharedSecret));

        assertThat(jweObject.getPayload().toString(), is(PAYLOAD));
    }

    private static String produceEncryptedJwe(JWEEncrypter encrypter) throws JOSEException {
        JWEObject jweObject = new JWEObject(
                new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128GCM),
                new Payload(PAYLOAD));

        jweObject.encrypt(encrypter);

        return jweObject.serialize();
    }

    private static JWEObject consumeEncryptedJwe(String jweString, JWEDecrypter decrypter)
            throws JOSEException, ParseException {

        JWEObject jweObject = JWEObject.parse(jweString);

        jweObject.decrypt(decrypter);

        return jweObject;
    }
}
