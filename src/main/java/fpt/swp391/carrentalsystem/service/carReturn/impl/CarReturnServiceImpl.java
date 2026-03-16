package fpt.swp391.carrentalsystem.service.carReturn.impl;

import fpt.swp391.carrentalsystem.dto.request.CarReturnDto;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarReturn;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.repository.CarReturnRepository;
import fpt.swp391.carrentalsystem.service.carReturn.CarReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public

class CarReturnServiceImpl implements CarReturnService {

    private final CarReturnRepository carReturnRepository;
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;

    @Override
    public List<CarReturn> getCarReturnsByCustomer(Long customerId) {
        // Log debug
        System.out.println("FETCHING CAR RETURNS FOR CUSTOMER ID: " + customerId);
        List<CarReturn> returns = carReturnRepository.findReturnsByCustomerId(customerId);
        System.out.println("TOTAL RETURNS FOUND: " + (returns != null ? returns.size() : 0));
        return returns;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsReadyToReturn(Long customerId) {
        // Gọi hàm từ CarReturnRepository như bạn đã định nghĩa
        List<Booking> readyBookings = carReturnRepository.findReadyToReturn(customerId);

        // Log để kiểm tra dữ liệu khi debug
        System.out.println("Customer ID: " + customerId + " has " + readyBookings.size() + " bookings ready to return.");

        return readyBookings;
    }

    @Override
    public Booking getBookingById(Integer bookingId) {
        return bookingRepository.findWithDetailsByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đơn đặt xe với mã: " + bookingId));
    }

    @Override
    @Transactional
    public void createCarReturn(CarReturnDto dto) {

        // 1. Lấy Booking
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        // 2. Lấy Car
        Car car = booking.getCar();

        // 3. Tạo CarReturn
        CarReturn carReturn = CarReturn.builder()
                .booking(booking)
                .actualReturnDate(dto.getActualReturnDate())
                .odometerReading(dto.getOdometerReading())
                .damageDetected(Boolean.TRUE.equals(dto.getDamageDetected()))
                .damageDescription(dto.getDamageDescription())
                .penaltyAmount(null)
                .ownerConfirmation(false)
                .cleaningStatus(dto.getCleaningStatus())
                .createdAt(LocalDateTime.now())
                .build();

        // 4. Lưu đơn trả xe
        carReturnRepository.save(carReturn);

        // 5. Tính lại tổng tiền (trừ phí phạt nếu có)
        BigDecimal totalAmount = booking.getTotalAmount() != null
                ? booking.getTotalAmount()
                : BigDecimal.ZERO;

        BigDecimal penalty = dto.getPenaltyAmount() != null
                ? dto.getPenaltyAmount()
                : BigDecimal.ZERO;

        BigDecimal finalAmount = totalAmount.subtract(penalty);

        // tránh âm tiền
        if(finalAmount.compareTo(BigDecimal.ZERO) < 0){
            finalAmount = BigDecimal.ZERO;
        }

        booking.setTotalAmount(finalAmount);

        // 6. Cập nhật trạng thái booking
        booking.setStatus(BookingStatus.COMPLETED);

        bookingRepository.save(booking);

        // 7. Cập nhật trạng thái xe
        car.setStatus(CarStatus.AVAILABLE);

        carRepository.save(car);
    }
    @Override
    @Transactional(readOnly = true)
    public CarReturn getCarReturnDetail(Integer returnId) {
        return carReturnRepository.findDetailByReturnId(returnId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin chi tiết cho phiếu trả xe mã: " + returnId));
    }

    @Override
    public List<CarReturn> getCarReturnsByOwner(Integer ownerId) {
        return carReturnRepository.findByOwnerId(ownerId);
    }

    @Override
    public void updatePenaltyAmount(Integer returnId, BigDecimal penaltyAmount) {
        CarReturn carReturn = carReturnRepository.findById(returnId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn trả xe"));

        carReturn.setPenaltyAmount(penaltyAmount);
        carReturnRepository.save(carReturn);
    }
    @Override
    @Transactional
    public void confirmCarReturn(Integer returnId) {
        // 1. Tìm đơn trả xe
        CarReturn carReturn = carReturnRepository.findById(returnId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn trả xe"));

        // 2. Cập nhật xác nhận của chủ xe
        carReturn.setOwnerConfirmation(true);

        // 3. Lấy thông tin Booking liên quan
        Booking booking = carReturn.getBooking();

        // 4. Cập nhật trạng thái Booking thành COMPLETED (Hoàn thành)
        booking.setStatus(BookingStatus.COMPLETED);

        // 5. Cập nhật trạng thái Xe thành AVAILABLE (Sẵn sàng cho thuê tiếp)
        Car car = booking.getCar();
        car.setStatus(CarStatus.AVAILABLE);

        // Lưu tất cả (nhờ có @Transactional nên các thay đổi sẽ được commit đồng thời)
        carReturnRepository.save(carReturn);
        bookingRepository.save(booking);
        carRepository.save(car);
    }
}