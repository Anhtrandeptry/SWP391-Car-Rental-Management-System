# ⚙️ SETUP GUIDE - PAYMENT & NOTIFICATIONS

## 🎯 Quick Setup (15 Minutes)

### Step 1: Gmail SMTP Setup (5 min)

#### 1.1 Enable 2-Factor Authentication
```
1. Go to https://myaccount.google.com/security
2. Enable "2-Step Verification"
3. Choose verification method
```

#### 1.2 Generate App Password
```
1. Go to https://myaccount.google.com/apppasswords
2. Select "Mail" and "Windows Computer"
3. Google generates 16-character password
4. Copy the password
```

#### 1.3 Update application.properties
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=xxxx xxxx xxxx xxxx
spring.mail.from=noreply@carrentalsystem.com
```

### Step 2: VnPay Setup (5 min)

#### 2.1 Register VnPay Account
```
1. Go to https://vnpayment.vn
2. Sign up and verify email
3. Wait for account approval (24-48 hours)
```

#### 2.2 Get Credentials
```
After approval:
1. Login to VnPay dashboard
2. Find "API Information" section
3. Copy TMN Code (Merchant Code)
4. Copy Hash Secret (MD5 Secret)
```

#### 2.3 Update application.properties
```properties
vnpay.tmn-code=YOUR_TMN_CODE
vnpay.hash-secret=YOUR_HASH_SECRET
vnpay.payment-url=https://sandbox.vnpayment.vn/paygate
vnpay.return-url=http://localhost:8080/payment/return
```

#### 2.4 Configure Return URL in VnPay Dashboard
```
1. Go to VnPay Dashboard
2. Settings → Return URLs
3. Add: http://localhost:8080/payment/return
4. Save
```

### Step 3: Twilio Setup (5 min - Optional)

#### 3.1 Register Twilio Account
```
1. Go to https://www.twilio.com/console
2. Sign up with email
3. Verify phone number
```

#### 3.2 Get Credentials
```
1. Dashboard → Account
2. Copy Account SID
3. Copy Auth Token
4. Copy Twilio Phone Number (or buy one)
```

#### 3.3 Update application.properties
```properties
twilio.account-sid=YOUR_ACCOUNT_SID
twilio.auth-token=YOUR_AUTH_TOKEN
twilio.phone-number=+1234567890
```

---

## ✅ VERIFICATION CHECKLIST

### Email Setup
```bash
# Test 1: Compile project (should have no errors)
mvn clean compile

# Test 2: Start application
mvn spring-boot:run

# Test 3: Create booking and check email
# Expected: Confirmation email arrives in 1-2 minutes
```

### VnPay Setup
```bash
# Test 1: Create booking
# Expected: Payment link generated without errors

# Test 2: Access payment page
# Expected: QR code displays, timer shows 3:00

# Test 3: Click payment button
# Expected: Redirected to VnPay sandbox (https://sandbox.vnpayment...)
```

### Test Card Numbers (VnPay Sandbox)
```
Visa:
Card: 4012888888881881
Exp: 12/20
OTP: Any 6-digit number

MasterCard:
Card: 5123456789012346
Exp: 12/20
OTP: Any 6-digit number
```

---

## 📝 Configuration Template

Create file: `application-production.properties`

```properties
# ===============================
# SERVER
# ===============================
server.port=8080

# ===============================
# DATASOURCE
# ===============================
spring.datasource.url=jdbc:mysql://your-host:3306/crms_db
spring.datasource.username=your-user
spring.datasource.password=your-password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ===============================
# JPA / HIBERNATE
# ===============================
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# ===============================
# EMAIL
# ===============================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.from=noreply@carrentalsystem.com

# ===============================
# VNPAY
# ===============================
vnpay.tmn-code=${VNPAY_TMN_CODE}
vnpay.hash-secret=${VNPAY_HASH_SECRET}
vnpay.payment-url=https://api.vnpayment.vn/paygate
vnpay.return-url=https://your-domain.com/payment/return

# ===============================
# TWILIO
# ===============================
twilio.account-sid=${TWILIO_ACCOUNT_SID}
twilio.auth-token=${TWILIO_AUTH_TOKEN}
twilio.phone-number=${TWILIO_PHONE_NUMBER}

