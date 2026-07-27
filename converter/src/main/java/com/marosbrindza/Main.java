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
    // define a date formatter for parsing the input dates
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // main method to run the application
    public static void main(String[] args) {

        // check if the correct number of arguments is provided
        if (args.length != 4) {
            System.out.println("4 arguments required: " + "<inputDirectory> <outputDirectory> <platnostOd> <platnostDo>");
            System.exit(1);
        }

        String inputDirectory = args[0];
        String outputDirectory = args[1];

        LocalDate platnostOd;
        LocalDate platnostDo;

        // parse the input dates and validate that platnostOd is not after platnostDo
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

        // add the Bouncy Castle security provider for cryptographic operations
        Security.addProvider(new BouncyCastleProvider());
        // create instances of the necessary services for reading JSON, validating, writing XML, and signing
        SignatureService signatureService = new SignatureService();
        JsonReader reader = new JsonReader();
        JsonValidator validator = new JsonValidator(platnostOd, platnostDo);
        XmlWriter writer = new XmlWriter();     

        // try to read JSON files, validate them, write to XML, and sign the XML files
        try {
            // generate a key pair and a self-signed certificate for signing the XML files
            CertificateGenerator generator = new CertificateGenerator();
            KeyPair keyPair = generator.generateKeyPair();
            X509Certificate certificate = generator.generateCertificate(keyPair);

            for (Path file : reader.readJsonFiles(inputDirectory)) {

                String outputFileName = file.getFileName().toString().replace(".json", ".xml");
                String outputFile = outputDirectory + "\\" + outputFileName;

                String json = Files.readString(file);
                
                // validate the JSON records and get a list of valid records
                List<JsonNode> validRecords = validator.getValidRecords(json);

                // print the number of valid records found in the JSON file
                System.out.println(file.getFileName() + " -> valid records: " + validRecords.size());
                // if there are valid records, write them to an XML file and sign the XML file
                
                if (validRecords.size() > 0){
                    writer.write(validRecords,outputFile);
                    // create a Path object for the XML file and a Path object for the signed file
                    Path xmlFile = Path.of(outputFile);
                    String signedName = outputFile.replace(".xml", ".signed");
                    Path signedFile = Path.of(signedName);
                    // sign the XML file and save the signature to a .signed file
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