# 🚗 Tính Năng Thuê Xe - Implementation Guide

## 📋 Tóm Tắt

Hệ thống thuê xe hoàn chỉnh với các tính năng:
- ✅ Đặt/thuê xe theo ngày giờ
- ✅ Tính toán phí tự động (phí giữ chỗ, phí thế chấp, phí thuê)
- ✅ Thanh toán QR code (phí giữ chỗ trong 3 phút)
- ✅ Auto-release nếu hết hạn thanh toán
- ✅ Thông báo cho cả khách hàng và chủ xe
- ✅ Quản lí cho thuê cho chủ xe

---

## 🏗️ Kiến Trúc Hệ Thống

### 1. **Entities** (Bảng Database)

#### `Car.java` - Thông tin xe
```
- carId (PK)
- owner (FK -> User)
- name, brand, model, carType
- fuelType (PETROL, ELECTRIC, DIESEL, HYBRID)
- pricePerDay (giá tiền 1 ngày)
- location (địa điểm)
- status (PENDING, APPROVED, AVAILABLE, BOOKED, DISABLED...)
- createdAt, updatedAt
```

#### `Booking.java` - Thông tin đặt xe
```
- bookingId (PK)
- customer (FK -> User)
- car (FK -> Car)
- startDate, endDate
- pickupLocation
- rentalFee (tính = pricePerDay × số ngày)
- holdingFee = 500,000 VND (phí giữ chỗ)
- depositAmount = 5,000,000 VND (phí thế chấp)
- totalAmount (tổng = rentalFee + holdingFee + depositAmount)
- status (PAYMENT_PENDING, CONFIRMED, CANCELLED...)
- paymentStatus (UNPAID, PARTIALLY_PAID, PAID)
- paymentDeadline (3 phút từ lúc tạo booking)
- createdAt, updatedAt
```

---

## 📁 Cấu Trúc Files

```
src/main/java/fpt/swp391/carrentalsystem/

├── entity/
│   ├── Car.java              ✅ Entity xe
│   ├── Booking.java          ✅ Entity đặt xe
│   └── User.java             ✅ Đã có

├── enums/
│   ├── CarStatus.java        ✅ Trạng thái xe
│   ├── BookingStatus.java    ✅ Trạng thái đặt xe
│   ├── PaymentStatus.java    ✅ Trạng thái thanh toán
│   ├── FuelType.java         ✅ Loại nhiên liệu
│   └── ...

├── dto/
│   ├── request/
│   │   ├── CreateBookingRequest.java     ✅ Request tạo booking
│   │   └── ...
│   └── response/
│       ├── BookingConfirmationDto.java   ✅ Response xác nhận đặt xe
│       ├── PaymentInfoDto.java           ✅ Response thông tin thanh toán
│       ├── ApiResponse.java              ✅ Response wrapper chung
│       └── ...

├── repository/
│   ├── CarRepository.java               ✅ Repo quản lí xe
│   ├── BookingRepository.java           ✅ Repo quản lí booking
│   └── UserRepository.java              ✅ Đã có

├── mapper/
│   ├── BookingMapper.java               ✅ MapStruct mapper
│   └── ...

├── service/
│   ├── BookingService.java              ✅ Interface service
│   ├── BookingServiceImpl.java           ✅ Implementation
│   ├── BookingScheduler.java            ✅ Scheduled task (auto-release)
│   └── ...

└── controller/
    ├── customer/
    │   ├── BookingController.java        ✅ REST API booking
    │   └── ...
    └── ...
```

---

## 🔌 API Endpoints

### **Base URL:** `/api/v1/bookings`

#### 1️⃣ **POST /api/v1/bookings** - Tạo booking
**Request:**
```json
{
  "carId": 1,
  "startDate": "2026-02-25T09:00:00",
  "endDate": "2026-02-26T09:00:00",
  "pickupLocation": "123 Nguyen Hue, HCMC"
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "bookingId": 101,
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

#### 2️⃣ **GET /api/v1/bookings/{bookingId}/payment-info** - Lấy thông tin thanh toán
**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "bookingId": 101,
    "holdingFee": 500000,
    "depositAmount": 5000000,
    "rentalFee": 1500000,
    "totalAmount": 7000000,
    "qrCodeUrl": "data:image/png;base64,..."
  },
  "timestamp": "2026-02-20T10:31:00"
}
```

#### 3️⃣ **POST /api/v1/bookings/{bookingId}/confirm-payment** - Xác nhận thanh toán
**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": "Payment confirmed",
  "timestamp": "2026-02-20T10:32:00"
}
```

---

## 🔄 Quy Trình Đặt Xe

```
1. Customer chọn xe & ngày giờ
   ↓
2. POST /api/v1/bookings → Tạo booking (PAYMENT_PENDING)
   ↓
3. GET /api/v1/bookings/{id}/payment-info → Hiển thị QR code
   ↓
4. Customer quét QR → Thanh toán phí giữ chỗ (500K)
   ↓
