package com.byteentropy.iso_20022_messaging_core.service;

import com.byteentropy.iso_20022_messaging_core.model.pacs008.GroupHeader;
import com.byteentropy.iso_20022_messaging_core.model.pacs008.CreditTransferTransaction;
import com.byteentropy.iso_20022_messaging_core.dto.InternalPaymentRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class IsoTransformer {

    public GroupHeader buildHeader() {
        return new GroupHeader(
            "MSG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
            LocalDateTime.now(),
            1,
            "BANKUS33XXX" 
        );
    }

    public CreditTransferTransaction mapToTransaction(InternalPaymentRequest request) {
        return new CreditTransferTransaction(
            request.transactionRef(),
            request.amount(),
            request.currency(),
            request.senderName(),
            request.senderIban(),
            request.receiverName(),
            request.receiverIban()
        );
    }
}