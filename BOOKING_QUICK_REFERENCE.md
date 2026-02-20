# 🎯 Tóm Tắt Nhanh - Tính Năng Thuê Xe

## ✅ Đã Hoàn Thành

### 1. **Entities**
- ✅ `Car.java` - Thông tin xe (carId, owner, name, pricePerDay, status...)
- ✅ `Booking.java` - Thông tin đặt xe (bookingId, customer, car, startDate, endDate, fees...)

### 2. **Enums**
- ✅ `BookingStatus.java` - PENDING, PAYMENT_PENDING, CONFIRMED, CANCELLED...
- ✅ `PaymentStatus.java` - UNPAID, PARTIALLY_PAID, PAID
- ✅ `CarStatus.java` - PENDING, APPROVED, AVAILABLE, BOOKED, DISABLED...
- ✅ `FuelType.java` - PETROL, ELECTRIC, DIESEL, HYBRID

### 3. **DTOs (Data Transfer Objects)**
- ✅ `CreateBookingRequest.java` - Request tạo booking
- ✅ `BookingConfirmationDto.java` - Response xác nhận đặt xe
- ✅ `PaymentInfoDto.java` - Response thông tin thanh toán
- ✅ `ApiResponse.java` - Response wrapper chung

### 4. **Repositories**
- ✅ `CarRepository.java` - CRUD xe
- ✅ `BookingRepository.java` - CRUD booking + query kiểm tra trùng lặp

### 5. **Mappers**
- ✅ `BookingMapper.java` - MapStruct convert Booking → BookingConfirmationDto

### 6. **Services**
- ✅ `BookingService.java` - Interface
- ✅ `BookingServiceImpl.java` - Implementation (tính phí, kiểm tra khả dụng, xác nhận thanh toán...)
- ✅ `BookingScheduler.java` - Scheduled task (auto-release booking quá hạn)

### 7. **Controllers**
- ✅ `BookingController.java` - REST API endpoints

### 8. **Configuration**
- ✅ `@EnableScheduling` trong `CarRentalSystemApplication.java`

---

## 📊 Phí Tính Toán Tự Động

| Phí | Số Tiền | Khi Thanh Toán |
|-----|---------|----------------|
| Phí giữ chỗ (holding_fee) | 500,000 VND | **Ngay lập tức (3 phút)** |
| Phí thế chấp (deposit) | 5,000,000 VND | Khi nhận xe |
| Phí thuê (rental_fee) | pricePerDay × số ngày | Khi nhận xe |

**Công thức:**
```
totalAmount = rentalFee + holdingFee + depositAmount

Ví dụ:
- Xe giá 1.5M/ngày, thuê 1 ngày
- rentalFee = 1,500,000
- holdingFee = 500,000
- depositAmount = 5,000,000
- totalAmount = 7,000,000
```

---

## 🔗 API Endpoints

```
POST   /api/v1/bookings
       → Tạo booking (PAYMENT_PENDING)
       → Response: BookingConfirmationDto (totalAmount, fees, paymentDeadline)

GET    /api/v1/bookings/{bookingId}/payment-info
       → Lấy thông tin thanh toán + QR code

POST   /api/v1/bookings/{bookingId}/confirm-payment
       → Xác nhận thanh toán → Status = CONFIRMED
```

---

## ⏰ Timeout Logic (3 Phút)

```
Timeline:
├─ T+0min: Booking created (status = PAYMENT_PENDING)
├─ T+3min: paymentDeadline
├─ T+3min 1sec: Scheduler kiểm tra → Nếu vẫn UNPAID → CANCELLED
└─ Xe lại available cho khách khác

Scheduler:
├─ Chạy mỗi 60 giây
├─ Tìm: status=PAYMENT_PENDING AND paymentStatus=UNPAID AND paymentDeadline<NOW
└─ Action: Set status=CANCELLED
```

---

## 🚗 Kiểm Tra Tính Khả Dụng (Car Availability)

```java
// Query: Kiểm tra có booking trùng lặp không?
SELECT COUNT(b) FROM Booking b 
WHERE car.carId = ? 
AND status = CONFIRMED
AND ((b.startDate < :endDate AND b.endDate > :startDate))

// Nếu COUNT = 0 → Xe available ✅
// Nếu COUNT > 0 → Xe booked ❌
```

---

## 📝 Ví Dụ Request/Response

### 1️⃣ POST /api/v1/bookings

