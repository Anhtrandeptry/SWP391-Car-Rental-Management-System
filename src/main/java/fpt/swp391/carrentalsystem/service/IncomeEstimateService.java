package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.IncomeEstimateRequestDTO;
import fpt.swp391.carrentalsystem.dto.response.IncomeEstimateResponseDTO;

public interface IncomeEstimateService {

    /**
     * Tính toán thu nhập ước tính cho xe
     *
     * @param request dữ liệu đầu vào (brandId, modelId, year, city)
     * @return thông tin ước tính thu nhập
     */
    IncomeEstimateResponseDTO calculateIncome(IncomeEstimateRequestDTO request);
}


