package fpt.swp391.carrentalsystem.service;


import fpt.swp391.carrentalsystem.dto.request.IncomeEstimateRequestDTO;
import fpt.swp391.carrentalsystem.dto.response.IncomeEstimateResponseDTO;

public interface IncomeEstimateService {

    IncomeEstimateResponseDTO calculateIncome(IncomeEstimateRequestDTO request);
}

