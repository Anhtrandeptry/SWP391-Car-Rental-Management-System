-- Add orderCode and paymentUrl columns to bookings table for PayOS integration
-- orderCode: Globally unique identifier for PayOS (NOT the same as booking_id)
-- paymentUrl: Cached PayOS checkout URL to avoid duplicate payment creation

ALTER TABLE bookings ADD COLUMN IF NOT EXISTS order_code BIGINT UNIQUE;
ALTER TABLE bookings ADD COLUMN IF NOT EXISTS payment_url VARCHAR(500);

-- Create index for faster lookup by orderCode (used in webhook processing)
CREATE INDEX IF NOT EXISTS idx_bookings_order_code ON bookings(order_code);

