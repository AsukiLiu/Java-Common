package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
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

public class JwtTest {

    private static final String SUBJECT = "andy";
    private static final String ISSUER = "https://example.org";

    @Test
    public void testWithHmacProtection() throws Exception {
        byte[] sharedSecret = Util.generateSharedSecret(32);

        String jwtString = produceSignedJwt(sharedSecret);
        out.println(jwtString);

        JWTClaimsSet jwtClaims = consumeSignedJwt(jwtString, sharedSecret);

        assertJWTClaimsSet(jwtClaims);
    }

    @Test
    public void testWithRsaSignature() throws Exception {
        Pair<RSAPublicKey, RSAPrivateKey> keyPair = Util.generateRsaKeyPair();

        String jwsString = produceSignedJws(keyPair.getRight());
        out.println(jwsString);

        JWTClaimsSet jwtClaims = consumeSignedJws(jwsString, keyPair.getLeft());

        assertJWTClaimsSet(jwtClaims);
    }

    //        @Test
    public void testWithEcSignature() throws Exception {
        Pair<ECPublicKey, ECPrivateKey> keyPair = Util.generateEcKeyPair();

        String jwsString = produceSignedJws(keyPair.getRight());
        out.println(jwsString);

        JWTClaimsSet jwtClaims = consumeSignedJws(jwsString, keyPair.getLeft());

        assertJWTClaimsSet(jwtClaims);
    }

    @Test
    public void testWithRsaEncryption() throws Exception {
        Pair<RSAPublicKey, RSAPrivateKey> keyPair = Util.generateRsaKeyPair();

        String jwtString = produceEncryptedJwt(keyPair.getLeft());
        out.println(jwtString);

        JWTClaimsSet jwtClaims = consumeEncryptedJwt(jwtString, keyPair.getRight());

        assertJWTClaimsSet(jwtClaims);
    }

    @Test
    public void testWithAesEncryption() throws Exception {
        SecretKey secretKey = Util.generateSecretKey();

        String jweString = produceEncryptedJwt(secretKey);
        out.println(jweString);

        JWTClaimsSet jwtClaims = consumeEncryptedJwt(jweString, secretKey);

        assertJWTClaimsSet(jwtClaims);
    }

    private static String produceSignedJwt(byte[] sharedSecret) throws JOSEException {

        JWSSigner signer = new MACSigner(sharedSecret);

        JWTClaimsSet jwtClaims = generateJWTClaimsSet();

        out.println(jwtClaims.toJSONObject());

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                jwtClaims);

        // Apply the HMAC protection
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private static JWTClaimsSet consumeSignedJwt(String jwtString, byte[] sharedSecret)
            throws ParseException, JOSEException {

        SignedJWT signedJWT = SignedJWT.parse(jwtString);

        JWSVerifier verifier = new MACVerifier(sharedSecret);

        assertThat(signedJWT.verify(verifier), is(true));

        return signedJWT.getJWTClaimsSet();
    }

    private static String produceSignedJws(RSAPrivateKey privateKey) throws JOSEException {
        JWSSigner signer = new RSASSASigner(privateKey);

        JWTClaimsSet jwtClaims = generateJWTClaimsSet();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.RS256),
                jwtClaims);

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private static JWTClaimsSet consumeSignedJws(String jwtString, RSAPublicKey publicKey)
            throws JOSEException, ParseException {

        SignedJWT signedJWT = SignedJWT.parse(jwtString);

        JWSVerifier verifier = new RSASSAVerifier(publicKey);

        assertThat(signedJWT.verify(verifier), is(true));

        return signedJWT.getJWTClaimsSet();
    }

    private static String produceSignedJws(ECPrivateKey privateKey) throws JOSEException {
        JWSSigner signer = new ECDSASigner(privateKey);

        JWTClaimsSet jwtClaims = generateJWTClaimsSet();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.ES256),
                jwtClaims);

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private static JWTClaimsSet consumeSignedJws(String jwtString, ECPublicKey publicKey) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(jwtString);

        JWSVerifier verifier = new ECDSAVerifier(publicKey);

        assertThat(signedJWT.verify(verifier), is(true));

        return signedJWT.getJWTClaimsSet();
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

    private static JWTClaimsSet consumeEncryptedJwt(String jwtString, RSAPrivateKey privateKey)
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

    private static JWTClaimsSet generateJWTClaimsSet() {
        return new Builder()
                .subject(SUBJECT)
                .issuer(ISSUER)
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();
    }

    private static String produceEncryptedJwt(SecretKey secretKey) throws JOSEException {
        JWSSigner signer = new MACSigner(secretKey.getEncoded());

        JWTClaimsSet claimsSet = generateJWTClaimsSet();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        signedJWT.sign(signer);

        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                        .contentType("JWT") // signal nested JWT
                        .build(),
                new Payload(signedJWT));

        jweObject.encrypt(new DirectEncrypter(secretKey.getEncoded()));

        return jweObject.serialize();
    }

    private static JWTClaimsSet consumeEncryptedJwt(String jweString, SecretKey secretKey) throws JOSEException, ParseException {
        JWEObject jweObject = JWEObject.parse(jweString);

        jweObject.decrypt(new DirectDecrypter(secretKey.getEncoded()));

        SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();

        assertThat(signedJWT.verify(new MACVerifier(secretKey.getEncoded())), is(true));

        return signedJWT.getJWTClaimsSet();
    }

}
