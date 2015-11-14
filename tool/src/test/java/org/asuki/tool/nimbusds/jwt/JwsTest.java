package org.asuki.tool.nimbusds.jwt;

import com.nimbusds.jose.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JwsTest {

    private static final String PAYLOAD = "Hello";

    @Test(dataProvider = "data")
    public void testWithSignature(
            JWSAlgorithm algorithm, JWSSigner signer, JWSVerifier verifier) throws Exception {

        String jwsString = produceSignedJws(algorithm, signer);

        JWSObject jwsObject = consumeSignedJws(jwsString, verifier);

        assertThat(jwsObject.getPayload().toString(), is(PAYLOAD));
    }

    @DataProvider
    public Object[][] data() throws Exception {
        return Util.data();
    }

    private static String produceSignedJws(
            JWSAlgorithm algorithm, JWSSigner signer) throws JOSEException {

        JWSObject jwsObject = new JWSObject(
                new JWSHeader(algorithm),
                new Payload(PAYLOAD));

        jwsObject.sign(signer);

        return jwsObject.serialize();
    }

    private static JWSObject consumeSignedJws(String jwsString, JWSVerifier verifier)
            throws ParseException, JOSEException {

        JWSObject jwsObject = JWSObject.parse(jwsString);

        assertThat(jwsObject.verify(verifier), is(true));

        return jwsObject;
    }
}
