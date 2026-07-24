package com.marosbrindza;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;

import org.w3c.dom.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;

public class XmlWriter {

    public void write(List<JsonNode> messages,String outputFile) throws Exception {

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Element header = document.createElement("Messages");

        document.appendChild(header);

        for(JsonNode json : messages){

            Element message = document.createElement("Message");

            addElement(document,message,"Id",json.get("id").asText());

            addElement(document,message,"Type",json.get("type").asText());

            addElement(document,message,"Created",json.get("created").asText());

            BigDecimal amount =json.get("amount").decimalValue();

            int vat =json.get("vat").asInt();

            addElement(document,message,"Amount",amount.toString());

            addElement(document,message,"Vat",String.valueOf(vat));
            
			BigDecimal vatAmount = BigDecimal.valueOf(vat).divide(BigDecimal.valueOf(100));

			BigDecimal multiplier = BigDecimal.ONE.add(vatAmount);

			BigDecimal amountWithVat = amount.multiply(multiplier);

            addElement(document,message,"AmountWithVat",amountWithVat.toString());

            header.appendChild(message);
        }
		System.out.println("Writing XML to: " + outputFile);

        javax.xml.transform.Transformer transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT,"yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","4");
		
        transformer.transform(
                new javax.xml.transform.dom.DOMSource(document),
                new javax.xml.transform.stream.StreamResult(new File(outputFile))
        );
    }

    private void addElement(
            Document doc,
            Element parent,
            String name,
            String value
    ){
        Element element = doc.createElement(name);
 
        element.appendChild(doc.createTextNode(value));

        parent.appendChild(element);
    }
}