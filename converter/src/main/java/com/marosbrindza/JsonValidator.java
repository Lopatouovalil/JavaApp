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

    public JsonValidator(LocalDate platnostOd, LocalDate platnostDo) {
        this.platnostOd = platnostOd;
        this.platnostDo = platnostDo;
    }

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

    private boolean isValid(JsonNode node) {
        try {
            // id
            if (node.get("id").asText().isBlank()) {
                return false;
            }
            // type
            String type = node.get("type").asText();

            if (!type.equals("ORDER") &&
                !type.equals("INVOICE")) {
                return false;
            }
            // created
            LocalDate created = LocalDate.parse(node.get("created").asText());
            if (created.isBefore(platnostOd) ||
                created.isAfter(platnostDo)) {
                return false;
            }
            // amount
            if (node.get("amount").asDouble() < 0) {
                return false;
            }
            // vat
            if (!node.get("vat").isInt()) {
                return false;
            }
            int vat = node.get("vat").asInt();
            if (vat < 0 || vat > 100) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}