5. POST /api/v1/bookings/{id}/confirm-payment → Status = CONFIRMED
   ↓
6. Gửi thông báo cho:
   - Customer: "Đặt xe thành công"
   - Owner: "Có khách đặt xe của bạn"
   ↓
7. Xe xuất hiện trong "Quản lí cho thuê" của Owner

⏰ Timeout logic:
   - Nếu 3 phút chưa thanh toán → Status = CANCELLED
   - Xe được mở khóa cho khách khác
```

---

## 💰 Tính Toán Phí

### Ví dụ:
- **Giá xe/ngày:** 1,500,000 VND
- **Thời gian thuê:** 25/02 09:00 - 26/02 09:00 = 1 ngày

**Chi Tiết Phí:**
| Loại Phí | Số Tiền | Ghi Chú |
|----------|---------|--------|
| Phí thuê xe | 1,500,000 VND | pricePerDay × số ngày |
| Phí giữ chỗ | 500,000 VND | Thanh toán ngay (3 phút) |
| Phí thế chấp | 5,000,000 VND | Thanh toán khi nhận xe |
| **Tổng Cộng** | **7,000,000 VND** | |

---

## 🤖 Scheduled Tasks

### `BookingScheduler.java`
```java
@Scheduled(fixedDelay = 60000)  // Chạy mỗi 60 giây
public void releaseExpiredBookings() {
    // Tìm các booking:
    // - Status = PAYMENT_PENDING
    // - PaymentStatus = UNPAID
    // - PaymentDeadline < NOW
    // → Set status = CANCELLED
}
```

**Chức năng:**
- ✅ Mỗi 1 phút kiểm tra 1 lần
- ✅ Tìm booking quá hạn thanh toán
- ✅ Tự động hủy nếu hết hạn
- ✅ Xe được mở khóa cho khách khác

---

## 🛠️ Sử Dụng MapStruct

### `BookingMapper.java`
```java
@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "car.carId", target = "carId")
    @Mapping(source = "car.name", target = "carName")
    BookingConfirmationDto toConfirmationDto(Booking booking);
}
```

**Lợi Ích:**
- ✅ Tự động mapping Entity → DTO
- ✅ Không cần viết setter thủ công
- ✅ Type-safe
- ✅ Compile-time validation

---

## 🔒 Kiểm Tra Tính Khả Dụng

### Logic trong `BookingServiceImpl.java`
```java
private boolean isCarAvailable(Integer carId, LocalDateTime startDate, LocalDateTime endDate) {
    return bookingRepository.countOverlappingBookings(
        carId, startDate, endDate, BookingStatus.CONFIRMED) == 0;
}
```

**Query:**
```sql
SELECT COUNT(b) FROM Booking b 
WHERE b.car.carId = :carId 
AND b.status = :status 
AND ((b.startDate < :endDate AND b.endDate > :startDate))
```

**Kiểm Tra Trùng Lặp:**
```
Booking A:  [===========]  (09:00 - 17:00)
Booking B:        [===========]  (14:00 - 22:00)

Trùng lặp nếu:
- startDate_A < endDate_B   (09:00 < 22:00 ✓)
AND
- endDate_A > startDate_B   (17:00 > 14:00 ✓)
→ KHÔNG CHO PHÉP
```

---

## 📝 Annotation & Configuration

### `@EnableScheduling` (CarRentalSystemApplication.java)
```java
@SpringBootApplication
@EnableScheduling
public class CarRentalSystemApplication {
    // ...
}
```
**Cần thiết để:** Kích hoạt @Scheduled tasks

### `@Service @Transactional` (BookingServiceImpl.java)
```java
@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    // ...
}
```
**Tác dụng:**
- ✅ `@Service` - Đăng ký Spring Bean
- ✅ `@Transactional` - Tự động rollback khi lỗi

### `@RequiredArgsConstructor` (Constructor Injection)
```java
@RequiredArgsConstructor
private final BookingRepository bookingRepository;
private final BookingService bookingService;
// Tự động tạo constructor
```

---

## 🧪 Kiểm Thử API

### Sử dụng Postman/cURL:

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

# 2. Lấy thông tin thanh toán
curl http://localhost:8080/api/v1/bookings/101/payment-info

# 3. Xác nhận thanh toán
curl -X POST http://localhost:8080/api/v1/bookings/101/confirm-payment
```

---

## ⚙️ Cấu Hình Database

