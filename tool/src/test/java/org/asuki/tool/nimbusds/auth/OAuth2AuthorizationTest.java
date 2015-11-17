package org.asuki.tool.nimbusds.auth;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.AuthorizationRequest.Builder;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.hamcrest.Matcher;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.nimbusds.oauth2.sdk.ResponseType.Value.CODE;
import static com.nimbusds.oauth2.sdk.ResponseType.Value.TOKEN;
import static java.lang.System.err;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OAuth2AuthorizationTest {

    private State state = new State();

    // NOTE: Run "Spring-Boot" repo in advance
//    @Test(dataProvider = "data")
    public void test(ResponseType.Value type, Matcher<AuthorizationCode> codeMatcher, Matcher<AccessToken> tokenMatcher)
            throws Exception {

        HTTPResponse httpResponse = requestAuthorisation(type);
        callback(httpResponse, codeMatcher, tokenMatcher);
    }

    private HTTPResponse requestAuthorisation(ResponseType.Value type)
            throws URISyntaxException, SerializeException, IOException, ParseException {

        AuthorizationRequest request = new Builder(new ResponseType(type), new ClientID("123abcd"))
                .scope(new Scope("read", "write"))
                .state(state)
                .redirectionURI(new URI("https://client.com/callback"))
                .endpointURI(new URI("http://localhost:8082/api/authz"))
                .build();

//        URI requestUri = request.toURI();

        return request.toHTTPRequest().send();
    }

    private void callback(HTTPResponse httpResponse,
                          Matcher<AuthorizationCode> codeMatcher, Matcher<AccessToken> tokenMatcher)
            throws URISyntaxException, ParseException {

//        AuthorizationResponse response = AuthorizationResponse.parse(new URI("https://client.com/callback"));
        AuthorizationResponse response = AuthorizationResponse.parse(httpResponse);

        if (!response.indicatesSuccess()) {
            AuthorizationErrorResponse errorResponse = (AuthorizationErrorResponse) response;

            err.println(errorResponse);
            Assert.fail();
        }

        AuthorizationSuccessResponse successResponse = (AuthorizationSuccessResponse) response;

        AuthorizationCode code = successResponse.getAuthorizationCode();
        AccessToken token = successResponse.getAccessToken();

        assertThat(code, is(codeMatcher));
        assertThat(token, is(tokenMatcher));
        assertThat(successResponse.getState(), is(state));
    }

    @DataProvider
    private Object[][] data() {
        return new Object[][]{
                {CODE, notNullValue(), nullValue()},        // AuthCode Flow
                {TOKEN, nullValue(), notNullValue()},       // Implicit Flow
        };
    }

}
