package com.byteentropy.iso_20022_messaging_core;

import com.byteentropy.iso_20022_messaging_core.dto.InternalPaymentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IsoMessagingApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Success Case: Valid request produces 200 OK and ISO XML")
    void shouldReturnFormattedIsoResponse() {
        InternalPaymentRequest request = new InternalPaymentRequest(
            "REF-999",
            new BigDecimal("50.00"),
            "GBP",
            "John Doe", "GB29000012345678",
            "Jane Smith", "GB30000087654321"
        );

        ResponseEntity<Object> response = restTemplate.postForEntity("/api/v1/iso/generate", request, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        String bodyString = response.getBody().toString();
        assertThat(bodyString).contains("pacs.008.001.10");
    }

    @Test
    @DisplayName("Failure Case: Invalid data produces 400 Bad Request with error details")
    void shouldReturn400WhenRequestIsInvalid() {
        // Given
        InternalPaymentRequest invalidRequest = new InternalPaymentRequest(
            "REF-BAD",
            new BigDecimal("-10.00"),
            "USDOLLAR",
            "John", "BAD_IBAN",
            "Jane", "BAD_IBAN"
        );

        // Define the expected return type for the Map
        ParameterizedTypeReference<Map<String, String>> responseType = 
            new ParameterizedTypeReference<>() {};

        // When - Using exchange to maintain type safety
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
            "/api/v1/iso/generate",
            HttpMethod.POST,
            new HttpEntity<>(invalidRequest),
            responseType
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> errors = response.getBody();
        
        assertThat(errors).isNotNull();
        assertThat(errors).containsKey("amount");
        assertThat(errors).containsKey("currency");
        assertThat(errors.get("amount")).isEqualTo("Payment amount must be greater than zero");
    }
}