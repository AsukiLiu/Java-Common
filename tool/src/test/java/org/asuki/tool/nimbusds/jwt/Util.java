package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Arrays;
import java.util.Date;

import static java.lang.System.out;
import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class Util {
    private Util() {
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
        keyGenerator.initialize(generateECParameterSpec());
//        keyGenerator.initialize(571);

        KeyPair keyPair = keyGenerator.generateKeyPair();

        return Pair.of((ECPublicKey) keyPair.getPublic(), (ECPrivateKey) keyPair.getPrivate());
    }

    private static ECParameterSpec generateECParameterSpec() {

        EllipticCurve curve = new EllipticCurve(new ECFieldFp(valueOf(5L)), ZERO, valueOf(4L));
        ECPoint generator = new ECPoint(ZERO, valueOf(2L));
        BigInteger order = valueOf(5L);
        int cofactor = 10;

        return new ECParameterSpec(curve, generator, order, cofactor);
    }

    public static Object[][] data() throws Exception {
        byte[] sharedSecret = Util.generateSharedSecret(32);
        Pair<RSAPublicKey, RSAPrivateKey> rsaKeyPair = Util.generateRsaKeyPair();
//        Pair<ECPublicKey, ECPrivateKey> ecKeyPair = Util.generateEcKeyPair();

        return new Object[][]{
                {JWSAlgorithm.HS256, new MACSigner(sharedSecret), new MACVerifier(sharedSecret)},
                {JWSAlgorithm.RS256, new RSASSASigner(rsaKeyPair.getRight()), new RSASSAVerifier(rsaKeyPair.getLeft())},
//                {JWSAlgorithm.ES256, new ECDSASigner(ecKeyPair.getRight()), new ECDSAVerifier(ecKeyPair.getLeft())},
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
}
