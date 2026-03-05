package fpt.swp391.carrentalsystem.service;


import fpt.swp391.carrentalsystem.dto.request.FinalCarSubmitDTO;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDTO;

public interface FinalCarCreationService {

    /**
     * Tạo xe hoàn chỉnh từ 3 steps
     */
    CarResponseDTO createCompleteCar(FinalCarSubmitDTO submitDTO);
}