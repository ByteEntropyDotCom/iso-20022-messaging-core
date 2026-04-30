package com.byteentropy.iso_20022_messaging_core.service;

import com.byteentropy.iso_20022_messaging_core.dto.InternalPaymentRequest;
import com.byteentropy.iso_20022_messaging_core.model.pacs008.CreditTransferTransaction;
import com.byteentropy.iso_20022_messaging_core.model.pacs008.GroupHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class IsoTransformationTest {

    private IsoTransformer transformer;
    private XmlGeneratorService xmlGenerator;

    @BeforeEach
    void setUp() {
        transformer = new IsoTransformer();
        xmlGenerator = new XmlGeneratorService();
    }

    @Test
    @DisplayName("Should correctly transform internal request to ISO XML components")
    void testFullTransformationFlow() {
        // Given - Updated with valid IBAN formats and 3-letter currency
        InternalPaymentRequest request = new InternalPaymentRequest(
            "TXN-12345",
            new BigDecimal("1500.50"),
            "USD",
            "Alice Sender",
            "US12345678901234567890",
            "Bob Receiver",
            "GB98765432109876543210"
        );

        // When
        GroupHeader header = transformer.buildHeader();
        CreditTransferTransaction tx = transformer.mapToTransaction(request);
        String xml = xmlGenerator.generatePacs008(header, tx);

        // Then
        assertThat(header.messageId()).startsWith("MSG-");
        assertThat(tx.amount()).isEqualByComparingTo("1500.50");
        assertThat(tx.currency()).isEqualTo("USD");
        
        // Assert XML contains critical ISO 20022 tags
        assertThat(xml).contains("<MsgId>" + header.messageId() + "</MsgId>");
        assertThat(xml).contains("<EndToEndId>TXN-12345</EndToEndId>");
        assertThat(xml).contains("Ccy=\"USD\">1500.50</IntrBkSttlmAmt>");
        assertThat(xml).contains("<IBAN>US12345678901234567890</IBAN>");
        assertThat(xml).contains("urn:iso:std:iso:20022:tech:xsd:pacs.008.001.10");
    }

    @Test
    @DisplayName("Should handle large amounts with correct scale in XML")
    void testLargeAmountScaling() {
        // Given - Updated with valid IBAN formats
        InternalPaymentRequest request = new InternalPaymentRequest(
            "TXN-LARGE",
            new BigDecimal("1000000.00"),
            "EUR",
            "Corp A", "IE1234567890", 
            "Corp B", "IE0987654321"
        );

        // When
        CreditTransferTransaction tx = transformer.mapToTransaction(request);
        String xml = xmlGenerator.generatePacs008(transformer.buildHeader(), tx);

        // Then
        assertThat(xml).contains(">1000000.00</IntrBkSttlmAmt>");
    }
}