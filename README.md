# iso-20022-messaging-core

A high-performance, Loom-native adapter for transforming internal payment instructions into **ISO 20022 (pacs.008.001.10)** compliant financial messages.

## 🏗️ Architecture
- **Java 21 (Virtual Threads):** Optimized for massive throughput in I/O-bound messaging scenarios.
- **Zero Boilerplate:** Utilizes Java Records for immutable data modeling.
- **Logic-in-Template:** Uses Java 21 Text Blocks for high-speed XML generation without the overhead of heavy Marshalling libraries (JAXB).

## Technical
- Engine: Java 21 (Temurin) with Virtual Threads (Project Loom).
- GC: Generational ZGC for low-latency financial processing.
- Spec: ISO 20022 pacs.008.001.10 compliant.
- Security: Non-root Docker execution & strict Input Validation.
- Observability: Integrated Micrometer/Prometheus telemetry.

## 🛠️ Key Components
- `IsoTransformer`: Maps internal DTOs to ISO-compliant Records.
- `XmlGeneratorService`: Generates strict ISO 20022 XML messages.
- `XmlFormatter`: Ensures human-readable, pretty-printed output for audit logging and debugging.


---

## Tests

### 🟢 Test 1: The "Happy Path" (Valid Request)

This uses a valid 3-letter currency and correctly formatted IBANs.

```Bash
curl -i -X POST http://localhost:8085/api/v1/iso/generate \
-H "Content-Type: application/json" \
-d '{
    "transactionRef": "BE-2026-OK",
    "amount": 1500.00,
    "currency": "USD",
    "senderName": "ByteEntropy Labs",
    "senderIban": "US12345678901234567890",
    "receiverName": "Global Settlement Bank",
    "receiverIban": "GB98765432109876543210"
}'
```

Expected Result: 200 OK with the full ISO XML in the rawXml field.

###  🔴 Test 2: Validation Failure (Bad Currency & Negative Amount)

This tests if your @Positive and @Pattern constraints are working.

```Bash
curl -i -X POST http://localhost:8085/api/v1/iso/generate \
-H "Content-Type: application/json" \
-d '{
    "transactionRef": "BE-ERR-001",
    "amount": -50.00,
    "currency": "USDOLLAR",
    "senderName": "John Doe",
    "senderIban": "US123",
    "receiverName": "Jane Smith",
    "receiverIban": "GB456"
}'
````

Expected Result: 400 Bad Request with a JSON body explaining that the amount must be positive and currency must be 3 letters.

### 🔴 Test 3: Validation Failure (Bad IBAN Format)

This tests the Regex pattern ^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$.

```Bash
curl -i -X POST http://localhost:8085/api/v1/iso/generate \
-H "Content-Type: application/json" \
-d '{
    "transactionRef": "BE-ERR-IBAN",
    "amount": 100.00,
    "currency": "EUR",
    "senderName": "Alice",
    "senderIban": "12345-NOT-AN-IBAN",
    "receiverName": "Bob",
    "receiverIban": "INVALID-99"
}'
```
Expected Result: 400 Bad Request specifically targeting the senderIban and receiverIban fields.

---

## License 
MIT License
