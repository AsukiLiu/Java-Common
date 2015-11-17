package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

import static org.asuki.tool.nimbusds.jwt.JwtUtil.assertJWTClaimsSet;
import static org.asuki.tool.nimbusds.jwt.JwtUtil.generateJWTClaimsSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JweTest {

    private static final String PAYLOAD = "Hello";

    @Test
    public void testWithSharedKeyEncryption() throws Exception {
        byte[] sharedSecret = JwtUtil.generateSharedSecret(16);

        String jweString = produceEncryptedJwe(new DirectEncrypter(sharedSecret));

        JWEObject jweObject = consumeEncryptedJwe(jweString, new DirectDecrypter(sharedSecret));

        assertThat(jweObject.getPayload().toString(), is(PAYLOAD));
    }

    @Test
    public void testWithRsaEncryption() throws Exception {
        Pair<RSAPublicKey, RSAPrivateKey> keyPair = JwtUtil.generateRsaKeyPair();

        String jwtString = produceEncryptedJwt(keyPair.getLeft());

        JWTClaimsSet jwtClaims = consumeEncryptedJwt(jwtString, keyPair.getRight());

        assertJWTClaimsSet(jwtClaims);
    }

    @Test
    public void testWithAesEncryption() throws Exception {
        SecretKey secretKey = JwtUtil.generateSecretKey();

        String jweString = produceEncryptedJwe(secretKey);

        JWTClaimsSet jwtClaims = consumeEncryptedJwe(jweString, secretKey);

        assertJWTClaimsSet(jwtClaims);
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

    private static String produceEncryptedJwt(RSAPublicKey publicKey) throws JOSEException {

        EncryptedJWT encryptedJWT = new EncryptedJWT(
                new JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM),
                generateJWTClaimsSet());

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

    private static String produceEncryptedJwe(SecretKey secretKey) throws JOSEException {

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                generateJWTClaimsSet());

        JWSSigner signer = new MACSigner(secretKey.getEncoded());
        signedJWT.sign(signer);

        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                        .contentType("JWT") // signal nested JWT
                        .build(),
                new Payload(signedJWT));

        JWEEncrypter encrypter = new DirectEncrypter(secretKey.getEncoded());
        jweObject.encrypt(encrypter);

        return jweObject.serialize();
    }

    private static JWTClaimsSet consumeEncryptedJwe(String jweString, SecretKey secretKey) throws JOSEException, ParseException {
        JWEObject jweObject = JWEObject.parse(jweString);

        jweObject.decrypt(new DirectDecrypter(secretKey.getEncoded()));

        SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();

        assertThat(signedJWT.verify(new MACVerifier(secretKey.getEncoded())), is(true));

        return signedJWT.getJWTClaimsSet();
    }
}
