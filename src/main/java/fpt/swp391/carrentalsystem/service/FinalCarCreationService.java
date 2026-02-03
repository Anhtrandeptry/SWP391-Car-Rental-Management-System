package fpt.swp391.carrentalsystem.service;


import fpt.swp391.carrentalsystem.dto.request.CarDocumentDTO;
import fpt.swp391.carrentalsystem.dto.request.FinalCarSubmitDTO;

public interface FinalCarCreationService {

    /**
     * Tạo xe hoàn chỉnh từ 3 steps
     */
    CarDocumentDTO createCompleteCar(FinalCarSubmitDTO submitDTO);
}