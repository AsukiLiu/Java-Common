package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.*;
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

import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;

public final class Util {
    private Util() {
    }

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
}