**Request:**
```json
{
  "carId": 1,
  "startDate": "2026-02-25T09:00:00",
  "endDate": "2026-02-26T09:00:00",
  "pickupLocation": "123 Nguyen Hue, HCMC"
}
```

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "bookingId": 1,
    "carId": 1,
    "carName": "Toyota Camry",
    "startDate": "2026-02-25T09:00:00",
    "endDate": "2026-02-26T09:00:00",
    "pickupLocation": "123 Nguyen Hue, HCMC",
    "rentalFee": 1500000,
    "depositAmount": 5000000,
    "holdingFee": 500000,
    "totalAmount": 7000000,
    "paymentDeadline": "2026-02-25T09:03:00",
    "status": "PAYMENT_PENDING"
  },
  "timestamp": "2026-02-20T10:30:00"
}
```

### 2️⃣ GET /api/v1/bookings/1/payment-info

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "bookingId": 1,
    "holdingFee": 500000,
    "depositAmount": 5000000,
    "rentalFee": 1500000,
    "totalAmount": 7000000,
    "qrCodeUrl": "data:image/png;base64,..."
  },
  "timestamp": "2026-02-20T10:31:00"
}
```

### 3️⃣ POST /api/v1/bookings/1/confirm-payment

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Success",
  "data": "Payment confirmed",
  "timestamp": "2026-02-20T10:32:00"
}
```

---

## 🏗️ Cấu Trúc Folder Sau Implementation

```
src/main/java/fpt/swp391/carrentalsystem/
├── entity/
│   ├── Car.java ✅
│   ├── Booking.java ✅
│   └── User.java (có sẵn)
├── enums/
│   ├── CarStatus.java ✅
│   ├── BookingStatus.java ✅
│   ├── PaymentStatus.java ✅
│   ├── FuelType.java ✅
│   └── ... (Gender, Role, UserStatus)
├── dto/
│   ├── request/
│   │   ├── CreateBookingRequest.java ✅
│   │   └── ... (LoginRequest, RegisterRequest)
│   └── response/
│       ├── ApiResponse.java ✅
│       ├── BookingConfirmationDto.java ✅
│       ├── PaymentInfoDto.java ✅
│       └── ... (khác)
├── repository/
│   ├── CarRepository.java ✅
│   ├── BookingRepository.java ✅
│   └── UserRepository.java (có sẵn)
├── mapper/
│   ├── BookingMapper.java ✅
│   └── ... (UserMapper, ProfileMapper)
├── service/
│   ├── BookingService.java ✅
│   ├── BookingServiceImpl.java ✅
│   ├── BookingScheduler.java ✅
│   └── ... (AuthService, ProfileService...)
└── controller/
    ├── customer/
    │   ├── BookingController.java ✅
    │   └── CustomerController.java (có sẵn)
    └── ... (admin, common, owner)
```

---

## 🔒 Xác Thực & Phân Quyền

```
Endpoints:
├─ /api/v1/bookings              → @PostMapping (Customer)
├─ /api/v1/bookings/{id}/...     → @GetMapping (Public)
└─ /api/v1/bookings/{id}/...     → @PostMapping (Customer)

Authentication:
├─ Từ Security context (authentication.getPrincipal())
├─ Extract userId từ UserDetails
└─ Pass vào service
```

---

## 🧪 Test Endpoints (cURL)

```bash
# 1. Tạo booking
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "carId": 1,
    "startDate": "2026-02-25T09:00:00",
    "endDate": "2026-02-26T09:00:00",
    "pickupLocation": "123 Nguyen Hue, HCMC"
  }'

# Response: bookingId = 1

# 2. Lấy thông tin thanh toán
curl http://localhost:8080/api/v1/bookings/1/payment-info

# 3. Xác nhận thanh toán
curl -X POST http://localhost:8080/api/v1/bookings/1/confirm-payment
```

---

## ⚙️ Cấu Hình Cần Thiết

### `application.properties` (Đã có)
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/crms_db
spring.jpa.hibernate.ddl-auto=update
```

### `CarRentalSystemApplication.java` (Cập nhật)
```java
@SpringBootApplication
@EnableScheduling  ✅ ADDED
public class CarRentalSystemApplication {
    // ...
}
```

### `pom.xml` (Đã có)
```xml
<!-- MapStruct -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- Spring Boot Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## 🎯 Luồng Xử Lý Booking

```
┌──────────────────┐
│  Customer          │
│  Chọn xe + ngày    │
└────────┬──────────┘
         │
         ↓ POST /api/v1/bookings
