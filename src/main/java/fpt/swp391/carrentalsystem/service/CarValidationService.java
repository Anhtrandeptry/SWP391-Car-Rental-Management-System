package fpt.swp391.carrentalsystem.service;


import fpt.swp391.carrentalsystem.dto.request.CarSetupDTO;

public interface CarValidationService {

    /**
     * Kiểm tra biển số xe đã tồn tại chưa
     */
    boolean isLicensePlateExists(String licensePlate);

    /**
     * Validate dữ liệu step 1
     */
    void validateStep1(CarSetupDTO carSetupDTO);
}

