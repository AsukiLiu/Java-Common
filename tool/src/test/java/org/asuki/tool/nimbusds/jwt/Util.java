package org.asuki.tool.nimbusds.jwt;

import org.apache.commons.lang3.tuple.Pair;

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

    public static byte[] generateSharedSecret() {
        // Generate random 256-bit (32-byte) shared secret
        byte[] sharedSecret = new byte[32];
        new SecureRandom().nextBytes(sharedSecret);

        return sharedSecret;
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

}