# ===============================
# LOGGING
# ===============================
logging.level.root=INFO
logging.level.fpt.swp391=DEBUG
logging.file.name=logs/application.log
```

---

## 🚀 Deployment Checklist

### Before Going Live
- [ ] Gmail credentials verified
- [ ] VnPay account approved & configured
- [ ] Test card payments successful
- [ ] Email templates reviewed
- [ ] Return URL configured in VnPay
- [ ] Database backed up
- [ ] HTTPS enabled in production
- [ ] SSL certificate installed

### Environment Variables (Production)
```bash
export DB_URL=jdbc:mysql://prod-host:3306/crms_db
export SPRING_MAIL_USERNAME=your-email@gmail.com
export SPRING_MAIL_PASSWORD=your-app-password
export VNPAY_TMN_CODE=your-tmn-code
export VNPAY_HASH_SECRET=your-hash-secret
export TWILIO_ACCOUNT_SID=your-sid
export TWILIO_AUTH_TOKEN=your-token
```

### Start with Production Profile
```bash
java -jar car-rental-system.jar --spring.profiles.active=production
```

---

## 🆘 Troubleshooting

### Email Not Sending

**Problem:** "Authentication failed"
```
Solution:
1. Verify Gmail credentials are correct
2. Check if using app-password (not regular password)
3. Ensure 2-Factor Authentication is enabled
4. Check firewall allows port 587
```

**Problem:** "TLS error"
```
Solution:
1. Verify STARTTLS is enabled in properties
2. Check SMTP port is 587 (not 25 or 465)
3. Test with telnet: telnet smtp.gmail.com 587
```

### VnPay Integration Issues

**Problem:** "Invalid hash"
```
Solution:
1. Verify TMN Code and Hash Secret match VnPay account
2. Check query string formatting
3. Ensure HMAC-SHA512 implementation is correct
```

**Problem:** "Return URL not matching"
```
Solution:
1. Check URL in VnPay dashboard matches return-url in config
2. Ensure protocol (http/https) is correct
3. Verify domain name is registered in VnPay
```

### Payment Verification Failed

**Problem:** "Secure hash mismatch"
```
Solution:
1. Verify all response parameters included
2. Check parameter order in hash calculation
3. Ensure hash secret is exact match
4. Check for extra spaces or encoding issues
```

---

## 📊 Testing Payments

### Complete Test Flow

```
1. Register test account
   └─ URL: http://localhost:8080/auth/register

2. Create booking
   └─ URL: http://localhost:8080/customer/booking

3. Review fees
   └─ Should show: 500K + 5M + calculated rental

4. Go to payment
   └─ Should show QR code and 3-min timer

5. Click "Thanh Toán Xong"
   └─ Should redirect to VnPay sandbox

6. Enter test card
   ├─ Card: 4012888888881881
   ├─ Exp: 12/20
   └─ OTP: 123456

7. Verify payment
   └─ Should return to /payment/return with success code

8. Check success page
   └─ Should show confirmation message

9. Check email
   └─ Should receive payment confirmation email
```

---

## 💡 Tips

1. **Test Mode:** Always use sandbox URLs first
2. **Logs:** Enable DEBUG level to see detailed logs
3. **Emails:** Check spam folder for test emails
4. **Time Zone:** Ensure server time zone matches Vietnam (GMT+7)
5. **Database:** Keep test data separate from production

---

## 📞 Support

If you encounter issues:

1. **Check Logs:**
   ```bash
   tail -f logs/application.log
   ```

2. **Verify Configuration:**
   ```bash
   grep "spring.mail\|vnpay\|twilio" application.properties
   ```

3. **Test Email:**
   ```java
   @Test
   public void testEmailSending() {
       SimpleMailMessage msg = new SimpleMailMessage();
       msg.setTo("test@example.com");
       msg.setSubject("Test");
       msg.setText("Test email");
       mailSender.send(msg);
   }
   ```

4. **Test Payment:**
   ```
   Use sandbox: https://sandbox.vnpayment.vn/paygate
   Test card: 4012888888881881
   ```

---

**Setup Time:** ~15 minutes  
**Configuration Complexity:** Low  
**Testing:** Required before production  
**Support:** Contact service providers if issues persist

---

**Created:** February 20, 2026  
**Version:** 1.0.0

