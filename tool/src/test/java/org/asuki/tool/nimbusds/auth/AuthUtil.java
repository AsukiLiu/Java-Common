package org.asuki.tool.nimbusds.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AuthUtil {
    private AuthUtil() {
    }

    public static Properties getConfigProps(String path) {
        Properties config = new Properties();

        try (InputStream is = AuthUtil.class.getClassLoader().getResourceAsStream(path)) {
            config.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Could not load properties from " + path, e);
        }

        return config;
    }
}
