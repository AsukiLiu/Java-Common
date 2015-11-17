package org.asuki.tool.nimbusds.auth;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import static java.lang.System.err;
import static java.lang.System.out;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class OAuth2TokenRequestTest {

    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String TOKEN_URL;

    static {
        Properties config = AuthUtil.getConfigProps("oauth2.properties");
//        Properties config = AuthUtil.getConfigProps("real.properties");

        CLIENT_ID = (String) config.get("client_id");
        CLIENT_SECRET = (String) config.get("client_secret");
        TOKEN_URL = (String) config.get("token_url");
    }

    // NOTE: Fill in "oauth2.properties" in advance
//    @Test
    public void testClientCredentialsGrant() throws Exception {
        String accessToken = clientCredentialsGrant();

        assertThat(accessToken, is(notNullValue()));
    }

    public static String authorizationCodeGrant(String code)
            throws URISyntaxException, SerializeException, IOException, ParseException {

        TokenRequest request = new TokenRequest(
                new URI(TOKEN_URL),
                new ClientSecretBasic(new ClientID(CLIENT_ID), new Secret(CLIENT_SECRET)),
                new AuthorizationCodeGrant(new AuthorizationCode(code), new URI("https://client.com/callback")));

        return requestToken(request);
    }

    public static String passwordCredentialsGrant(String username, String password)
            throws URISyntaxException, SerializeException, IOException, ParseException {

        TokenRequest request = new TokenRequest(
                new URI(TOKEN_URL),
                new ClientSecretBasic(new ClientID(CLIENT_ID), new Secret(CLIENT_SECRET)),
                new ResourceOwnerPasswordCredentialsGrant(username, new Secret(password)),
                new Scope("read", "write"));

        return requestToken(request);
    }

    public static String clientCredentialsGrant()
            throws URISyntaxException, SerializeException, IOException, ParseException {

        TokenRequest request = new TokenRequest(
                new URI(TOKEN_URL),
                new ClientSecretBasic(new ClientID(CLIENT_ID), new Secret(CLIENT_SECRET)),
                new ClientCredentialsGrant(),
                new Scope("read", "write"));

        return requestToken(request);
    }

    public static String refreshTokenGrant(String token)
            throws URISyntaxException, SerializeException, IOException, ParseException {

        TokenRequest request = new TokenRequest(
                new URI(TOKEN_URL),
                new ClientSecretBasic(new ClientID(CLIENT_ID), new Secret(CLIENT_SECRET)),
                new RefreshTokenGrant(new RefreshToken(token)));

        return requestToken(request);
    }

    private static String requestToken(TokenRequest request)
            throws URISyntaxException, SerializeException, IOException, ParseException {

        TokenResponse response = TokenResponse.parse(request.toHTTPRequest().send());

        if (!response.indicatesSuccess()) {
            TokenErrorResponse errorResponse = (TokenErrorResponse) response;

            err.println(errorResponse);
            Assert.fail();
        }

        AccessTokenResponse successResponse = (AccessTokenResponse) response;

        AccessToken accessToken = successResponse.getAccessToken();
        RefreshToken refreshToken = successResponse.getRefreshToken();

        out.println(accessToken);
        out.println(refreshToken);

        return accessToken.getValue();
    }
}
