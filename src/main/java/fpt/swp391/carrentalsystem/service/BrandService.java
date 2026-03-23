package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.BrandDTO;
import fpt.swp391.carrentalsystem.dto.request.CarModelDTO;

import java.util.List;

public interface BrandService {

    /**
     * Lấy tất cả hãng xe
     */
    List<BrandDTO> getAllBrands();

    /**
     * Lấy danh sách model theo brand
     */
    List<CarModelDTO> getModelsByBrandId(Long brandId);
}


