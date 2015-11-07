package org.asuki.common.cert;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.KeyGenerator;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;

import static java.lang.System.out;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

//import sun.security.tools.keytool.CertAndKeyGen;
//import sun.security.x509.X500Name;

@SuppressWarnings("restriction")
public class KeyStoreTest {

    private static final long CERTIFICATE_EXPIRY = 365 * 24 * 60 * 60L;
    private static final String PASSWORD = "password";

    @Test(dataProvider = "secretData")
    public void testSecretKey(String keystoreType, String outputFile)
            throws Exception {
        final String ALIAS = "secret";

        // Store secret key
        {
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(null, null);

            // KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            // keyGen.init(56);
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);

            Key key = keyGen.generateKey();

            keyStore.setKeyEntry(ALIAS, key, PASSWORD.toCharArray(), null);

            keyStore.store(new FileOutputStream(outputFile),
                    PASSWORD.toCharArray());
        }

        // Load secret key
        {
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(new FileInputStream(outputFile),
                    PASSWORD.toCharArray());

            Key key = keyStore.getKey(ALIAS, PASSWORD.toCharArray());
            out.println(key.toString());
        }
    }

//    @Test(dataProvider = "data")
    public void testPrivateKey(String keystoreType, String outputFile)
            throws Exception {
        final String ALIAS = "private";

        createKeystore(keystoreType, outputFile);

        // Store private key
        {
//            KeyStore keyStore = KeyStore.getInstance(keystoreType);
//            keyStore.load(new FileInputStream(outputFile),
//                    PASSWORD.toCharArray());
//
//            CertAndKeyGen gen = new CertAndKeyGen("RSA", "SHA1WithRSA");
//            gen.generate(1024);
//
//            Key key = gen.getPrivateKey();
//            X509Certificate cert = gen.getSelfCertificate(new X500Name(
//                    "CN=ROOT"), CERTIFICATE_EXPIRY);
//            X509Certificate[] chain = { cert };
//
//            keyStore.setKeyEntry(ALIAS, key, PASSWORD.toCharArray(), chain);
//
//            keyStore.store(new FileOutputStream(outputFile),
//                    PASSWORD.toCharArray());
        }

        // Load private key
        {
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(new FileInputStream(outputFile),
                    PASSWORD.toCharArray());

            Key key = keyStore.getKey(ALIAS, PASSWORD.toCharArray());
            out.println("Private key : " + key.toString());

            Certificate[] chain = keyStore.getCertificateChain(ALIAS);
            for (Certificate cert : chain) {
                out.println(cert.toString());
            }

            Certificate cert = keyStore.getCertificate(ALIAS);
            assertThat(chain[0].toString(), is(cert.toString()));
        }

    }

    @Test(dataProvider = "data")
    public void testCertificate(String keystoreType, String outputFile)
            throws Exception {
        final String ALIAS = "single_cert";

        createKeystore(keystoreType, outputFile);

        // Store certificate
        {
//            KeyStore keyStore = KeyStore.getInstance(keystoreType);
//            keyStore.load(new FileInputStream(outputFile),
//                    PASSWORD.toCharArray());
//
//            CertAndKeyGen gen = new CertAndKeyGen("RSA", "SHA1WithRSA");
//            gen.generate(1024);
//
//            X509Certificate cert = gen.getSelfCertificate(new X500Name(
//                    "CN=SINGLE_CERTIFICATE"), CERTIFICATE_EXPIRY);
//
//            keyStore.setCertificateEntry(ALIAS, cert);
//
//            keyStore.store(new FileOutputStream(outputFile),
//                    PASSWORD.toCharArray());
        }

        // Load certificate
        {
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(new FileInputStream(outputFile),
                    PASSWORD.toCharArray());

            Certificate[] chain = keyStore.getCertificateChain(ALIAS);
            assertThat(chain, is(nullValue()));

            Certificate cert = keyStore.getCertificate(ALIAS);
            out.println(cert);
        }

    }

    private static void createKeystore(String keystoreType, String outputFile)
            throws Exception {

        KeyStore keyStore = KeyStore.getInstance(keystoreType);
        keyStore.load(null, null);

        keyStore.store(new FileOutputStream(outputFile), PASSWORD.toCharArray());
    }

    @DataProvider
    private Object[][] data() {
        // @formatter:off
        return new Object[][] { 
                { "JKS", "output.jks" },
                { "PKCS12", "output.p12" } };
        // @formatter:on
    }

    @DataProvider
    private Object[][] secretData() {
        // @formatter:off
        return new Object[][] { 
                //{ "PKCS12", "output.p12" },   // doesn't work?
                { "JCEKS", "output.jceks" } };
        // @formatter:on
    }
}
