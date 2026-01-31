package fpt.swp391.carrentalsystem.service;



import fpt.swp391.carrentalsystem.dto.request.BrandDTO;
import fpt.swp391.carrentalsystem.dto.request.CarModelDTO;
import fpt.swp391.carrentalsystem.entity.Brand;
import fpt.swp391.carrentalsystem.repository.BrandRepository;
import fpt.swp391.carrentalsystem.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final CarModelRepository carModelRepository;

    @Override
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(BrandDTO::new)
                .collect(Collectors.toList());
    }

//    @Override
//    public BrandDTO getBrandById(Long id) {
//        Brand brand = brandRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hãng xe với ID: " + id));
//        return new BrandDTO(brand);
//    }

    @Override
    public List<CarModelDTO> getModelsByBrandId(Long brandId) {
        return carModelRepository.findByBrandId(brandId).stream()
                .map(CarModelDTO::new)
                .collect(Collectors.toList());
    }
}




