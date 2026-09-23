# Finance API

The finance ledger is exposed below `/api/finance`.

## Endpoints

- `GET /api/finance/summary`: current and comparison metrics. Dates use `yyyy-MM-dd`; omitted dates default to the current month and the immediately preceding comparable period.
- `GET /api/finance/transactions`: paginated transactions. Supported filters are `startDate`, `endDate`, `type`, `category`, `status`, `paymentMethod`, `partyName`, and `search`.
- `GET /api/finance/transactions/{id}`
- `POST /api/finance/transactions`
- `PUT /api/finance/transactions/{id}`
- `PATCH /api/finance/transactions/{id}/void`: preserves the row and writes an audit record.
- `POST /api/finance/transactions/{id}/payments`: records a full or partial payment.
- `GET /api/finance/due?date=2026-09-23`: returns overdue, due-today, and upcoming transactions.
- `GET /api/finance/categories`

Amounts are stored as `numeric(19,4)`. Currency is currently explicitly supported as `LKR`. Income transaction amounts represent revenue; payment rows represent cash receipts/payments and are not added to revenue again.

## Example

```json
{
  "transactionDate": "2026-09-23",
  "dueDate": "2026-09-30",
  "type": "EXPENSE",
  "category": "RAW_MATERIALS",
  "description": "Resin and pellets",
  "referenceNumber": "PO-284",
  "purchaseOrderId": 284,
  "partyName": "Supplier name",
  "paymentMethod": "BANK_TRANSFER",
  "amount": 162500,
  "currency": "LKR",
  "status": "PAID",
  "notes": "Monthly raw material purchase"
}
```

## Authentication

The existing application does not currently configure Spring Security or an authentication provider. Finance create/update/payment/void operations record `Principal.getName()` when the host application supplies an authenticated principal and otherwise use `system`. Before exposing this module outside the trusted application boundary, wire the project authentication provider and enforce role checks for read, write, and void operations at the controller/security configuration layer.

Flyway migration `V2__finance_ledger.sql` creates the transaction, payment, audit, indexes, and amount constraints.
