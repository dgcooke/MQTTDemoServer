package com.smartphonedev.mqttserver;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemReader;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/*protected*/class SecurityHelper
{
    private static final String TLS_VERSION = "TLSv1.2";

    private SecurityHelper()
    {
        //hiding utility class constructor
    }

    public static SSLSocketFactory createSocketFactory(final String caCertificateFileName, final String clientCertificateFileName, final String clientKeyFileName) throws Exception
    {
        final String clientKeyPassword = "";
        try
        {
            Security.addProvider(new BouncyCastleProvider());
            final KeyManager[] keyManagers = createKeyManagerFactory(clientCertificateFileName, clientKeyFileName, clientKeyPassword).getKeyManagers();
            final TrustManager[] trustManagers = createTrustManagerFactory(caCertificateFileName).getTrustManagers();

            final SSLContext context = SSLContext.getInstance(TLS_VERSION);
            context.init(keyManagers, trustManagers, new SecureRandom());
            return context.getSocketFactory();
        } catch (Exception e)
        {
            throw new Exception("I cannot create the TLS socket factory.", e);
        }
    }


    private static KeyFactory getKeyFactoryInstance() throws NoSuchAlgorithmException
    {
        return KeyFactory.getInstance("RSA");
    }

    private static X509Certificate createX509CertificateFromFile(final String certificateFileName) throws IOException, CertificateException
    {
        final var file = new java.io.File(certificateFileName);
        if (!file.isFile())
        {
            throw new IOException(String.format("The certificate file %s doesn't exist.", certificateFileName));
        }
        final CertificateFactory certificateFactoryX509 = CertificateFactory.getInstance("X.509");
        final var inputStream = new FileInputStream(file);
        final var certificate = (X509Certificate) certificateFactoryX509.generateCertificate(inputStream);
        inputStream.close();
        return certificate;
    }

    private static PrivateKey createPrivateKeyFromPemFile(final String keyFileName) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        final var pemReader = new PemReader(new FileReader(keyFileName));
        final var pemObject = pemReader.readPemObject();
        final byte[] pemContent = pemObject.getContent();
        pemReader.close();

        final var encodedKeySpec = new PKCS8EncodedKeySpec(pemContent);
        final var keyFactory = getKeyFactoryInstance();
        final var privateKey = keyFactory.generatePrivate(encodedKeySpec);
        return privateKey;
    }

    private static KeyManagerFactory createKeyManagerFactory(final String clientCertificateFileName, final String clientKeyFileName, final String clientKeyPassword)
            throws InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException
    {
        final var clientCertificate = createX509CertificateFromFile(clientCertificateFileName);
        final var privateKey = createPrivateKeyFromPemFile(clientKeyFileName);
        final var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        keyStore.load(null, null);
        keyStore.setCertificateEntry("certificate", clientCertificate);
        keyStore.setKeyEntry("private-key", privateKey, clientKeyPassword.toCharArray(), new Certificate[] { clientCertificate });
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, clientKeyPassword.toCharArray());
        return keyManagerFactory;
    }

    private static TrustManagerFactory createTrustManagerFactory(final String caCertificateFileName) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException
    {
        final var caCertificate = (X509Certificate) createX509CertificateFromFile(caCertificateFileName);
        final var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca-certificate", caCertificate);

        final var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        return trustManagerFactory;
    }
}
