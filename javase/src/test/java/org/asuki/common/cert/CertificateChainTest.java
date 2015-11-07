package org.asuki.common.cert;

import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import static java.lang.System.err;
import static java.lang.System.out;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

//import javax.security.cert.Certificate;

//import sun.security.tools.keytool.CertAndKeyGen;
//import sun.security.x509.BasicConstraintsExtension;
//import sun.security.x509.CertificateExtensions;
//import sun.security.x509.X500Name;
//import sun.security.x509.X509CertImpl;
//import sun.security.x509.X509CertInfo;

@SuppressWarnings("restriction")
public class CertificateChainTest {

    private static final String KEYSTORE_TYPE = "jks";
    private static final long CERTIFICATE_EXPIRY = 365 * 24 * 60 * 60L;

    @Test
    public void shouldGenerateCertificateChain() throws Exception {

//        // Root certificate (self signed: subject and issuer is the same)
//        CertAndKeyGen rootGen = genInstance();
//        X509Certificate rootCertificate = rootGen.getSelfCertificate(
//                new X500Name("CN=ROOT"), CERTIFICATE_EXPIRY);
//
//        // Intermediate certificate
//        CertAndKeyGen middleGen = genInstance();
//        X509Certificate middleCertificate = middleGen.getSelfCertificate(
//                new X500Name("CN=MIDDLE"), CERTIFICATE_EXPIRY);
//
//        // Leaf certificate
//        CertAndKeyGen topGen = genInstance();
//        X509Certificate topCertificate = topGen.getSelfCertificate(
//                new X500Name("CN=TOP"), CERTIFICATE_EXPIRY);
//
//        PrivateKey rootPrivateKey = rootGen.getPrivateKey();
//        PrivateKey middlePrivateKey = middleGen.getPrivateKey();
//        PrivateKey topPrivateKey = topGen.getPrivateKey();
//
//        rootCertificate = createSignedCertificate(rootCertificate,
//                rootCertificate, rootPrivateKey);
//        middleCertificate = createSignedCertificate(middleCertificate,
//                rootCertificate, rootPrivateKey);
//        topCertificate = createSignedCertificate(topCertificate,
//                middleCertificate, middlePrivateKey);
//
//        X509Certificate[] chain = { topCertificate, middleCertificate,
//                rootCertificate };
//
//        String alias = "mykey";
//        char[] password = "password".toCharArray();
//        String keystore = "test.jks";
//
//        storeKeyAndChain(alias, password, keystore, topPrivateKey, chain);
//
//        loadAndDisplayChain(alias, password, keystore);
//
//        clearKeyStore(alias, password, keystore);
    }

//    private static CertAndKeyGen genInstance() throws Exception {
//        CertAndKeyGen gen = new CertAndKeyGen("RSA", "SHA1WithRSA", null);
//        gen.generate(1024);
//        return gen;
//    }

    // Use one certificate and its private key to sign another certificate
//    private static X509Certificate createSignedCertificate(
//            X509Certificate cetrificate, X509Certificate issuerCertificate,
//            PrivateKey issuerPrivateKey) {
//
//        try {
//            Principal issuer = issuerCertificate.getSubjectDN();
//            String issuerSigAlg = issuerCertificate.getSigAlgName();
//
//            byte[] inCertBytes = cetrificate.getTBSCertificate();
//            X509CertInfo info = new X509CertInfo(inCertBytes);
//            info.set(X509CertInfo.ISSUER, (X500Name) issuer);
//
//            // Except leaf certificate
//            if (!cetrificate.getSubjectDN().getName().equals("CN=TOP")) {
//                // Add CA extension to the certificate
//                CertificateExtensions exts = new CertificateExtensions();
//                BasicConstraintsExtension bce = new BasicConstraintsExtension(
//                        true, -1);
//                exts.set(
//                        BasicConstraintsExtension.NAME,
//                        new BasicConstraintsExtension(false, bce
//                                .getExtensionValue()));
//                info.set(X509CertInfo.EXTENSIONS, exts);
//            }
//
//            X509CertImpl outCert = new X509CertImpl(info);
//            outCert.sign(issuerPrivateKey, issuerSigAlg);
//
//            return outCert;
//        } catch (Exception e) {
//            err.println(e);
//            return null;
//        }
//    }

    private static void storeKeyAndChain(String alias, char[] password,
            String keystore, Key key, X509Certificate[] chain) throws Exception {

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(null, null);
        keyStore.setKeyEntry(alias, key, password, chain);
        keyStore.store(new FileOutputStream(keystore), password);
    }

    private static void loadAndDisplayChain(String alias, char[] password,
            String keystore) throws Exception {

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(new FileInputStream(keystore), password);

        Key key = keyStore.getKey(alias, password);

        if (key instanceof PrivateKey) {
            out.println("Private key : ");
            out.println(key.toString());

            Certificate[] certs = keyStore.getCertificateChain(alias);
            assertThat(certs.length, is(3));

            for (Certificate cert : certs) {
                out.println(cert.toString());
            }
        } else {
            err.println("Key is not private key");
        }
    }

    private static void clearKeyStore(String alias, char[] password,
            String keystore) throws Exception {

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(new FileInputStream(keystore), password);
        keyStore.deleteEntry(alias);
        keyStore.store(new FileOutputStream(keystore), password);
    }
}
