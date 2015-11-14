package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import org.testng.annotations.Test;

import java.text.ParseException;

import static java.lang.System.out;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JweTest {

    private static final String PAYLOAD = "Hello";

    @Test
    public void testWithSharedKeyEncryption() throws Exception {
        byte[] sharedSecret = Util.generateSharedSecret(16);

        String jweString = produceEncryptedJwe(sharedSecret);
        out.println(jweString);

        JWEObject jweObject = consumeEncryptedJwe(jweString, sharedSecret);

        assertThat(jweObject.getPayload().toString(), is(PAYLOAD));
    }

    private static String produceEncryptedJwe(byte[] sharedSecret) throws JOSEException {
        JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128GCM);

        Payload payload = new Payload(PAYLOAD);

        JWEObject jweObject = new JWEObject(header, payload);
        jweObject.encrypt(new DirectEncrypter(sharedSecret));

        return jweObject.serialize();
    }

    private static JWEObject consumeEncryptedJwe(String jweString, byte[] sharedSecret) throws JOSEException, ParseException {
        JWEObject jweObject = JWEObject.parse(jweString);

        jweObject.decrypt(new DirectDecrypter(sharedSecret));

        return jweObject;
    }

}
