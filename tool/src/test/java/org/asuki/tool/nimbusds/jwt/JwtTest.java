package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import static java.lang.System.out;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JwtTest {

    private static final String SUBJECT = "andy";
    private static final String ISSUER = "https://example.org";

    @Test
    public void testWithHmacProtection() throws Exception {
        byte[] sharedSecret = generateSharedSecret();

        String jwtString = produceSignedJwt(sharedSecret);
        out.println(jwtString);

        JWTClaimsSet jwtClaims = consumeSignedJwt(jwtString, sharedSecret);

        assertJWTClaimsSet(jwtClaims);
    }

    @Test
    public void testWithRsaEncryption() throws Exception {
        Pair<RSAPublicKey, RSAPrivateKey> keyPair = generateKeyPair();

        String jwtString = produceEncryptedJwt(keyPair.getLeft());
        out.println(jwtString);

        JWTClaimsSet jwtClaims = consumeEncryptedJwt(jwtString, keyPair.getRight());

        assertJWTClaimsSet(jwtClaims);
    }

    private static byte[] generateSharedSecret() {
        // Generate random 256-bit (32-byte) shared secret
        SecureRandom random = new SecureRandom();
        byte[] sharedSecret = new byte[32];
        random.nextBytes(sharedSecret);

        return sharedSecret;
    }

    private static String produceSignedJwt(byte[] sharedSecret) throws JOSEException {

        JWSSigner signer = new MACSigner(sharedSecret);

        JWTClaimsSet jwtClaims = new Builder()
                .subject(SUBJECT)
                .issuer(ISSUER)
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();

        out.println(jwtClaims.toJSONObject());

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaims);

        // Apply the HMAC protection
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private JWTClaimsSet consumeSignedJwt(String jwtString, byte[] sharedSecret)
            throws ParseException, JOSEException {

        SignedJWT signedJWT = SignedJWT.parse(jwtString);

        JWSVerifier verifier = new MACVerifier(sharedSecret);

        assertThat(signedJWT.verify(verifier), is(true));

        return signedJWT.getJWTClaimsSet();
    }

    private static Pair<RSAPublicKey, RSAPrivateKey> generateKeyPair() throws NoSuchAlgorithmException {

        // Public key must be made known to the JWS recipient in order to verify the signatures
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(1024);

        KeyPair kp = keyGenerator.genKeyPair();

        return Pair.of((RSAPublicKey) kp.getPublic(), (RSAPrivateKey) kp.getPrivate());
    }

    private static String produceEncryptedJwt(RSAPublicKey publicKey) throws JOSEException {

        JWTClaimsSet jwtClaims = new Builder()
                .subject(SUBJECT)
                .issuer(ISSUER)
                .audience(Arrays.<String>asList("https://app-one.com", "https://app-two.com"))
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .notBeforeTime(new Date())
                .issueTime(new Date())
                .jwtID(randomUUID().toString())
                .build();

        out.println(jwtClaims.toJSONObject());

        // Request JWT encrypted with RSA-OAEP and 128-bit AES/GCM
        JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM);

        EncryptedJWT encryptedJWT = new EncryptedJWT(header, jwtClaims);

        RSAEncrypter encrypter = new RSAEncrypter(publicKey);

        encryptedJWT.encrypt(encrypter);

        return encryptedJWT.serialize();
    }

    private JWTClaimsSet consumeEncryptedJwt(String jwtString, RSAPrivateKey privateKey)
            throws ParseException, JOSEException {

        EncryptedJWT encryptedJWT = EncryptedJWT.parse(jwtString);

        RSADecrypter decrypter = new RSADecrypter(privateKey);

        encryptedJWT.decrypt(decrypter);

        return encryptedJWT.getJWTClaimsSet();
    }

    private static void assertJWTClaimsSet(JWTClaimsSet jwtClaims) {
        assertThat(jwtClaims.getSubject(), is(SUBJECT));
        assertThat(jwtClaims.getIssuer(), is(ISSUER));
        assertThat(new Date().before(jwtClaims.getExpirationTime()), is(true));
    }
}
