# Payment Service - HEMS

The Payment Service is a dedicated microservice for managing energy billing, rate management, and transaction tracking in the Home Energy Management System (HEMS).

## Architecture Overview

The Payment Service operates independently from the Site Manager Service and processes energy billing based on meter readings from the Simulator Service.

### Key Features

1. **Energy Rate Management** - Configure buy/sell rates for grid and solar
2. **Meter Reading Processing** - Consume and store daily meter readings
3. **Billing Calculation** - Automatic daily and monthly billing
4. **Transaction Tracking** - Detailed audit trail of all financial transactions
5. **Invoice Generation** - Monthly invoice creation with payment tracking
6. **Payment Recording** - Track user payments and billing status

## Data Models

### 1. EnergyRate
```
Tracks electricity pricing configuration:
- gridBuyRate: $/kWh user pays for grid imports
- solarSellRate: $/kWh user earns for solar exports
- fixedMonthlyCharge: Infrastructure/meter rental cost
- effectiveFrom/To: Rate validity period
```

### 2. MeterReading
```
Daily meter data from Simulator Service:
- siteId, ownerId: Site and owner references
- readingDate: Date of the reading
- solarYieldKwh: Total solar energy produced
- gridImportKwh: Energy consumed from grid
- gridExportKwh: Energy sold to grid
- homeUsageKwh: Total home consumption
- batteryChargeKwh/batteryDischargeKwh: Battery activity
- processed: Flag indicating billing completion
```

### 3. UserBilling
```
Monthly billing summary for each owner-site:
- billingMonth: Month in YYYY-MM format
- totalSolarYieldKwh: Aggregated solar production
- totalGridImportKwh: Total grid consumption
- totalGridExportKwh: Total grid export
- gridCharges: Monthly grid costs (gridImport × gridBuyRate)
- solarEarnings: Monthly solar credits (gridExport × solarSellRate)
- fixedCharges: Monthly infrastructure cost
- netBalance: solarEarnings - gridCharges - fixedCharges
- balanceStatus: CREDIT (earned) or DEBIT (owes)
- paymentStatus: PENDING, PAID, OVERDUE, etc.
```

### 4. Transaction
```
Individual transaction records for audit trail:
- transactionType: SOLAR_EARNING, GRID_CHARGE, FIXED_CHARGE, PAYMENT
- amount: Transaction amount in dollars
- referenceNumber: Unique transaction identifier
- status: COMPLETED, PENDING, FAILED, etc.
```

## Billing Logic

### Daily Processing
When a meter reading is received:
1. Extract energy values (solar yield, grid import/export)
2. Apply current energy rates
3. Calculate daily charges and earnings
4. Store transaction records

### Monthly Processing (Runs 1st of each month)
1. Aggregate all daily readings for the previous month
2. Calculate total charges and earnings
3. Apply fixed monthly charges
4. Determine balance status (CREDIT or DEBIT)
5. Generate UserBilling record
6. Create transaction records for audit trail
7. Mark readings as processed

### Balance Calculation
```
Monthly Balance = Solar Earnings - Grid Charges - Fixed Charges

Where:
- Solar Earnings = Total Grid Export × Solar Sell Rate
- Grid Charges = Total Grid Import × Grid Buy Rate
- Fixed Charges = Monthly Infrastructure Cost
```

## API Endpoints

### Billing Endpoints

#### Calculate Daily Billing
```
POST /api/v1/billing/calculate-daily
Content-Type: application/json

{
  "siteId": 1,
  "ownerId": 1,
  "solarYieldKwh": 45.50,
  "gridImportKwh": 12.30,
  "gridExportKwh": 18.75,
  "homeUsageKwh": 39.55,
  "batteryChargeKwh": 10.0,
  "batteryDischargeKwh": 8.5
}

Response:
{
  "siteId": 1,
  "ownerId": 1,
  "totalSolarYieldKwh": 45.50,
  "totalGridImportKwh": 12.30,
  "totalGridExportKwh": 18.75,
  "gridCharges": 1.48,
  "solarEarnings": 1.50,
  "netBalance": 0.02,
  "balanceStatus": "CREDIT",
  "message": "Daily Summary: Solar Export: 18.75 kWh (Earn: $1.50), Grid Import: 12.30 kWh (Pay: $1.48), Net: $0.02"
}
```

#### Calculate Monthly Billing
```
GET /api/v1/billing/calculate-monthly/{ownerId}/{siteId}/{yearMonth}

Example: /api/v1/billing/calculate-monthly/1/1/2024-03

Response:
{
  "siteId": 1,
  "ownerId": 1,
  "billingMonth": "2024-03",
  "totalSolarYieldKwh": 1250.50,
  "totalGridImportKwh": 450.30,
  "totalGridExportKwh": 580.75,
  "gridCharges": 54.04,
  "solarEarnings": 46.46,
  "fixedCharges": 10.00,
  "netBalance": -17.58,
  "balanceStatus": "DEBIT",
  "message": "Monthly Summary: ... (full summary)"
}
```

