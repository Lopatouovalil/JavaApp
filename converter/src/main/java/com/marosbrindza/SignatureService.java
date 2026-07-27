package com.marosbrindza;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class SignatureService {

    // sign an XML file with a private key and save the signature to a file
    public void sign(Path xmlFile, Path signedFile, PrivateKey privateKey) throws Exception {

        // read the XML file into a byte array
        byte[] xmlBytes = Files.readAllBytes(xmlFile);

        // create a Signature object with the SHA256withRSA algorithm and Bouncy Castle as the provider
        Signature signature = Signature.getInstance("SHA256withRSA", "BC");

        // initialize the Signature object with the private key for signing
        signature.initSign(privateKey);

        // update the Signature object with the XML bytes to be signed
        signature.update(xmlBytes);

        // generate the digital signature and encode it in Base64 format
        byte[] signedBytes = signature.sign();

        // encode the signed bytes to Base64 and write to the signed file
        String base64 = Base64.getEncoder().encodeToString(signedBytes);

        // write the Base64-encoded signature to the specified file
        Files.writeString(signedFile, base64);
    }
}