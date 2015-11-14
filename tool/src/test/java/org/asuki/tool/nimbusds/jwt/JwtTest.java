package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;

import static org.asuki.tool.nimbusds.jwt.Util.assertJWTClaimsSet;
import static org.asuki.tool.nimbusds.jwt.Util.generateJWTClaimsSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JwtTest {

    @Test(dataProvider = "data")
    public void testWithSignature(
            JWSAlgorithm algorithm, JWSSigner signer, JWSVerifier verifier) throws Exception {

        String jwtString = produceSignedJwt(algorithm, signer);

        JWTClaimsSet jwtClaims = consumeSignedJwt(jwtString, verifier);

        assertJWTClaimsSet(jwtClaims);
    }

    @DataProvider
    private Object[][] data() throws Exception {
        return Util.data();
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
