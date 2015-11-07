package org.asuki.common.util;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import static java.lang.String.format;
import static java.util.ResourceBundle.getBundle;
import static org.asuki.common.util.PropertyUtil.UTF8_ENCODING_CONTROL;
import static org.asuki.common.util.PropertyUtil.loadUtf8Properties;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PropertyUtilTest {

    @Test
    public void testUTF8() throws IOException {

        final String KEY = "キー";
        final String FILE_NAME = "test_utf8";

        Properties prop = loadUtf8Properties(format("/%s.properties", FILE_NAME));

        ResourceBundle bundle = getBundle(FILE_NAME, UTF8_ENCODING_CONTROL);

        assertThat(prop.getProperty(KEY), is(bundle.getString(KEY)));
    }
}
