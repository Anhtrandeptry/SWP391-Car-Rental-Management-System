package fpt.swp391.carrentalsystem.service;


import fpt.swp391.carrentalsystem.dto.request.CarInfoDTO;

public interface CarInfoService {

    /**
     * Validate dữ liệu Step 2
     */
    void validateStep2(CarInfoDTO carInfoDTO);

    /**
     * Lưu nháp Step 2
     */
    CarInfoDTO saveDraftStep2(CarInfoDTO carInfoDTO);

    /**
     * Lấy thông tin nháp Step 2
     */
    CarInfoDTO getDraftStep2(Long ownerId);
}

