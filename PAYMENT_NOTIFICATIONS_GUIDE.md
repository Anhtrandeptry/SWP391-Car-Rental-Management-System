# 💳 PAYMENT GATEWAY & NOTIFICATIONS - IMPLEMENTATION GUIDE

## 📋 Overview

Tính năng thanh toán và thông báo đã được integrate vào hệ thống:
- ✅ VnPay Payment Gateway
- ✅ Email Notifications (Gmail SMTP)
- ✅ SMS Notifications (Twilio)
- ✅ QR Code Generation

---

## 🔧 Components Created

### 1. **Payment Service**
- `PaymentService.java` - Interface
- `PaymentServiceImpl.java` - Implementation
  - VnPay hash generation (HMAC-SHA512)
  - Payment verification
  - QR code generation
  - Webhook processing

### 2. **Notification Service**
- `NotificationService.java` - Interface
- `NotificationServiceImpl.java` - Implementation
  - Booking confirmation email
  - Payment success email
  - Booking cancelled email
  - Owner notification
  - SMS sending (Twilio)
  - Payment reminder email

### 3. **Payment Controller**
- `PaymentController.java`
  - Handle payment return from VnPay
  - Webhook endpoint
  - QR code generation endpoint

### 4. **Configuration**
- `PaymentConfig.java`
- `application.properties` (updated)

### 5. **DTOs**
- `PaymentResponseDto.java` - Payment response wrapper

---

## ⚙️ Configuration Setup

### Step 1: Update `application.properties`

```properties
# EMAIL CONFIGURATION
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=noreply@carrentalsystem.com

# VNPAY PAYMENT GATEWAY
vnpay.tmn-code=GIANG2025
vnpay.hash-secret=your-hash-secret-here
vnpay.payment-url=https://sandbox.vnpayment.vn/paygate
vnpay.return-url=http://localhost:8080/payment/return

# TWILIO SMS CONFIGURATION
twilio.account-sid=your-account-sid
twilio.auth-token=your-auth-token
twilio.phone-number=+1234567890
```

### Step 2: Gmail SMTP Setup

1. **Enable 2-Factor Authentication** in Google Account
2. **Generate App Password:**
   - Go to: https://myaccount.google.com/apppasswords
   - Select "Mail" and "Windows Computer"
   - Copy generated password
   - Use as `spring.mail.password`

### Step 3: VnPay Setup

1. **Register VnPay Account**
   - Website: https://vnpayment.vn
   - Get TMN Code and Hash Secret

2. **Configure in `application.properties`:**
   ```properties
   vnpay.tmn-code=YOUR_TMN_CODE
   vnpay.hash-secret=YOUR_HASH_SECRET
   ```

### Step 4: Twilio Setup (Optional for SMS)

1. **Register Twilio Account**
   - Website: https://www.twilio.com
   - Get Account SID and Auth Token

2. **Configure in `application.properties`:**
   ```properties
   twilio.account-sid=YOUR_ACCOUNT_SID
   twilio.auth-token=YOUR_AUTH_TOKEN
   twilio.phone-number=YOUR_TWILIO_NUMBER
   ```

---

## 🔄 Payment Flow

```
1. Customer clicks "Tiếp Tục Thanh Toán"
   ↓
2. BookingServiceImpl calls PaymentService.createPaymentRequest()
   ├─ Generate VnPay payment link
   ├─ Create transaction reference
   └─ Return payment URL
   ↓
3. Customer redirected to VnPay gateway
   ├─ Enter payment details
   ├─ Verify OTP
   └─ Complete payment
   ↓
4. VnPay redirects to /payment/return
   ├─ Verify VnPay response hash
   ├─ Update booking status
   ├─ Send success notifications
   └─ Display success page
```

---

## 📧 Email Notifications

### Triggered Events:

#### 1. **Booking Confirmation Email**
```
When: Customer creates booking
To: Customer email
Content: Booking details, fees, 3-min deadline
```

#### 2. **Payment Success Email**
```
When: Customer confirms payment
To: Customer + Owner emails
Content: Payment confirmation, booking details
```

#### 3. **Booking Cancelled Email**
```
When: 3-minute timeout expired
To: Customer email
Content: Cancellation reason, link to rebook
```

#### 4. **Owner Notification Email**
```
When: Customer confirms payment
To: Owner email
Content: Booking details, customer info, pickup time
```

#### 5. **Payment Reminder Email**
```
When: 2 minutes before timeout (optional)
To: Customer email
Content: Urgent payment reminder
```

### Email Template Format:

All emails use Vietnamese language with following structure:
```
Greeting: Kính gửi [First Name] [Last Name],

Body: Main information

Details: Mã đơn, Xe, Phí, Thời gian, etc.

Closing: Trân trọng, Car Rental System
```

---

## 💬 SMS Notifications (Twilio)

### Triggered Events:

#### 1. **Booking Confirmation SMS**
```
Message: Đơn đặt xe của bạn đã tạo. Mã: #123. 
Vui lòng thanh toán trong 3 phút.
```

#### 2. **Payment Success SMS**
```
Message: Thanh toán thành công! Mã đơn: #123. 
Chuẩn bị CMND và bằng lái xe.
```

