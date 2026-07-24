package com.marosbrindza;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyPair;
import java.security.Security;

import java.security.cert.X509Certificate;

public class Main {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println(
               "4 arguments required: " +
               "<inputDirectory> <outputDirectory> <platnostOd> <platnostDo>"
            );
            System.exit(1);
        }

        String inputDirectory = args[0];
        String outputDirectory = args[1];

        LocalDate platnostOd;
        LocalDate platnostDo;

        try {

            platnostOd = LocalDate.parse(args[2], DATE_FORMAT);
            platnostDo = LocalDate.parse(args[3], DATE_FORMAT);

            if (platnostOd.isAfter(platnostDo)) {
                throw new IllegalArgumentException(
                   "platnostOd cannot be after platnostDo"
                );
            }

        } catch (Exception e) {

            System.err.println(
               "Invalid date format. Expected YYYY-MM-DD"
            );
            System.exit(1);
            return;
        }

        Security.addProvider(new BouncyCastleProvider());
        SignatureService signatureService = new SignatureService();
        JsonReader reader = new JsonReader();
        JsonValidator validator = new JsonValidator(platnostOd, platnostDo);
        XmlWriter writer = new XmlWriter();     

        try {
            CertificateGenerator generator = new CertificateGenerator();
            KeyPair keyPair = generator.generateKeyPair();
            X509Certificate certificate = generator.generateCertificate(keyPair);

            for (Path file : reader.readJsonFiles(inputDirectory)) {

                String outputFileName = file.getFileName().toString().replace(".json", ".xml");
                String outputFile = outputDirectory + "\\" + outputFileName;

                String json = Files.readString(file);
                
                List<JsonNode> validRecords = validator.getValidRecords(json);

                System.out.println(
                    file.getFileName() +
                    " -> valid records: " +
                    validRecords.size()
                );
                if (validRecords.size() > 0) {
                    writer.write(
                        validRecords,
                        outputFile
                    );

                    Path xmlFile = Path.of(outputFile);

                    String signedName = outputFile.replace(".xml", ".signed");

                    Path signedFile = Path.of(signedName);

                    signatureService.sign(xmlFile,signedFile,keyPair.getPrivate());
                    
                    System.out.println("Written to output");
                } else {
                    System.out.println("No valid records");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}