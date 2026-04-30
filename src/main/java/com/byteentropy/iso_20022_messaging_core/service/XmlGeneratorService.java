package com.byteentropy.iso_20022_messaging_core.service;

// Explicitly importing the types from the pacs008 sub-package
import com.byteentropy.iso_20022_messaging_core.model.pacs008.GroupHeader;
import com.byteentropy.iso_20022_messaging_core.model.pacs008.CreditTransferTransaction;

import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
public class XmlGeneratorService {

    // Using a static formatter for better performance
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Generates a pacs.008.001.10 XML message.
     * Uses Java 21 Text Blocks for template clarity and performance.
     */
    public String generatePacs008(GroupHeader header, CreditTransferTransaction tx) {
        String timestamp = header.creationDateTime().format(ISO_FORMATTER);

        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.10">
                <FIToFICstmrCdtTrf>
                    <GrpHdr>
                        <MsgId>%s</MsgId>
                        <CreDtTm>%s</CreDtTm>
                        <NbOfTxs>%d</NbOfTxs>
                        <InstgAgt>
                            <FinInstnId><BICFI>%s</BICFI></FinInstnId>
                        </InstgAgt>
                    </GrpHdr>
                    <CdtTrfTxInf>
                        <PmtId>
                            <EndToEndId>%s</EndToEndId>
                        </PmtId>
                        <IntrBkSttlmAmt Ccy="%s">%s</IntrBkSttlmAmt>
                        <Dbtr>
                            <Nm>%s</Nm>
                        </Dbtr>
                        <DbtrAcct>
                            <Id><IBAN>%s</IBAN></Id>
                        </DbtrAcct>
                        <Cdtr>
                            <Nm>%s</Nm>
                        </Cdtr>
                        <CdtrAcct>
                            <Id><IBAN>%s</IBAN></Id>
                        </CdtrAcct>
                    </CdtTrfTxInf>
                </FIToFICstmrCdtTrf>
            </Document>
            """.formatted(
                header.messageId(),
                timestamp,
                header.numberOfTransactions(),
                header.instructingAgentBic(),
                tx.endToEndId(),
                tx.currency(),
                tx.amount().toPlainString(),
                tx.debtorName(),
                tx.debtorIban(),
                tx.creditorName(),
                tx.creditorIban()
            );
    }
}