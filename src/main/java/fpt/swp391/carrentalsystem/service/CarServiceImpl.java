package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.CarResponseDTO;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.exception.ResourceNotFoundException;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarDocumentService carDocumentService;

    // ... existing methods ...

    @Override
    public void softDeleteCar(Long id, Long ownerId) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));

        // Kiểm tra quyền sở hữu
        if (!car.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Bạn không có quyền xóa xe này");
        }

        // Kiểm tra xe có đang được thuê không
        if (car.getStatus() == CarStatus.RENTED || car.getStatus() == CarStatus.BOOKED) {
            throw new IllegalArgumentException("Không thể xóa xe đang được thuê hoặc đã đặt");
        }

        // Soft delete - chuyển status sang INACTIVE
        car.setStatus(CarStatus.INACTIVE);
        carRepository.save(car);
    }

    @Override
    public void hardDeleteCar(Long id, Long ownerId) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));

        // Kiểm tra quyền sở hữu
        if (!car.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Bạn không có quyền xóa xe này");
        }

        // Kiểm tra xe có đang được thuê không
        if (car.getStatus() == CarStatus.RENTED || car.getStatus() == CarStatus.BOOKED) {
            throw new IllegalArgumentException("Không thể xóa xe đang được thuê hoặc đã đặt");
        }

        // Xóa documents của xe
        // carDocumentService.deleteAllDocumentsByCar(id);

        // Hard delete - xóa vĩnh viễn khỏi database
        carRepository.delete(car);
    }

    @Override
    public CarResponseDTO restoreCar(Long id, Long ownerId) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));

        // Kiểm tra quyền sở hữu
        if (!car.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Bạn không có quyền khôi phục xe này");
        }

        // Kiểm tra xe có ở trạng thái INACTIVE không
        if (car.getStatus() != CarStatus.INACTIVE) {
            throw new IllegalArgumentException("Chỉ có thể khôi phục xe đã bị vô hiệu hóa");
        }

        // Restore - chuyển về PENDING để admin duyệt lại
        car.setStatus(CarStatus.PENDING);
        Car restoredCar = carRepository.save(car);

        return new CarResponseDTO(restoredCar);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCarRented(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        return car.getStatus() == CarStatus.RENTED || car.getStatus() == CarStatus.BOOKED;
    }
}