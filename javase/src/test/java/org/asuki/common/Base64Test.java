package org.asuki.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.BaseEncoding.base64Url;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static java.util.Base64.getMimeEncoder;
import static java.util.Base64.getUrlEncoder;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64Chunked;
import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafe;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.testng.annotations.Test;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64Test {
    private static final String TEXT = "test";

    @SuppressWarnings("restriction")
    @Test
    public void testBasic() throws IOException {

        String encoded = (new BASE64Encoder()).encodeBuffer(TEXT.getBytes());
        String java8Encoded = getEncoder().encodeToString(
                TEXT.getBytes(StandardCharsets.UTF_8));

        assertThat(encoded, is(java8Encoded + "\n"));

        String decoded = new String((new BASE64Decoder()).decodeBuffer(encoded));
        String java8Decoded = new String(getDecoder().decode(java8Encoded),
                StandardCharsets.UTF_8);

        assertThat(decoded, is(TEXT));
        assertThat(java8Decoded, is(TEXT));
    }

    @Test
    public void testCompare() {
        byte[] bytes = TEXT.getBytes();

        byte[] java8BasicEncoded = getEncoder().encode(bytes);
        byte[] java8UrlEncoded = getUrlEncoder().encode(bytes);
        byte[] java8UrlEncodedNoPad = getUrlEncoder().withoutPadding().encode(bytes);
        byte[] java8MimeEncoded = getMimeEncoder().encode(bytes);

        byte[] commonsBasicEncoded = encodeBase64(bytes);
        byte[] commonsUrlEncoded = encodeBase64URLSafe(bytes);
        byte[] commonsMimeEncoded = encodeBase64Chunked(bytes);

        byte[] guavaBasicEncoded = base64().encode(bytes).getBytes();
        byte[] guavaUrlEncoded = base64Url().encode(bytes).getBytes();

        assertThat(Arrays.equals(java8BasicEncoded, commonsBasicEncoded), is(true));
        assertThat(Arrays.equals(java8UrlEncoded, commonsUrlEncoded), is(false));
        assertThat(Arrays.equals(java8UrlEncodedNoPad, commonsUrlEncoded), is(true));
        assertThat(Arrays.equals(java8MimeEncoded, commonsMimeEncoded),is(false));

        assertThat(Arrays.equals(java8BasicEncoded, guavaBasicEncoded), is(true));
        assertThat(Arrays.equals(java8BasicEncoded, guavaUrlEncoded), is(true));

        assertThat(new String(decodeBase64(commonsBasicEncoded)), is(TEXT));
    }
}
