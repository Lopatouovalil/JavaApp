package com.marosbrindza;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.math.BigInteger;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

// certificate generator using Bouncy Castle
public class CertificateGenerator {
	// generating a new RSA key pair
    public KeyPair generateKeyPair() throws Exception {

		// creating a new instance of the RSA key pair generator with Bouncy Castle as the provider
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");

        generator.initialize(2048);

        return generator.generateKeyPair();
    }

    // generate a new X.509 certificate
    public X509Certificate generateCertificate(KeyPair keyPair) throws Exception {

		// creating a self-signed certificate with the given key pair
        X500Name issuer = new X500Name("CN=MarosB");
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

		// setting the validity period of the certificate to 1 year
        Date notBefore = new Date();
        Date notAfter = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);

		// building the certificate using the Bouncy Castle library
        X509v3CertificateBuilder builder =
                new JcaX509v3CertificateBuilder(
                        issuer,
                        serial,
                        notBefore,
                        notAfter,
                        issuer,
                        keyPair.getPublic()
                );

		// signing the certificate with the private key of the key pair
        ContentSigner signer =
                new JcaContentSignerBuilder("SHA256withRSA")
                        .setProvider("BC")
                        .build(keyPair.getPrivate());
		
		// converting the certificate to a standard X.509 certificate
        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(builder.build(signer));
    }
}