┌──────────────────────────────────┐
│ BookingController.createBooking() │
└────────┬─────────────────────────┘
         │
         ↓ bookingService.createBooking(...)
┌──────────────────────────────────────┐
│ BookingServiceImpl.createBooking()    │
│                                      │
│ 1. Fetch User (customer)             │
│ 2. Fetch Car                         │
│ 3. Validate dates                    │
│ 4. Check car availability            │
│ 5. Calculate fees:                   │
│    - rentalFee = pricePerDay × days │
│    - holdingFee = 500K               │
│    - depositAmount = 5M              │
│ 6. Create Booking entity             │
│ 7. Set paymentDeadline = NOW + 3min │
│ 8. Set status = PAYMENT_PENDING      │
│ 9. Save to DB                        │
│ 10. Map to DTO (BookingMapper)       │
└────────┬────────────────────────────┘
         │
         ↓ Response 200 OK
┌──────────────────────────┐
│ BookingConfirmationDto   │
│ - bookingId: 1           │
│ - totalAmount: 7000000   │
│ - status: PAYMENT_PENDING│
│ - deadline: +3min        │
└──────────┬───────────────┘
           │
           ↓ GET /api/v1/bookings/1/payment-info
┌──────────────────────────────────┐
│ PaymentInfoDto (+ QR code)        │
│ - holdingFee: 500K                │
│ - depositAmount: 5M               │
│ - rentalFee: 1.5M                 │
│ - qrCodeUrl: "data:image/..."     │
└──────────┬──────────────────────┘
           │
           ↓ Customer scans QR
┌──────────────────────────┐
│ Payment gateway           │
│ VnPay/Momo (Future)      │
└──────────┬───────────────┘
           │
           ↓ POST /api/v1/bookings/1/confirm-payment
┌──────────────────────────────────┐
│ BookingServiceImpl.confirmPayment │
│                                  │
│ 1. Fetch Booking                 │
│ 2. Set paymentStatus = PAID      │
│ 3. Set status = CONFIRMED        │
│ 4. Save to DB                    │
│ 5. Send notifications:           │
│    - To customer ✉️              │
│    - To owner ✉️                 │
└──────────┬──────────────────────┘
           │
           ↓ Response 200 OK
┌──────────────────────────┐
│ "Payment confirmed"       │
│ Booking completed! ✅     │
└──────────────────────────┘

⏰ Background Scheduler (every 60s):
   IF (status = PAYMENT_PENDING 
       AND paymentStatus = UNPAID 
       AND paymentDeadline < NOW)
   THEN status = CANCELLED
```

---

## 💡 Điểm Highlight

✅ **Auto-calculation:** Phí tính toán tự động dựa trên ngày thuê  
✅ **MapStruct:** Zero-config entity to DTO mapping  
✅ **Scheduled Task:** Auto-release booking quá hạn  
✅ **Availability Check:** Kiểm tra trùng lặp thời gian thông minh  
✅ **RESTful API:** Clean, standard API design  
✅ **Validation:** Request/response validation  
✅ **Transaction:** Tự động rollback nếu có lỗi  
✅ **Logging:** SLF4J logging cho debug  

---

## 📌 Lưu Ý Quan Trọng

1. **User ID Type:** Hiện tại sử dụng `Long` (user.id), hãy điều chỉnh nếu project dùng `Integer`
2. **Authentication:** Controller extract userId từ `authentication.getPrincipal()`. Cần bảo đảm UserDetailsService set username = userId
3. **QR Code:** Hiện tại chỉ placeholder. Cần integrate library như `zxing` để generate QR thực tế
4. **Notifications:** Cần implement Email/SMS service để gửi thông báo thực tế
5. **Payment Gateway:** Cần integrate VnPay/Momo API (future feature)

---

## 🚀 Next Steps

1. **Test API:** Chạy project và test endpoints với Postman
2. **Database:** Verify tables được tạo (cars, bookings)
3. **Scheduler:** Monitor logs để xem scheduler chạy
4. **Frontend:** Build UI để chọn xe & ngày
5. **Payment Integration:** Kết nối VnPay/Momo
6. **Notifications:** Thêm email/SMS

---

**Status:** ✅ COMPLETE  
**Date:** 20/02/2026  
**Version:** 1.0.0

