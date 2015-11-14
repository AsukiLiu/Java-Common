package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
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

    @Test(dataProvider = "data")
    public void testWithSignature(
            JWSAlgorithm algorithm, JWSSigner signer, JWSVerifier verifier) throws Exception {

        String jwtString = produceSignedJwt(algorithm, signer);

        JWTClaimsSet jwtClaims = consumeSignedJwt(jwtString, verifier);

        assertJWTClaimsSet(jwtClaims);
    }

    @DataProvider
    public Object[][] data() throws Exception {
        return Util.data();
    }

    @Test
    public void testWithRsaEncryption() throws Exception {
        Pair<RSAPublicKey, RSAPrivateKey> keyPair = Util.generateRsaKeyPair();

        String jwtString = produceEncryptedJwt(keyPair.getLeft());

        JWTClaimsSet jwtClaims = consumeEncryptedJwt(jwtString, keyPair.getRight());

        assertJWTClaimsSet(jwtClaims);
    }

    @Test
    public void testWithAesEncryption() throws Exception {
        SecretKey secretKey = Util.generateSecretKey();

        String jweString = produceEncryptedJwt(secretKey);

        JWTClaimsSet jwtClaims = consumeEncryptedJwt(jweString, secretKey);

        assertJWTClaimsSet(jwtClaims);
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
        JWTClaimsSet jwtClaims = new Builder()
                .subject(SUBJECT)
                .issuer(ISSUER)
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();

        out.println(jwtClaims.toJSONObject());

        return jwtClaims;
    }

    private static String produceEncryptedJwt(SecretKey secretKey) throws JOSEException {

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

    private static JWTClaimsSet consumeEncryptedJwt(String jweString, SecretKey secretKey) throws JOSEException, ParseException {
        JWEObject jweObject = JWEObject.parse(jweString);

        jweObject.decrypt(new DirectDecrypter(secretKey.getEncoded()));

        SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();

        assertThat(signedJWT.verify(new MACVerifier(secretKey.getEncoded())), is(true));

        return signedJWT.getJWTClaimsSet();
    }

    private static String produceSignedJwt(
            JWSAlgorithm algorithm, JWSSigner signer) throws JOSEException {

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(algorithm),
                generateJWTClaimsSet());

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private static JWTClaimsSet consumeSignedJwt(String jwtString, JWSVerifier verifier)
            throws ParseException, JOSEException {

        SignedJWT signedJWT = SignedJWT.parse(jwtString);

        assertThat(signedJWT.verify(verifier), is(true));

        return signedJWT.getJWTClaimsSet();
    }
}
