package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.exception.ResourceNotFoundException;
import fpt.swp391.carrentalsystem.mapper.CarMapper;
import fpt.swp391.carrentalsystem.repository.CarRepositoryByThinhHT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceByThinhHTImpl implements CarServiceByThinhHT {

    private final CarRepositoryByThinhHT carRepositoryByThinhHT;
    private final CarDocumentService carDocumentService;
    private final CarMapper carMapper;

    // ... existing methods ...

    @Override
    public void softDeleteCar(Long id, Long ownerId) {
        Car car = carRepositoryByThinhHT.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));

        // Kiểm tra quyền sở hữu
        if (!car.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Bạn không có quyền xóa xe này");
        }

        // Kiểm tra xe có đang được thuê không
        if (car.getStatus() == CarStatus.RESERVED || car.getStatus() == CarStatus.BOOKED) {
            throw new IllegalArgumentException("Không thể xóa xe đang được thuê hoặc đã đặt");
        }

        // Soft delete - chuyển status sang INACTIVE
        car.setStatus(CarStatus.INACTIVE);
        carRepositoryByThinhHT.save(car);
    }

    @Override
    public void hardDeleteCar(Long id, Long ownerId) {
        Car car = carRepositoryByThinhHT.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));

        // Kiểm tra quyền sở hữu
        if (!car.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Bạn không có quyền xóa xe này");
        }

        // Kiểm tra xe có đang được thuê không
        if (car.getStatus() == CarStatus.RESERVED || car.getStatus() == CarStatus.BOOKED) {
            throw new IllegalArgumentException("Không thể xóa xe đang được thuê hoặc đã đặt");
        }

        // Xóa documents của xe
        // carDocumentService.deleteAllDocumentsByCar(id);

        // Hard delete - xóa vĩnh viễn khỏi database
        carRepositoryByThinhHT.delete(car);
    }

    @Override
    public CarResponseDto restoreCar(Long id, Long ownerId) {
        Car car = carRepositoryByThinhHT.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));

        // Kiểm tra quyền sở hữu
        if (!car.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Bạn không có quyền khôi phục xe này");
        }

        // Kiểm tra xe có ở trạng thái INACTIVE không
        if (car.getStatus() != CarStatus.INACTIVE) {
            throw new IllegalArgumentException("Chỉ có thể khôi phục xe đã bị vô hiệu hóa");
        }

        // Restore - chuyển về PENDING để admin duyệt lại
        car.setStatus(CarStatus.PENDING);
        Car restoredCar = carRepositoryByThinhHT.save(car);

        return carMapper.toDto(restoredCar);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCarRented(Long carId) {
        Car car = carRepositoryByThinhHT.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        return car.getStatus() == CarStatus.RESERVED || car.getStatus() == CarStatus.BOOKED;
    }
}