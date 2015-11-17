package org.asuki.tool.nimbusds.auth;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.nimbusds.oauth2.sdk.ResponseType.Value.CODE;
import static java.lang.System.err;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class OpenIdConnectAuthenticationTest {

    // NOTE: Run "Spring-Boot" repo in advance
//    @Test
    public void test() throws Exception {
        requestAuthentication();
    }

    private static void requestAuthentication()
            throws IOException, SerializeException, ParseException, URISyntaxException {

        State state = new State();

        AuthenticationRequest request = new AuthenticationRequest(
                new URI("http://localhost:8082/api/login"),
                new ResponseType(CODE),
                Scope.parse("openid email profile"),
                new ClientID("123abcd"),
                new URI("https://client.com/callback"),
                state,
                new Nonce());

        HTTPResponse httpResponse = request.toHTTPRequest().send();

        AuthenticationResponse response = AuthenticationResponseParser.parse(httpResponse);

        if (response instanceof AuthenticationErrorResponse) {
            AuthenticationErrorResponse errorResponse = (AuthenticationErrorResponse) response;

            err.println(errorResponse);
            Assert.fail();
        }

        AuthenticationSuccessResponse successResponse = (AuthenticationSuccessResponse) response;

        AuthorizationCode code = successResponse.getAuthorizationCode();

        assertThat(code, is(notNullValue()));
        assertThat(successResponse.getState(), is(state));
    }
}
