package com.marosbrindza;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JsonValidator {

    private final ObjectMapper mapper = new ObjectMapper();
    private LocalDate platnostOd;
    private LocalDate platnostDo;

    // constructor to initialize the validity period
    public JsonValidator(LocalDate platnostOd, LocalDate platnostDo) {
        this.platnostOd = platnostOd;
        this.platnostDo = platnostDo;
    }

    // method to validate the JSON records and return a list of valid records
    public List<JsonNode> getValidRecords(String json) {

        List<JsonNode> validRecords = new ArrayList<>();

        try {
            JsonNode array = mapper.readTree(json);
            if (!array.isArray()) {
                return validRecords; // empty
            }
            for (JsonNode node : array) {
                if (isValid(node)) {
                    validRecords.add(node);
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid JSON syntax");
        }
        return validRecords;
    }

    // method to check if a JSON record is valid based on the specified criteria
    private boolean isValid(JsonNode node) {
        try {
            // id
            JsonNode idNode = node.get("id");
            if (idNode == null || idNode.asText().isBlank()) {
                invalidMessage(node, "id");
                return false;
            }

            // type
            JsonNode typeNode = node.get("type");
            if (typeNode == null) {
                invalidMessage(node, "type");
                return false;
            }
            String typeText = typeNode.asText();
            if (!typeText.equals("ORDER") && !typeText.equals("INVOICE")) {
                invalidMessage(node, "type");
                return false;
            }

            // created
            try {
                String createdText = node.get("created").asText();
                LocalDate created = LocalDate.parse(createdText);
                if (created.isBefore(platnostOd) || created.isAfter(platnostDo)) {
                    invalidMessage(node, "created");
                    return false;
                }   
            } catch (Exception e) {
                invalidMessage(node, "created");
                return false;
            }

            // amount
            JsonNode amountNode = node.get("amount");
            if (amountNode == null || amountNode.asDouble() < 0) {
                invalidMessage(node, "amount");
                return false;
            }

            // vat
            JsonNode vatNode = node.get("vat");
            if (vatNode == null || !vatNode.isInt()) {
                invalidMessage(node, "vat");
                return false;
            }
            int vat = vatNode.asInt();
            if (vat < 0 || vat > 100) {
                invalidMessage(node, "vat");
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Invalid JSON syntax");
            return false;
        }
        System.out.println(
            "Valid record: " + node.toString()
        );
        return true;
    }
    
    private void invalidMessage(JsonNode node, String fieldName) {
        System.out.println("Invalid field \"" + fieldName + "\": " + (node.get(fieldName) == null ? "null" : node.get(fieldName).asText()));
    }
}