### `application.properties`
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/crms_db
spring.jpa.hibernate.ddl-auto=update
```

**Hibernate tự động tạo tables:**
- ✅ `cars`
- ✅ `bookings`
- ✅ `users` (có sẵn)

---

## 📊 Luồng Xử Lý Chi Tiết

```
┌─────────────────────────────────────────────────────┐
│ 1. Customer POST /api/v1/bookings                   │
└────────────────┬──────────────────────────────────┘
                 ↓
    ┌────────────────────────────────────┐
    │ BookingController.createBooking()   │
    │ - Valid request                    │
    │ - Extract userId from auth         │
    └────────────┬───────────────────────┘
                 ↓
    ┌────────────────────────────────────────────┐
    │ BookingService.createBooking()              │
    │ - Find Customer by userId                  │
    │ - Find Car by carId                        │
    │ - Validate dates (endDate > startDate)     │
    │ - Check car availability                   │
    │ - Calculate fees                           │
    │ - Build Booking entity                     │
    │ - Save to DB                               │
    │ - Map to BookingConfirmationDto (MapStruct)│
    └────────────┬───────────────────────────────┘
                 ↓
    ┌────────────────────────────────────────────┐
    │ Response: 200 OK with BookingConfirmationDto│
    │ - bookingId                                │
    │ - totalAmount, fees                        │
    │ - paymentDeadline (now + 3 min)            │
    └────────────┬───────────────────────────────┘
                 ↓
    ┌────────────────────────────────────────────┐
    │ 2. Customer GET /api/v1/bookings/{id}/...  │
    │ Retrieve payment info & QR code            │
    └────────────┬───────────────────────────────┘
                 ↓
    ┌────────────────────────────────────────────┐
    │ 3. Customer scans QR → Pay 500K (holding fee)
    └────────────┬───────────────────────────────┘
                 ↓
    ┌────────────────────────────────────────────┐
    │ 4. Customer POST /api/v1/bookings/{id}/...│
    │    confirm-payment                         │
    └────────────┬───────────────────────────────┘
                 ↓
    ┌────────────────────────────────────────────┐
    │ BookingService.confirmPayment()             │
    │ - Update status → CONFIRMED                │
    │ - Update paymentStatus → PAID              │
    │ - Send notifications                       │
    │ - Update owner's rental list               │
    └────────────┬───────────────────────────────┘
                 ↓
    ┌────────────────────────────────────────────┐
    │ Response: 200 OK "Payment confirmed"        │
    │ Booking complete! ✅                        │
    └────────────────────────────────────────────┘

⏰ Background Task:
    Every 60 seconds:
    - Find PAYMENT_PENDING + UNPAID bookings
    - If paymentDeadline < NOW
      → Set status = CANCELLED
      → Notification sent
```

---

## 🔐 Security & Validation

### Request Validation (`CreateBookingRequest.java`)
```java
@NotNull(message = "Car ID is required")
private Integer carId;

@NotNull(message = "Start date is required")
@FutureOrPresent(message = "Start date must be in the future")
private LocalDateTime startDate;

@NotNull(message = "End date is required")
@Future(message = "End date must be after start date")
private LocalDateTime endDate;
```

### Service-level Validation
```java
if (!request.getEndDate().isAfter(request.getStartDate())) {
    throw new RuntimeException("End date must be after start date");
}

boolean isAvailable = isCarAvailable(car.getCarId(), ...);
if (!isAvailable) {
    throw new RuntimeException("Car is not available for selected dates");
}
```

### Authentication
```java
private Long extractUserId(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return Long.valueOf(userDetails.getUsername());
}
```

---

## 📋 Checklist Hoàn Thành

- ✅ Entities (Car, Booking)
- ✅ Enums (CarStatus, BookingStatus, PaymentStatus, FuelType)
- ✅ DTOs (Request/Response)
- ✅ Repositories (CarRepository, BookingRepository)
- ✅ MapStruct Mapper (BookingMapper)
- ✅ Service Interface (BookingService)
- ✅ Service Implementation (BookingServiceImpl)
- ✅ Scheduler (BookingScheduler)
- ✅ REST Controller (BookingController)
- ✅ API Response Wrapper (ApiResponse)
- ✅ @EnableScheduling annotation
- ✅ Auto fee calculation
- ✅ Car availability check
- ✅ Payment timeout logic (3 minutes)
- ✅ MapStruct integration

---

## 🚀 Tiếp Theo (Future Features)

1. **Payment Gateway Integration**
   - Kết nối VnPay/Momo API
   - Webhook nhận callback thanh toán

2. **Notification System**
   - Email/SMS thông báo
   - Push notification app

3. **Owner Dashboard**
   - Xem danh sách xe cho thuê
   - Quản lí booking
   - Thống kê doanh thu

4. **Rating & Review**
   - Khách hàng đánh giá xe
   - Chủ xe đánh giá khách

5. **Admin Panel**
   - Quản lí tất cả booking
   - Xử lý tranh chấp
   - Báo cáo hệ thống

---

## 📚 Tài Liệu Tham Khảo

- Spring Boot: https://spring.io/projects/spring-boot
- JPA/Hibernate: https://hibernate.org/
- MapStruct: https://mapstruct.org/
- Lombok: https://projectlombok.org/
- REST API: https://restfulapi.net/

---

**Tạo bởi:** AI Copilot  
**Ngày:** 20/02/2026  
**Version:** 1.0.0

