package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

import static java.lang.System.out;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JwsTest {

    private static final String PAYLOAD = "Hello";

    @Test
    public void testWithHmacProtection() throws Exception {
        byte[] sharedSecret = Util.generateSharedSecret(32);

        String jwsString = produceSignedJws(sharedSecret);
        out.println(jwsString);

        JWSObject jwsObject = consumeSignedJws(jwsString, sharedSecret);

        assertThat(jwsObject.getPayload().toString(), is(PAYLOAD));
    }

    @Test
    public void testWithRsaSignature() throws Exception {
        Pair<RSAPublicKey, RSAPrivateKey> keyPair = Util.generateRsaKeyPair();

        String jwsString = produceSignedJws(keyPair.getRight());
        out.println(jwsString);

        JWSObject jwsObject = consumeSignedJws(jwsString, keyPair.getLeft());

        assertThat(jwsObject.getPayload().toString(), is(PAYLOAD));
    }

//    @Test
    public void testWithEcSignature() throws Exception {
        Pair<ECPublicKey, ECPrivateKey> keyPair = Util.generateEcKeyPair();

        String jwsString = produceSignedJws(keyPair.getRight());
        out.println(jwsString);

        JWSObject jwsObject = consumeSignedJws(jwsString, keyPair.getLeft());

        assertThat(jwsObject.getPayload().toString(), is(PAYLOAD));
    }

    private static String produceSignedJws(byte[] sharedSecret) throws JOSEException {
        JWSSigner signer = new MACSigner(sharedSecret);

        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(PAYLOAD));

        // Apply the HMAC protection
        jwsObject.sign(signer);

        return jwsObject.serialize();
    }

    private static JWSObject consumeSignedJws(String jwsString, byte[] sharedSecret)
            throws JOSEException, ParseException {

        JWSObject jwsObject = JWSObject.parse(jwsString);

        JWSVerifier verifier = new MACVerifier(sharedSecret);

        assertThat(jwsObject.verify(verifier), is(true));

        return jwsObject;
    }

    private static String produceSignedJws(RSAPrivateKey privateKey) throws JOSEException {
        JWSSigner signer = new RSASSASigner(privateKey);

        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.RS256), new Payload(PAYLOAD));

        jwsObject.sign(signer);

        return jwsObject.serialize();
    }

    private static JWSObject consumeSignedJws(String jwsString, RSAPublicKey publicKey)
            throws JOSEException, ParseException {

        JWSObject jwsObject = JWSObject.parse(jwsString);

        JWSVerifier verifier = new RSASSAVerifier(publicKey);

        assertThat(jwsObject.verify(verifier), is(true));

        return jwsObject;
    }

    private static String produceSignedJws(ECPrivateKey privateKey) throws JOSEException {
        JWSSigner signer = new ECDSASigner(privateKey);

        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.ES256), new Payload(PAYLOAD));

        jwsObject.sign(signer);

        return jwsObject.serialize();
    }

    private static JWSObject consumeSignedJws(String jwsString, ECPublicKey publicKey)
            throws ParseException, JOSEException {

        JWSObject jwsObject = JWSObject.parse(jwsString);

        JWSVerifier verifier = new ECDSAVerifier(publicKey);

        assertThat(jwsObject.verify(verifier), is(true));

        return jwsObject;
    }

}
