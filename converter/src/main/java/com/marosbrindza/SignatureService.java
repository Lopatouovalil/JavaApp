package com.marosbrindza;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class SignatureService {

    public void sign(Path xmlFile, Path signedFile, PrivateKey privateKey) throws Exception {

        byte[] xmlBytes = Files.readAllBytes(xmlFile);

        Signature signature = Signature.getInstance("SHA256withRSA", "BC");

        signature.initSign(privateKey);

        signature.update(xmlBytes);

        byte[] signedBytes = signature.sign();

        String base64 = Base64.getEncoder().encodeToString(signedBytes);

        Files.writeString(signedFile, base64);
    }
}