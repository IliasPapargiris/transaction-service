package com.okto.transactionservice.util;

public class JsonExamples {

    // Transaction Ingestion Request
    public static final String TRANSACTION_INGEST_REQUEST_JSON = """
                {
                    "userId": 1,
                    "countryCode": "GR",
                    "amount": 150.75,
                    "currency": "EUR",
                    "timestamp": "2025-04-30T10:30:00Z"
                }
            """;

    // Aggregated Transactions by Country (per user)
    public static final String USER_AGGREGATES_RESPONSE_JSON = """
                [
                    {
                        "countryCode": "GR",
                        "transactionCount": 5,
                        "totalAmount": 782.50,
                        "averageAmount": 156.50
                    },
                    {
                        "countryCode": "US",
                        "transactionCount": 3,
                        "totalAmount": 450.00,
                        "averageAmount": 150.00
                    }
                ]
            """;

    // Daily Volume Summary
    public static final String DAILY_SUMMARY_RESPONSE_JSON = """
                {
                    "date": "2025-04-30",
                    "transactionCount": 12,
                    "totalAmount": 3420.50
                }
            """;

    // --- Error: User Not Found (404)
    public static final String USER_NOT_FOUND_JSON = """
                {
                    "error": "User Not Found",
                    "message": "No user found with ID 42",
                    "status": 404,
                    "timestamp": "2025-04-30T12:30:00.000Z"
                }
            """;

    // --- Error: Missing Request Parameter (400)
    public static final String MISSING_PARAMETER_ERROR_JSON = """
                {
                    "error": "Missing Request Parameter",
                    "message": "Required request parameter 'date' is not present",
                    "status": 400,
                    "timestamp": "2025-04-30T12:45:00.000Z"
                }
            """;

    // --- Error: Validation Failed (400)
    public static final String VALIDATION_ERROR_JSON = """
                {
                    "error": "Validation Failed",
                    "message": "amount: must be greater than or equal to 0.01",
                    "status": 400,
                    "timestamp": "2025-04-30T13:00:00.000Z"
                }
            """;



    // --- Error: Invalid Credentials (401) ---
    public static final String INVALID_CREDENTIALS_JSON = """
                {
                    "error": "Unauthorized",
                    "message": "Invalid credentials",
                    "status": 401,
                    "timestamp": "2025-05-05T12:00:00Z"
                }
            """;


}
