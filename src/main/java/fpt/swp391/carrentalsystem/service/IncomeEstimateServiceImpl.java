package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.IncomeEstimateRequestDTO;
import fpt.swp391.carrentalsystem.dto.response.IncomeEstimateResponseDTO;
import fpt.swp391.carrentalsystem.repository.BrandRepository;
import fpt.swp391.carrentalsystem.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeEstimateServiceImpl implements IncomeEstimateService {

    private final BrandRepository brandRepository;
    private final CarModelRepository carModelRepository;

    @Override
    public IncomeEstimateResponseDTO calculateIncome(IncomeEstimateRequestDTO request) {

        // ===== 1. Validate input =====
        if (request.getYear() > Year.now().getValue()) {
            throw new IllegalArgumentException("Năm sản xuất không hợp lệ");
        }

        if (request.getYear() < 1990) {
            throw new IllegalArgumentException("Năm sản xuất phải từ 1990 trở lên");
        }

        // ===== 2. Lấy brandName và modelName trực tiếp từ request (CarQuery API) =====
        String brandName = request.getBrandName();
        String modelName = request.getModelName();

        if (brandName == null || brandName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên hãng xe không được để trống");
        }

        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên mẫu xe không được để trống");
        }

        // ===== 3. Tính giá thuê/ngày =====
        BigDecimal pricePerDay = calculateBasePrice(
                brandName,
                request.getYear(),
                request.getCity()
        );

        // ===== 4. Thu nhập/tháng (20 ngày) =====
        BigDecimal estimatedMonthlyIncome = pricePerDay.multiply(BigDecimal.valueOf(20));

        // ===== 5. Gợi ý =====
        String recommendation = generateRecommendation(
                brandName,
                request.getYear(),
                request.getCity()
        );

        // ===== 6. Response =====
        return new IncomeEstimateResponseDTO(
                brandName,
                modelName,
                request.getYear(),
                request.getCity(),
                pricePerDay,               // suggestedPricePerDay
                estimatedMonthlyIncome,    // estimatedIncome
                pricePerDay,               // minPricePerDay
                recommendation
        );
    }

    /* ================== CORE LOGIC ================== */

    private BigDecimal calculateBasePrice(String brandName, Integer year, String city) {

        // Giá nền KHÔNG BAO GIỜ = 0
        BigDecimal basePrice = BigDecimal.valueOf(1_000_00);

        // ===== HỆ SỐ HÃNG XE =====
        Map<String, BigDecimal> brandFactor = Map.of(
                "TOYOTA", BigDecimal.valueOf(1.2),
                "HONDA", BigDecimal.valueOf(1.15),
                "MAZDA", BigDecimal.valueOf(1.1),
                "KIA", BigDecimal.valueOf(1.0),
                "HYUNDAI", BigDecimal.valueOf(1.0),
                "FORD", BigDecimal.valueOf(1.3),
                "MERCEDES-BENZ", BigDecimal.valueOf(2.0),
                "BMW", BigDecimal.valueOf(2.2)
              //  "VINFAST", BigDecimal.valueOf(2.1)
        );

        BigDecimal brandMultiplier = brandFactor
                .getOrDefault(brandName.toUpperCase(), BigDecimal.ONE);

        basePrice = basePrice.multiply(brandMultiplier);

        // ===== HỆ SỐ NĂM SẢN XUẤT =====
        int currentYear = Year.now().getValue();
        int age = currentYear - year;

        if (age <= 2) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(1.2));
        } else if (age <= 5) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(1.1));
        } else if (age >= 10) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(0.8));
        }

        // ===== HỆ SỐ THÀNH PHỐ =====
        switch (city.trim()) {
            case "Hồ Chí Minh":
            case "Hà Nội":
                basePrice = basePrice.multiply(BigDecimal.valueOf(1.2));
                break;
            case "Đà Nẵng":
                basePrice = basePrice.multiply(BigDecimal.valueOf(1.1));
                break;
            default:
                basePrice = basePrice.multiply(BigDecimal.ONE);
        }

        // ===== LÀM TRÒN =====
        return basePrice
                .divide(BigDecimal.valueOf(10_000), 0, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(10_000));
    }

    private String generateRecommendation(String brand, Integer year, String city) {
        int age = Year.now().getValue() - year;

        if (age <= 2) {
            return "Xe mới, rất dễ cho thuê và có thể đặt giá cao.";
        }
        if (age <= 5) {
            return "Xe ổn định, phù hợp cho thuê dài ngày.";
        }
        return "Nên đặt giá hợp lý để tăng tỷ lệ thuê.";
    }
}


