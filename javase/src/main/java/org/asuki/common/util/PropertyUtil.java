package org.asuki.common.util;

import com.google.common.base.Charsets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class PropertyUtil {

    // Approach 1
    public static ResourceBundle.Control UTF8_ENCODING_CONTROL = new ResourceBundle.Control() {

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale,
                String format, ClassLoader loader, boolean reload)
                throws IOException {

            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");

            // @formatter:off
            try (InputStream is = loader.getResourceAsStream(resourceName);
                    InputStreamReader isr = new InputStreamReader(is, Charsets.UTF_8);
                    BufferedReader reader = new BufferedReader(isr)) {
             // @formatter:on

                return new PropertyResourceBundle(reader);
            }

        }
    };

    // Approach 2
    public static Properties loadUtf8Properties(String resourceName)
            throws IOException {

        // @formatter:off
        try (InputStream is = PropertyUtil.class.getResourceAsStream(resourceName);
                InputStreamReader isr = new InputStreamReader(is, Charsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr)) {
         // @formatter:on

            Properties result = new Properties();
            result.load(reader);

            return result;
        }
    }

}
