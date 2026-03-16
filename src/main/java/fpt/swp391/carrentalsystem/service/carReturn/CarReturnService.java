package fpt.swp391.carrentalsystem.service.carReturn;

import fpt.swp391.carrentalsystem.dto.request.CarReturnDto;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.CarReturn;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CarReturnService {

    List<CarReturn> getCarReturnsByCustomer(Long customerId);

    // MỚI: Lấy danh sách các Booking ĐÃ THUÊ XONG nhưng CHƯA TẠO đơn trả xe
    List<Booking> getBookingsReadyToReturn(Long customerId);

    Booking getBookingById(Integer bookingId);

    void createCarReturn(CarReturnDto dto);

    // Lấy chi tiết đơn trả xe
    CarReturn getCarReturnDetail(Integer returnId);

    //hàm này để lấy danh sách trả xe cho chủ xe
    List<CarReturn> getCarReturnsByOwner(Integer ownerId);

    //cập nhật phí phạt
    void updatePenaltyAmount(Integer returnId, BigDecimal penaltyAmount);

    void confirmCarReturn(Integer returnId);
}
