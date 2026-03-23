package fpt.swp391.carrentalsystem.service;




import fpt.swp391.carrentalsystem.dto.response.CarResponseDTO;

public interface CarServiceByThinhHT {

    // ... existing methods ...

    /**
     * Xóa xe (Soft delete - chuyển status sang INACTIVE)
     */
    void softDeleteCar(Long id, Long ownerId);

    /**
     * Xóa xe vĩnh viễn (Hard delete)
     */
    void hardDeleteCar(Long id, Long ownerId);

    /**
     * Khôi phục xe đã xóa mềm
     */
    CarResponseDTO restoreCar(Long id, Long ownerId);

    /**
     * Kiểm tra xe có đang được thuê không
     */
    boolean isCarRented(Long carId);
}
