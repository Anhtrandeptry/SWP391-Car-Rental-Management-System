package fpt.swp391.carrentalsystem.service;



import fpt.swp391.carrentalsystem.dto.request.IncomeEstimateRequestDTO;
import fpt.swp391.carrentalsystem.dto.response.IncomeEstimateResponseDTO;
import fpt.swp391.carrentalsystem.entity.Brand;
import fpt.swp391.carrentalsystem.entity.CarModel;
import fpt.swp391.carrentalsystem.exception.ResourceNotFoundException;
import fpt.swp391.carrentalsystem.repository.BrandRepository;
import fpt.swp391.carrentalsystem.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeEstimateServiceImpl implements IncomeEstimateService {

    private final BrandRepository brandRepository;
    private final CarModelRepository carModelRepository;

    @Override
    public IncomeEstimateResponseDTO calculateIncome(IncomeEstimateRequestDTO request) {
        // Lấy thông tin Brand và Model
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Không tìm thấy hãng xe với ID: %d", request.getBrandId())));

        CarModel model = carModelRepository.findById(request.getModelId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu xe"));

        // Tính giá cho thuê gợi ý dựa trên hãng xe, năm sản xuất và thành phố
        BigDecimal basePricePerDay = calculateBasePrice(brand.getName(), request.getYear(), request.getCity());

        // Tính thu nhập ước tính (giả sử cho thuê 20 ngày/tháng)
        BigDecimal estimatedMonthlyIncome = basePricePerDay.multiply(BigDecimal.valueOf(20));

        // Tạo recommendation
        String recommendation = generateRecommendation(brand.getName(), request.getYear(), request.getCity());

        return new IncomeEstimateResponseDTO(
                brand.getName(),
                model.getName(),
                request.getYear(),
                request.getCity(),
                basePricePerDay,
                estimatedMonthlyIncome,
                basePricePerDay,
                recommendation
        );
    }

    // Logic tính giá cơ bản
    private BigDecimal calculateBasePrice(String brandName, Integer year, String city) {
        BigDecimal basePrice = BigDecimal.valueOf(1000000); // 1 triệu VNĐ base

        // Điều chỉnh theo hãng xe
        Map<String, BigDecimal> brandMultiplier = new HashMap<>();
        brandMultiplier.put("TOYOTA", BigDecimal.valueOf(1.2));
        brandMultiplier.put("HONDA", BigDecimal.valueOf(1.15));
        brandMultiplier.put("MAZDA", BigDecimal.valueOf(1.1));
        brandMultiplier.put("KIA", BigDecimal.valueOf(1.0));
        brandMultiplier.put("HYUNDAI", BigDecimal.valueOf(1.0));
        brandMultiplier.put("FORD", BigDecimal.valueOf(1.3));
        brandMultiplier.put("MERCEDES-BENZ", BigDecimal.valueOf(2.0));
        brandMultiplier.put("BMW", BigDecimal.valueOf(2.2));

        BigDecimal multiplier = brandMultiplier.getOrDefault(brandName.toUpperCase(), BigDecimal.valueOf(1.0));
        basePrice = basePrice.multiply(multiplier);

        // Điều chỉnh theo năm sản xuất (xe mới hơn = giá cao hơn)
        int currentYear = 2025;
        int age = currentYear - year;
        if (age <= 2) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(1.3));
        } else if (age <= 5) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(1.1));
        } else if (age > 10) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(0.8));
        }

        // Điều chỉnh theo thành phố
        if (city.equals("Hồ Chí Minh") || city.equals("Hà Nội")) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(1.2));
        } else if (city.equals("Đà Nẵng")) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(1.1));
        }

        // Làm tròn đến hàng nghìn
        return basePrice.divide(BigDecimal.valueOf(1000), 0, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(1000));
    }

    // Tạo recommendation
    private String generateRecommendation(String brandName, Integer year, String city) {
        int currentYear = 2025;
        int age = currentYear - year;

        if (age <= 2) {
            return "Xe của bạn còn mới, rất được ưa chuộng! Bạn có thể cho thuê với giá cao.";
        } else if (age <= 5) {
            return "Xe của bạn trong tình trạng tốt, phù hợp cho thuê dài hạn.";
        } else {
            return "Xe của bạn có thể cho thuê với giá hợp lý, phù hợp với khách hàng tiết kiệm.";
        }
    }
}


