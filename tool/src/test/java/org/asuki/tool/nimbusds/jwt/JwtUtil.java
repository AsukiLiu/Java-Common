package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import static java.lang.System.out;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class JwtUtil {
    private JwtUtil() {
    }

    private static final String SUBJECT = "andy";
    private static final String ISSUER = "https://example.org";

    // 256-bit (32-byte), 128-bit (16-byte)
    public static byte[] generateSharedSecret(int bytes) {
        byte[] sharedSecret = new byte[bytes];
        new SecureRandom().nextBytes(sharedSecret);

        return sharedSecret;
    }

    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public static Pair<RSAPublicKey, RSAPrivateKey> generateRsaKeyPair() throws NoSuchAlgorithmException {

        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(1024);

        KeyPair keyPair = keyGenerator.genKeyPair();

        return Pair.of((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
    }

    public static Pair<ECPublicKey, ECPrivateKey> generateEcKeyPair()
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("EC");
        keyGenerator.initialize(256);

        KeyPair keyPair = keyGenerator.generateKeyPair();

        return Pair.of((ECPublicKey) keyPair.getPublic(), (ECPrivateKey) keyPair.getPrivate());
    }

    public static Object[][] data() throws Exception {
        byte[] sharedSecret = JwtUtil.generateSharedSecret(32);
        Pair<RSAPublicKey, RSAPrivateKey> rsaKeyPair = JwtUtil.generateRsaKeyPair();
        Pair<ECPublicKey, ECPrivateKey> ecKeyPair = JwtUtil.generateEcKeyPair();

        return new Object[][]{
                {JWSAlgorithm.HS256, new MACSigner(sharedSecret), new MACVerifier(sharedSecret)},
                {JWSAlgorithm.RS256, new RSASSASigner(rsaKeyPair.getRight()), new RSASSAVerifier(rsaKeyPair.getLeft())},
                {JWSAlgorithm.ES256, new ECDSASigner(ecKeyPair.getRight()), new ECDSAVerifier(ecKeyPair.getLeft())},
        };
    }

    public static void assertJWTClaimsSet(JWTClaimsSet jwtClaims) {
        assertThat(jwtClaims.getSubject(), is(SUBJECT));
        assertThat(jwtClaims.getIssuer(), is(ISSUER));
        assertThat(new Date().before(jwtClaims.getExpirationTime()), is(true));
    }

    public static JWTClaimsSet generateJWTClaimsSet() {
        JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
                .subject(SUBJECT)
                .issuer(ISSUER)
                .audience(Arrays.<String>asList("https://app-one.com", "https://app-two.com"))
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .notBeforeTime(new Date())
                .issueTime(new Date())
                .jwtID(randomUUID().toString())
                .build();

        out.println(jwtClaims.toJSONObject());

        return jwtClaims;
    }

    public static Class<?> parseJOSEObject(String input) throws ParseException {
        JOSEObject joseObject = JOSEObject.parse(input);

        if (joseObject instanceof PlainObject) {
            return PlainObject.class;
        } else if (joseObject instanceof JWSObject) {
            return JWSObject.class;
        } else if (joseObject instanceof JWEObject) {
            return JWEObject.class;
        }

        return null;
    }

    public static Class<?> parseJWT(String input) throws ParseException {
        JWT jwt = JWTParser.parse(input);

        if (jwt instanceof PlainJWT) {
            return PlainJWT.class;
        } else if (jwt instanceof SignedJWT) {
            return SignedJWT.class;
        } else if (jwt instanceof EncryptedJWT) {
            return EncryptedJWT.class;
        }

        return null;
    }
}