#### Get Billing History
```
GET /api/v1/billing/history/owner/{ownerId}
GET /api/v1/billing/history/site/{siteId}

Response: Array of UserBilling records
```

#### Get Billing Summary (Latest Month)
```
GET /api/v1/billing/summary/{ownerId}

Response: Latest UserBilling record
```

#### Record Payment
```
POST /api/v1/billing/payment
?ownerId=1&siteId=1&billingId=5&amount=50.00&referenceNumber=PAY-001

Response:
{
  "success": true,
  "message": "Payment recorded successfully",
  "transactionId": 100,
  "referenceNumber": "PAY-001"
}
```

#### Get Pending Payments
```
GET /api/v1/billing/pending-payments

Response: Array of UserBilling records with PENDING status
```

### Rate Management Endpoints

#### Get Current Rate
```
GET /api/v1/rates/current

Response:
{
  "id": 1,
  "gridBuyRate": 0.12,
  "solarSellRate": 0.08,
  "fixedMonthlyCharge": 10.00,
  "isActive": true,
  "effectiveFrom": "2024-01-01T00:00:00"
}
```

#### Create Energy Rate
```
POST /api/v1/rates/create
Content-Type: application/json

{
  "gridBuyRate": 0.12,
  "solarSellRate": 0.08,
  "fixedMonthlyCharge": 10.00,
  "description": "Standard residential rate"
}

Response:
{
  "success": true,
  "message": "Energy rate created successfully",
  "rateId": 2,
  "gridBuyRate": 0.12,
  "solarSellRate": 0.08,
  "fixedMonthlyCharge": 10.00
}
```

#### Update Energy Rate
```
PUT /api/v1/rates/update/{rateId}
Content-Type: application/json

{
  "gridBuyRate": 0.14,
  "solarSellRate": 0.09,
  "fixedMonthlyCharge": 12.00
}
```

#### Get All Historical Rates
```
GET /api/v1/rates/all

Response: Array of all active EnergyRate records
```

#### Deactivate Rate
```
DELETE /api/v1/rates/deactivate/{rateId}

Response:
{
  "success": true,
  "message": "Energy rate deactivated successfully"
}
```

## Integration Points

### With Simulator Service
- Consumes `MeterSnapshot` events via Kafka
- Stores as `MeterReading` entities
- Processes unprocessed readings for monthly billing

### With Site Manager Service
- Fetches site details and owner information
- Used for billing calculations

### With Admin Service
- Sends billing notifications
- Receives rate update commands
- Provides billing summaries

## Setup & Running

### Prerequisites
- Java 17+
- PostgreSQL 14+
- Kafka cluster (for meter data consumption)
- Spring Cloud Config (for configuration)

### Database Setup
```bash
# The schema.sql is automatically executed on startup
# Ensure database exists before starting service
```

### Running Locally
```bash
# Set environment variables
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=hems_payment
export DB_USER=postgres
export DB_PASSWORD=password
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Run the service
./gradlew bootRun
```

### Docker Deployment
```dockerfile
FROM openjdk:17-slim
COPY build/libs/payment-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```

## Configuration

### Energy Rates (Default)
- Grid Buy Rate: $0.12/kWh
- Solar Sell Rate: $0.08/kWh
- Fixed Monthly Charge: $10.00

Rates can be updated via the `/api/v1/rates/create` endpoint.

## Database Indexing

Optimized indexes for common queries:
- `idx_active_rate`: Quick rate lookups
- `idx_site_date`: Meter reading access by site and date
- `idx_owner_month`: Billing history queries
- `idx_billing_month`: Month-based billing searches
- `idx_transaction_type`: Transaction filtering

## Error Handling

Common error responses:

```
404 Not Found
- Billing record doesn't exist for month
- No active energy rate configured

400 Bad Request
- Invalid date format
- Missing required parameters
- Invalid amount values

500 Internal Server Error
- Database connection issues
- Kafka consumption failures
- Rate calculation errors
```

## Monitoring & Logging

- All transactions logged with owner ID and amount
- Monthly billing process scheduled for 1st of month
- Detailed logging for troubleshooting (see application.yaml)
- Metrics exposed on `/actuator/metrics`

## Future Enhancements

1. **Invoice PDF Generation** - Create downloadable invoices
2. **Payment Gateway Integration** - Stripe, PayPal support
3. **Rate Schedules** - Time-of-use (TOU) pricing
4. **Incentive Programs** - Rebates and bonus structures
5. **Billing Disputes** - Dispute resolution workflow
6. **Export Functionality** - CSV/Excel billing exports
7. **Mobile Notifications** - SMS/Push alerts for payments
8. **Analytics** - Dashboard with billing trends

## Support

For issues or questions, contact the development team or open an issue in the repository.
