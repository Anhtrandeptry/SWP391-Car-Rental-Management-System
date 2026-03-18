package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.dto.request.CarCreationSessionDTO;
import fpt.swp391.carrentalsystem.dto.request.IncomeEstimateFormDTO;
import fpt.swp391.carrentalsystem.dto.response.IncomeEstimateResponseDTO;
import fpt.swp391.carrentalsystem.dto.response.LocationDTO;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.service.CarCreationService;
import fpt.swp391.carrentalsystem.service.LocationService;
import fpt.swp391.carrentalsystem.validation.CarCreationValidation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

/**
 * Controller xử lý các trang tạo xe của Owner.
 * Sử dụng Server-Side Rendering với Thymeleaf.
 *
 * Nguyên tắc:
 * - Controller CHỈ route request và trả view
 * - Business logic nằm trong CarCreationService
 * - Validation sử dụng @Valid / @Validated + BindingResult
 * - External API data (brands, models, provinces) được fetch bởi JS frontend
 */
@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {

    private final LocationService locationService;
    private final CarCreationService carCreationService;
    private final CarRepository carRepository;

    // Static data for step 1 dropdowns (NOT external API data)
    private static final List<String> COLORS = Arrays.asList(
            "Trắng", "Đen", "Xám", "Bạc", "Đỏ", "Xanh", "Vàng", "Nâu"
    );
    private static final List<String> TRANSMISSION_TYPES = Arrays.asList(
            "Số tự động", "Số sàn"
    );
    private static final List<String> FUEL_TYPES = Arrays.asList(
            "Xăng", "Dầu", "Điện", "Hybrid"
    );
    private static final List<Integer> SEAT_OPTIONS = Arrays.asList(4, 5, 7, 16);

    @GetMapping("/dashboard")
    public String dashboard() {
        return "owner/car-owner-dashboard";
    }

    // ========================= INCOME ESTIMATE (SSR) =========================

    /**
     * GET /owner/income-estimate
     * Hiển thị form ước tính thu nhập.
     * Truyền form-backing object vào model.
     */
    @GetMapping("/income-estimate")
    public String showIncomeEstimate(Model model) {
        if (!model.containsAttribute("incomeForm")) {
            model.addAttribute("incomeForm", new IncomeEstimateFormDTO());
        }
        return "owner/owner-income-estimate";
    }

    /**
     * POST /owner/income-estimate
     * Xử lý form submit ước tính thu nhập.
     * - @Valid validates IncomeEstimateFormDTO (including pricePerDay)
     * - Nếu có lỗi, trả lại view với error messages
     * - Nếu OK, lưu giá do user nhập và hiển thị kết quả
     *
     * UPDATED: Price is now manually entered by user (not auto-calculated)
     */
    @PostMapping("/income-estimate")
    public String processIncomeEstimate(
            @Valid @ModelAttribute("incomeForm") IncomeEstimateFormDTO formDTO,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        // Nếu validation fails → trả lại form với lỗi
        if (bindingResult.hasErrors()) {
            log.warn("Income estimate validation errors: {}", bindingResult.getAllErrors());
            return "owner/owner-income-estimate";
        }

        try {
            // Save user-entered price and calculate estimated monthly income
            IncomeEstimateResponseDTO result =
                    carCreationService.saveManualPriceEstimate(formDTO, session);

            // Truyền kết quả vào model
            model.addAttribute("estimateResult", result);
            model.addAttribute("showResult", true);

            return "owner/owner-income-estimate";

        } catch (IllegalArgumentException e) {
            log.error("Income estimate error: {}", e.getMessage());
            bindingResult.reject("calculation.error", e.getMessage());
            return "owner/owner-income-estimate";
        }
    }

    // ========================= STEP 1: Thiết lập xe =========================

    /**
     * GET /owner/create-car-step1
     * Hiển thị form Step 1.
     */
    @GetMapping("/create-car-step1")
    public String showStep1(HttpSession session, Model model) {
        CarCreationSessionDTO carSession = carCreationService.getOrCreateSession(session);

        model.addAttribute("carSession", carSession);
        populateStep1Model(model);

        return "owner/create-car-step1";
    }

    /**
     * POST /owner/create-car-step1
     * Xử lý form submit Step 1 với @Validated(Step1.class) + BindingResult.
     * - Nếu có lỗi validation → trả lại form, re-populate dropdowns
     * - Kiểm tra biển số xe đã tồn tại → reject nếu trùng
     * - Nếu OK → merge vào session, redirect step 2
     */
    @PostMapping("/create-car-step1")
    public String processStep1(
            @Validated(CarCreationValidation.Step1.class) @ModelAttribute("carSession") CarCreationSessionDTO formData,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        // Normalize license plate: trim and uppercase
        if (formData.getLicensePlate() != null) {
            String normalizedPlate = formData.getLicensePlate().trim().toUpperCase().replaceAll("\\s+", "");
            formData.setLicensePlate(normalizedPlate);
        }

        // Nếu validation fails → trả lại form
        if (bindingResult.hasErrors()) {
            log.warn("Step 1 validation errors: {}", bindingResult.getAllErrors());
            populateStep1Model(model);
            return "owner/create-car-step1";
        }

        // Check if license plate already exists in database
        if (formData.getLicensePlate() != null && !formData.getLicensePlate().isEmpty()) {
            if (carRepository.existsByLicensePlate(formData.getLicensePlate())) {
                log.warn("License plate already exists: {}", formData.getLicensePlate());
                bindingResult.rejectValue(
                        "licensePlate",
                        "error.licensePlate.duplicate",
                        "Biển số xe đã tồn tại, vui lòng nhập biển số khác"
                );
                populateStep1Model(model);
                return "owner/create-car-step1";
            }
        }

        // Lấy session hiện tại hoặc tạo mới
        CarCreationSessionDTO sessionDTO = carCreationService.getOrCreateSession(session);

        // Delegate merge data cho Service
        carCreationService.mergeStep1Data(sessionDTO, formData);

        // Lưu session
        carCreationService.saveSession(sessionDTO, session);

        return "redirect:/owner/create-car-step2";
    }

    // ========================= STEP 2: Thông tin chi tiết =========================

    /**
     * GET /owner/create-car-step2
     * Hiển thị form Step 2.
     * Load danh sách provinces từ backend, districts/wards nếu đã chọn trước đó.
     */
    @GetMapping("/create-car-step2")
    public String showStep2(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        CarCreationSessionDTO carSession = carCreationService.getExistingSession(session);

        if (carSession == null || !carSession.isStep1Completed()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Vui lòng hoàn thành bước 1 trước");
            return "redirect:/owner/create-car-step1";
        }

        model.addAttribute("carSession", carSession);
        populateStep2Model(model, carSession);

        return "owner/create-car-step2";
    }

    /**
     * POST /owner/create-car-step2
     * Xử lý form submit Step 2 với @Validated(Step2.class) + BindingResult.
     */
    @PostMapping("/create-car-step2")
    public String processStep2(
            @Validated(CarCreationValidation.Step2.class) @ModelAttribute("carSession") CarCreationSessionDTO formData,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        CarCreationSessionDTO sessionDTO = carCreationService.getExistingSession(session);

        if (sessionDTO == null || !sessionDTO.isStep1Completed()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Vui lòng hoàn thành bước 1 trước");
            return "redirect:/owner/create-car-step1";
        }

        // Nếu validation fails → trả lại form, re-populate dropdowns
        if (bindingResult.hasErrors()) {
            log.warn("Step 2 validation errors: {}", bindingResult.getAllErrors());
            // Copy step 1 data from session into formData so info card renders correctly
            formData.setBrandName(sessionDTO.getBrandName());
            formData.setModelName(sessionDTO.getModelName());
            formData.setYear(sessionDTO.getYear());
            formData.setCity(sessionDTO.getCity());
            formData.setLicensePlate(sessionDTO.getLicensePlate());
            formData.setPricePerDay(sessionDTO.getPricePerDay());
            formData.setEstimatedMonthlyIncome(sessionDTO.getEstimatedMonthlyIncome());
            // Re-populate model để trang render đúng
            populateStep2Model(model, formData);
            return "owner/create-car-step2";
        }

        // Delegate merge data cho Service
        carCreationService.mergeStep2Data(sessionDTO, formData);

        // Lưu session
        carCreationService.saveSession(sessionDTO, session);

        return "redirect:/owner/create-car-step3";
    }

    // ========================= STEP 3: Giấy tờ =========================

    @GetMapping("/create-car-step3")
    public String showStep3(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        CarCreationSessionDTO carSession = carCreationService.getExistingSession(session);

        if (carSession == null || !carSession.isStep1Completed()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Vui lòng hoàn thành bước 1 trước");
            return "redirect:/owner/create-car-step1";
        }

        if (!carSession.isStep2Completed()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Vui lòng hoàn thành bước 2 trước");
            return "redirect:/owner/create-car-step2";
        }

        model.addAttribute("carSession", carSession);
        return "owner/create-car-step3";
    }

    // ========================= API cho cascading dropdown (Step 2) =========================
    // GIỮ NGUYÊN: Frontend JS gọi API này để load districts/wards phụ thuộc

    @GetMapping("/api/districts")
    @ResponseBody
    public List<LocationDTO> getDistricts(@RequestParam String provinceCode) {
        return locationService.getDistrictsByProvince(provinceCode);
    }

    @GetMapping("/api/wards")
    @ResponseBody
    public List<LocationDTO> getWards(@RequestParam String districtCode) {
        return locationService.getWardsByDistrict(districtCode);
    }

    // ========================= PRIVATE HELPERS =========================

    /**
     * Populate model attributes cần thiết cho Step 1 form.
     * Đảm bảo khi trả lại form (có lỗi), dropdowns vẫn có data.
     */
    private void populateStep1Model(Model model) {
        model.addAttribute("colors", COLORS);
        model.addAttribute("transmissionTypes", TRANSMISSION_TYPES);
        model.addAttribute("fuelTypes", FUEL_TYPES);
        model.addAttribute("seatOptions", SEAT_OPTIONS);
    }

    /**
     * Populate model attributes cần thiết cho Step 2 form.
     * Load provinces luôn; load districts/wards nếu đã chọn trước.
     * Auto-match city name to province code when navigating from step1 for the first time.
     */
    private void populateStep2Model(Model model, CarCreationSessionDTO carSession) {
        List<LocationDTO> provinces = locationService.getAllProvinces();
        model.addAttribute("provinces", provinces);

        List<LocationDTO> districts = List.of();
        List<LocationDTO> wards = List.of();

        // If provinceCode not set but city (from income estimate) exists, try to auto-match
        if ((carSession.getProvinceCode() == null || carSession.getProvinceCode().isEmpty())
                && carSession.getCity() != null && !carSession.getCity().isEmpty()) {
            String city = carSession.getCity().trim();
            for (LocationDTO p : provinces) {
                if (p.getName().equals(city)
                        || p.getName().contains(city)
                        || city.contains(p.getName())) {
                    carSession.setProvinceCode(p.getCode());
                    carSession.setProvince(p.getName());
                    log.info("Auto-matched city '{}' to province '{}' (code={})",
                            city, p.getName(), p.getCode());
                    break;
                }
            }
        }

        if (carSession.getProvinceCode() != null && !carSession.getProvinceCode().isEmpty()) {
            districts = locationService.getDistrictsByProvince(carSession.getProvinceCode());

            if (carSession.getDistrictCode() != null && !carSession.getDistrictCode().isEmpty()) {
                wards = locationService.getWardsByDistrict(carSession.getDistrictCode());
            }
        }

        model.addAttribute("districts", districts);
        model.addAttribute("wards", wards);
    }
}





