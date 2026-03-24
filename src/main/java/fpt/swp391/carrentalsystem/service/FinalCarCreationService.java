package fpt.swp391.carrentalsystem.service;


import fpt.swp391.carrentalsystem.dto.request.FinalCarSubmitDTO;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;

public interface FinalCarCreationService {

    /**
     * Tạo xe hoàn chỉnh từ 3 steps
     */
    CarResponseDto createCompleteCar(FinalCarSubmitDTO submitDTO);
}