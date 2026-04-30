package com.byteentropy.iso_20022_messaging_core.controller;

import com.byteentropy.iso_20022_messaging_core.dto.InternalPaymentRequest;
import com.byteentropy.iso_20022_messaging_core.dto.IsoResponse;
import com.byteentropy.iso_20022_messaging_core.service.IsoTransformer;
import com.byteentropy.iso_20022_messaging_core.service.XmlGeneratorService;
import com.byteentropy.iso_20022_messaging_core.util.XmlFormatter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/iso")
public class PaymentTranslationController {

    private final IsoTransformer transformer;
    private final XmlGeneratorService xmlService;

    public PaymentTranslationController(IsoTransformer transformer, XmlGeneratorService xmlService) {
        this.transformer = transformer;
        this.xmlService = xmlService;
    }

    @PostMapping(value = "/generate")
    public ResponseEntity<IsoResponse> generateIsoMessage(@Valid @RequestBody InternalPaymentRequest request) {
        var header = transformer.buildHeader();
        var transaction = transformer.mapToTransaction(request);
        
        String rawXml = xmlService.generatePacs008(header, transaction);
        String formattedXml = XmlFormatter.format(rawXml, 4);
        
        IsoResponse response = new IsoResponse(
            header.messageId(),
            "pacs.008.001.10",
            header.creationDateTime(),
            formattedXml
        );
        
        return ResponseEntity.ok(response);
    }
}