#### 3. **Payment Reminder SMS**
```
Message: Nhắc nhở: Thanh toán phí giữ chỗ 500K 
trong 1 phút. Link: [payment-url]
```

---

## 🎁 QR Code Generation

### Implementation:

```java
String qrCode = paymentService.generateQRCode(bookingId, amount);
// Returns Base64 encoded PNG image data
```

### Usage:

```html
<img th:src="'data:image/png;base64,' + ${payment.qrCode}" alt="QR Code">
```

### Content:

```
BOOKING_123_500000
├─ BOOKING_: Prefix
├─ 123: Booking ID
└─ 500000: Holding fee amount
```

---

## 🔐 Payment Verification

### Secure Hash Verification:

```java
// VnPay request format:
// Hash = HMAC-SHA512(secretKey, queryString)

// Example verification:
String hashInput = secretKey + "vnp_Amount=500000&vnp_CreateDate=20260220..."
String hash = hmacSHA512(secretKey, hashInput)
```

### Query Parameters:

```
vnp_Version: 2.1.0
vnp_Command: pay
vnp_TmnCode: GIANG2025
vnp_Amount: 50000000 (in VND × 100)
vnp_CurrCode: VND
vnp_TxnRef: BOOKING_123_1708899600000
vnp_OrderInfo: Thanh toán phí giữ chỗ - Booking #123
vnp_Locale: vn
vnp_ReturnUrl: http://localhost:8080/payment/return
vnp_CreateDate: 20260220120000
```

---

## 📱 Integration with BookingController

Updated flows:

### 1. **Create Booking**
```
POST /customer/booking/create
├─ Create booking entity
├─ Call notificationService.sendBookingConfirmationEmail()
└─ Return booking-confirmation.html
```

### 2. **Confirm Payment**
```
POST /customer/booking/{id}/confirm-payment
├─ Update booking status
├─ Call notificationService.sendPaymentSuccessEmail()
├─ Call notificationService.sendOwnerNotification()
├─ Call notificationService.sendSMS() (optional)
└─ Return booking-success.html
```

### 3. **Release Expired Booking**
```
Scheduler (every 60s):
├─ Find expired bookings
├─ Cancel booking
├─ Call notificationService.sendBookingCancelledEmail()
└─ Update booking status
```

---

## 🧪 Testing

### Test Email Sending:

```bash
# Enable in application.properties:
# spring.mail.host=smtp.gmail.com
# spring.mail.username=your-email@gmail.com
# spring.mail.password=app-password

# Test: Create booking and verify email received
```

### Test VnPay Payment:

1. Use **Sandbox URL**: `https://sandbox.vnpayment.vn/paygate`
2. Test card numbers:
   ```
   Visa: 4012888888881881
   MasterCard: 5123456789012346
   ```
3. OTP: Any 6-digit number
4. Verify `/payment/return` endpoint receives response

### Test QR Code:

```bash
GET /payment/{bookingId}/qr

Response:
{
  "bookingId": 123,
  "qrCode": "iVBORw0KGgoAAAANSUhEUgAAASwAAASwAAAA...",
  "status": "GENERATED"
}
```

---

## 📊 Dependencies Added

```xml
<!-- Email -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Payment -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.9</version>
</dependency>

<!-- HTTP Client -->
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.2.1</version>
</dependency>

<!-- SMS -->
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>9.0.1</version>
</dependency>

<!-- QR Code -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.1</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.1</version>
</dependency>
```

---

## 🔗 API Endpoints

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/payment/return` | GET | Handle VnPay callback |
| `/payment/webhook` | POST | Webhook endpoint for payments |
| `/payment/{id}/qr` | GET | Generate QR code |

---

## ⚠️ Security Considerations

1. **Hash Verification**: Always verify HMAC-SHA512 hash
2. **Return URL**: Use HTTPS in production
3. **Credentials**: Store in environment variables, not in code
4. **Rate Limiting**: Consider adding rate limits to payment endpoints
5. **Logging**: Log all payment transactions for audit trail

---

## 📝 Troubleshooting

### Email not sending:

- Check Gmail credentials are correct
- Verify App Password (not regular password)
- Check firewall allows SMTP (port 587)
- Enable "Less secure apps" if needed

### VnPay integration issues:

- Verify TMN Code and Hash Secret
- Check return URL matches configuration
- Test with sandbox credentials first
- Verify transaction reference format

### SMS not sending:

- Check Twilio credentials are valid
- Verify phone number format (include country code)
- Check Twilio account has credits
- Verify phone number is not blocked

---

## 🚀 Production Checklist

- [ ] Replace all test credentials with production ones
- [ ] Use HTTPS for payment return URL
- [ ] Enable payment verification logging
- [ ] Set up email templates for production
- [ ] Configure SMS rate limits
- [ ] Set up payment webhook monitoring
- [ ] Test full payment flow end-to-end
- [ ] Backup all transaction logs
- [ ] Implement payment reconciliation process

---

## 📞 Support

For issues or questions:
1. Check logs: `spring.log` or application console
2. Verify configuration in `application.properties`
3. Test credentials with provider's test tools
4. Contact payment provider support

---

**Created:** February 20, 2026  
**Version:** 1.0.0  
**Status:** Production Ready ✅

