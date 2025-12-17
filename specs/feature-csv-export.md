# Feature Specification: CSV Export for Meter Readings

## Goal
Allow administrators to download a CSV export of all electricity readings for a specific smart meter.

## Technical Constraints (Strict)
1.  **No New Dependencies:** Do NOT add `apache-commons`, `opencsv`, or any external CSV libraries. Use standard Java `PrintWriter` and `String.format`.
2.  **Memory Safety:** The meter might have 100,000+ readings. Do NOT load them all into a `List` in memory. Stream the data directly to the HTTP response if possible, or use a buffered approach.
3.  **Formatting:**
    * **Time:** Must be formatted as `yyyy-MM-dd HH:mm` (User-friendly, in UTC).
    * **Reading:** Numeric value.
    * **Header:** `Date,Amount`

## Implementation Details
* **URL:** `GET /readings/export/{smartMeterId}`
* **Response Content-Type:** `text/csv`
* **Error Handling:** If the Smart Meter ID is not found, return `404 Not Found`.

## Example Output
Date,Amount
2023-10-25 14:30,0.045
2023-10-25 15:00,0.052
