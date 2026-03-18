package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.CarCreationSessionDTO;
import fpt.swp391.carrentalsystem.dto.request.IncomeEstimateFormDTO;
import fpt.swp391.carrentalsystem.dto.response.IncomeEstimateResponseDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service xử lý business logic cho quá trình tạo xe multi-step.
 *
 * Mục đích: Tách toàn bộ logic ra khỏi Controller.
 * Controller chỉ route request và trả view.
 *
 * UPDATED: Price is now manually entered by user (not auto-calculated)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CarCreationService {

    private static final String SESSION_CAR_CREATION = "CAR_CREATION_SESSION";
    private static final String SESSION_INCOME_ESTIMATE = "INCOME_ESTIMATE";

    // ==================== INCOME ESTIMATE ====================

    /**
     * Xử lý form ước tính thu nhập và lưu vào session.
     * Giá thuê/ngày do người dùng nhập thủ công (không tự động tính toán).
     * Thu nhập ước tính = pricePerDay * 20 ngày/tháng
     */
    public IncomeEstimateResponseDTO saveManualPriceEstimate(
            IncomeEstimateFormDTO formDTO, HttpSession session) {

        // Validate price
        if (formDTO.getPricePerDay() == null || formDTO.getPricePerDay().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá thuê phải lớn hơn 0");
        }

        // Calculate estimated monthly income based on user-entered price
        // Assumption: 20 rental days per month
        BigDecimal estimatedMonthlyIncome = formDTO.getPricePerDay().multiply(BigDecimal.valueOf(20));

        // Generate simple recommendation
        String recommendation = generateRecommendation(formDTO.getPricePerDay());

        // Create response DTO
        IncomeEstimateResponseDTO result = new IncomeEstimateResponseDTO(
                formDTO.getBrandName(),
                formDTO.getModelName(),
                formDTO.getYear(),
                formDTO.getCity(),
                formDTO.getPricePerDay(),      // suggestedPricePerDay (user-entered)
                estimatedMonthlyIncome,         // estimatedMonthlyIncome
                formDTO.getPricePerDay(),       // minPricePerDay (same as user-entered)
                recommendation
        );

        // Lưu kết quả vào session để Step 1 đọc
        session.setAttribute(SESSION_INCOME_ESTIMATE, result);

        log.info("Income estimate saved: brand={}, model={}, pricePerDay={}, monthly={}",
                formDTO.getBrandName(), formDTO.getModelName(),
                formDTO.getPricePerDay(), estimatedMonthlyIncome);

        return result;
    }

    /**
     * Generate simple recommendation based on price
     */
    private String generateRecommendation(BigDecimal pricePerDay) {
        if (pricePerDay.compareTo(BigDecimal.valueOf(500_000)) < 0) {
            return "Giá thuê thấp, có thể thu hút nhiều khách hàng.";
        } else if (pricePerDay.compareTo(BigDecimal.valueOf(1_500_000)) < 0) {
            return "Giá thuê trung bình, phù hợp với phân khúc phổ thông.";
        } else {
            return "Giá thuê cao, phù hợp với xe cao cấp hoặc xe mới.";
        }
    }

    // ==================== SESSION MANAGEMENT ====================

    /**
     * Lấy hoặc tạo mới CarCreationSessionDTO từ HttpSession.
     * Nếu có dữ liệu IncomeEstimate trong session thì copy vào.
     */
    public CarCreationSessionDTO getOrCreateSession(HttpSession session) {
        CarCreationSessionDTO sessionDTO =
                (CarCreationSessionDTO) session.getAttribute(SESSION_CAR_CREATION);

        if (sessionDTO == null) {
            sessionDTO = new CarCreationSessionDTO();
        }

        // Copy dữ liệu income estimate nếu có
        IncomeEstimateResponseDTO incomeEstimate =
                (IncomeEstimateResponseDTO) session.getAttribute(SESSION_INCOME_ESTIMATE);

        if (incomeEstimate != null) {
            sessionDTO.copyFromIncomeEstimate(
                    incomeEstimate.getBrandName(),
                    incomeEstimate.getModelName(),
                    incomeEstimate.getYear(),
                    incomeEstimate.getCity(),
                    incomeEstimate.getEstimatedMonthlyIncome(),
                    incomeEstimate.getSuggestedPricePerDay()
            );
        }

        session.setAttribute(SESSION_CAR_CREATION, sessionDTO);
        return sessionDTO;
    }

    /**
     * Lấy session DTO hiện tại (không tạo mới).
     * Trả về null nếu chưa có.
     */
    public CarCreationSessionDTO getExistingSession(HttpSession session) {
        return (CarCreationSessionDTO) session.getAttribute(SESSION_CAR_CREATION);
    }

    /**
     * Lưu session DTO
     */
    public void saveSession(CarCreationSessionDTO sessionDTO, HttpSession session) {
        session.setAttribute(SESSION_CAR_CREATION, sessionDTO);
    }

    // ==================== STEP 1: Thiết lập xe ====================

    /**
     * Merge dữ liệu form Step 1 vào session.
     * Giữ lại income estimate data, chỉ update các trường của Step 1.
     */
    public void mergeStep1Data(CarCreationSessionDTO sessionDTO,
                               CarCreationSessionDTO formData) {
        // Step 1 fields
        sessionDTO.setLicensePlate(formData.getLicensePlate());
        sessionDTO.setColor(formData.getColor());
        sessionDTO.setTransmissionType(formData.getTransmissionType());
        sessionDTO.setFuelType(formData.getFuelType());
        sessionDTO.setSeats(formData.getSeats());
        sessionDTO.setDescription(formData.getDescription());
        sessionDTO.setPricePerDay(formData.getPricePerDay());

        // Preserve income estimate hidden fields
        if (formData.getBrandName() != null) sessionDTO.setBrandName(formData.getBrandName());
        if (formData.getModelName() != null) sessionDTO.setModelName(formData.getModelName());
        if (formData.getYear() != null) sessionDTO.setYear(formData.getYear());
        if (formData.getCity() != null) sessionDTO.setCity(formData.getCity());
        if (formData.getEstimatedMonthlyIncome() != null)
            sessionDTO.setEstimatedMonthlyIncome(formData.getEstimatedMonthlyIncome());
        if (formData.getSuggestedPricePerDay() != null)
            sessionDTO.setSuggestedPricePerDay(formData.getSuggestedPricePerDay());

        sessionDTO.setStep1Completed(true);

        log.info("Step 1 completed: licensePlate={}", sessionDTO.getLicensePlate());
    }

    // ==================== STEP 2: Thông tin chi tiết ====================

    /**
     * Merge dữ liệu form Step 2 vào session.
     * Also syncs city field from province for data consistency.
     */
    public void mergeStep2Data(CarCreationSessionDTO sessionDTO,
                               CarCreationSessionDTO formData) {
        // Location
        sessionDTO.setAddress(formData.getAddress());
        sessionDTO.setProvinceCode(formData.getProvinceCode());
        sessionDTO.setDistrictCode(formData.getDistrictCode());
        sessionDTO.setWardCode(formData.getWardCode());
        sessionDTO.setProvince(formData.getProvince());
        sessionDTO.setDistrict(formData.getDistrict());
        sessionDTO.setWard(formData.getWard());

        // Sync city from province name for consistency across steps
        if (formData.getProvince() != null && !formData.getProvince().isEmpty()) {
            sessionDTO.setCity(formData.getProvince());
        }

        // Technical
        sessionDTO.setMileage(formData.getMileage());
        sessionDTO.setFuelConsumption(formData.getFuelConsumption());

        // Amenities
        sessionDTO.setHasAirConditioner(formData.getHasAirConditioner());
        sessionDTO.setHasDashCam(formData.getHasDashCam());
        sessionDTO.setHasReverseCamera(formData.getHasReverseCamera());
        sessionDTO.setHasGPS(formData.getHasGPS());
        sessionDTO.setHasUSB(formData.getHasUSB());
        sessionDTO.setHasBluetooth(formData.getHasBluetooth());
        sessionDTO.setHasMaps(formData.getHasMaps());
        sessionDTO.setHas360Camera(formData.getHas360Camera());
        sessionDTO.setHasSpareWheel(formData.getHasSpareWheel());
        sessionDTO.setHasDVDPlayer(formData.getHasDVDPlayer());
        sessionDTO.setHasETC(formData.getHasETC());
        sessionDTO.setHasSunroof(formData.getHasSunroof());

        // Notes
        sessionDTO.setSpecialNotes(formData.getSpecialNotes());

        sessionDTO.setStep2Completed(true);

        log.info("Step 2 completed: province={}, district={}, ward={}",
                sessionDTO.getProvince(), sessionDTO.getDistrict(), sessionDTO.getWard());
    }
}
