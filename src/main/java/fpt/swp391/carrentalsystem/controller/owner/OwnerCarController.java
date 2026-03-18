package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.dto.request.FinalCarSubmitDTO;
import fpt.swp391.carrentalsystem.dto.response.LocationDTO;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarDocument;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.repository.CarDocumentRepository;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.security.CustomUserDetails;
import fpt.swp391.carrentalsystem.service.FinalCarCreationService;
import fpt.swp391.carrentalsystem.service.LocationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OwnerCarController {

    private final FinalCarCreationService finalCarCreationService;
    private final CarRepository carRepository;
    private final CarDocumentRepository carDocumentRepository;
    private final LocationService locationService;

    // ======================================================
    // 1. ĐIỀU HƯỚNG GIAO DIỆN (VIEW) - Server-Side Rendering
    // ======================================================

    /**
     * Hiển thị danh sách xe của owner với filter support
     * GET /owner/owner-car-list?filter=all|deleted|pending|available|rented
     */
    @GetMapping("/owner/owner-car-list")
    public String showCarListPage(
            @RequestParam(value = "filter", required = false, defaultValue = "all") String filter,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        log.info("Owner accessing car list with filter: {}", filter);

        // Lấy ownerId từ authenticated user
        Long ownerId = userDetails != null ? userDetails.getId() : 1L;

        List<Car> cars;

        // Logic filter
        switch (filter.toLowerCase()) {
            case "deleted":
                // Lấy xe đã bị xóa mềm (status = INACTIVE)
                cars = carRepository.findDeletedCarsByOwnerId(ownerId);
                break;
            case "pending":
                cars = carRepository.findByOwnerIdAndStatus(ownerId, CarStatus.PENDING);
                break;
            case "available":
                cars = carRepository.findByOwnerIdAndStatus(ownerId, CarStatus.AVAILABLE);
                break;
            case "rented":
                cars = carRepository.findByOwnerIdAndStatus(ownerId, CarStatus.RENTED);
                break;
            case "all":
            default:
                // Mặc định: lấy tất cả xe CHƯA bị xóa
                cars = carRepository.findActiveCarsByOwnerId(ownerId);
                break;
        }

        log.info("Found {} cars for filter '{}'", cars.size(), filter);

        // Add data to model for Thymeleaf
        model.addAttribute("cars", cars);
        model.addAttribute("currentFilter", filter);
        model.addAttribute("totalCars", carRepository.findActiveCarsByOwnerId(ownerId).size());
        model.addAttribute("deletedCount", carRepository.findDeletedCarsByOwnerId(ownerId).size());

        return "owner/owner-car-list";
    }

    // ======================================================
    // 2. API XỬ LÝ DỮ LIỆU (REST)
    // ======================================================

    /**
     * API nhận toàn bộ dữ liệu từ Step 3 (gom cả sessionStorage từ Frontend)
     * Lấy ownerId từ authenticated user, không từ frontend
     */
    @PostMapping("/api/owner/complete-car-registration")
    @ResponseBody
    public ResponseEntity<?> completeRegistration(
            @RequestBody FinalCarSubmitDTO submitDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpSession session) {
        try {
            // Kiểm tra đăng nhập
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "message", "Bạn cần đăng nhập để thực hiện chức năng này"
                ));
            }

            // LẤY OWNER ID TỪ AUTHENTICATED USER (không tin tưởng frontend)
            Long ownerId = userDetails.getId();
            submitDTO.setOwnerId(ownerId);

            // Gọi Service để lưu vào Database
            finalCarCreationService.createCompleteCar(submitDTO);

            // XÓA SESSION SAU KHI TẠO XE THÀNH CÔNG
            session.removeAttribute("CAR_CREATION_SESSION");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đăng ký xe thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * API lấy danh sách xe với filter support
     * GET /api/owner/cars?filter=all|deleted|pending|available|rented
     */
    @GetMapping("/api/owner/cars")
    @ResponseBody
    public ResponseEntity<?> getOwnerCars(
            @RequestParam(value = "filter", required = false, defaultValue = "all") String filter,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Lấy ownerId từ authenticated user
        Long ownerId = userDetails != null ? userDetails.getId() : 1L;

        List<Car> cars;

        // Logic filter
        switch (filter.toLowerCase()) {
            case "deleted":
                cars = carRepository.findDeletedCarsByOwnerId(ownerId);
                break;
            case "pending":
                cars = carRepository.findByOwnerIdAndStatus(ownerId, CarStatus.PENDING);
                break;
            case "available":
                cars = carRepository.findByOwnerIdAndStatus(ownerId, CarStatus.AVAILABLE);
                break;
            case "rented":
                cars = carRepository.findByOwnerIdAndStatus(ownerId, CarStatus.RENTED);
                break;
            case "all":
            default:
                // Mặc định: lấy tất cả xe CHƯA bị xóa
                cars = carRepository.findActiveCarsByOwnerId(ownerId);
                break;
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", cars,
                "filter", filter,
                "count", cars.size()
        ));
    }

    /**
     * API xóa mềm xe
     */
    @DeleteMapping("/api/owner/cars/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Bạn chưa đăng nhập"));
        }

        Long ownerId = userDetails.getId();
        int updatedRows = carRepository.softDeleteCar(id, ownerId);

        if (updatedRows > 0) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Xóa xe thành công!"));
        } else {
            return ResponseEntity.status(403).body(Map.of("message", "Không tìm thấy xe hoặc bạn không có quyền xóa"));
        }
    }

    /**
     * API lấy dữ liệu 1 xe
     */
    @GetMapping("/api/owner/cars/{id}")
    @ResponseBody
    public ResponseEntity<?> getCarDetail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Car car = carRepository.findById(id).orElse(null);
        if (car != null && car.getOwnerId().equals(userDetails.getId())) {
            return ResponseEntity.ok(Map.of("success", true, "data", car));
        }
        return ResponseEntity.status(404).body(Map.of("message", "Xe không tồn tại"));
    }

    /**
     * API Cập nhật (PUT)
     */
    @PutMapping("/api/owner/cars/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCar(@PathVariable Long id, @RequestBody Map<String, Object> payload, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Car car = carRepository.findById(id).orElse(null);
        if (car != null && car.getOwnerId().equals(userDetails.getId())) {
            // Cập nhật các trường gửi lên từ JSON
            car.setPricePerDay(new BigDecimal(payload.get("pricePerDay").toString()));
            car.setCity(payload.get("city").toString());
            car.setDescription(payload.get("description").toString());

            carRepository.save(car);
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/owner/edit-car/{id}")
    public String showEditCarPage(@PathVariable Long id, Model model) {
        model.addAttribute("carId", id);
        return "owner/owner-edit-car";
    }

    /**
     * Show edit car form (SSR - Full edit)
     * GET /owner/cars/{id}/edit
     */
    @GetMapping("/owner/cars/{id}/edit")
    public String showEditCarForm(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Owner accessing edit form for car: {}", id);

        // Validate owner is authenticated
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để chỉnh sửa xe!");
            return "redirect:/login";
        }

        Long ownerId = userDetails.getId();

        // Find car and verify ownership
        Car car = carRepository.findById(id).orElse(null);

        if (car == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy xe!");
            return "redirect:/owner/owner-car-list";
        }

        // Verify car belongs to this owner
        if (!car.getOwnerId().equals(ownerId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền chỉnh sửa xe này!");
            return "redirect:/owner/owner-car-list";
        }

        // Add car to model
        model.addAttribute("car", car);
        model.addAttribute("fuelTypes", Car.FuelType.values());

        // Static dropdown options
        model.addAttribute("colors", java.util.Arrays.asList(
                "Trắng", "Đen", "Xám", "Bạc", "Đỏ", "Xanh", "Vàng", "Nâu"
        ));
        model.addAttribute("transmissionTypes", java.util.Arrays.asList(
                "Số tự động", "Số sàn"
        ));
        model.addAttribute("seatOptions", java.util.Arrays.asList(4, 5, 7, 16));

        // Load provinces for location dropdown (same as step 2)
        List<LocationDTO> provinces = locationService.getAllProvinces();
        model.addAttribute("provinces", provinces);

        return "owner/owner-car-edit";
    }

    /**
     * Process edit car form submission (SSR)
     * POST /owner/cars/{id}/edit
     *
     * LOCKED FIELDS (not editable): name, brand, model, year, licensePlate
     * EDITABLE FIELDS: color, carType, transmissionType, fuelType, seats,
     *                  fuelConsumption, mileage, pricePerDay, description,
     *                  province, city, district, ward, address, amenities
     */
    @PostMapping("/owner/cars/{id}/edit")
    public String processEditCar(
            @PathVariable Long id,
            @ModelAttribute("car") Car updatedCar,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        log.info("Owner updating car: {}", id);

        // Validate owner is authenticated
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để chỉnh sửa xe!");
            return "redirect:/login";
        }

        Long ownerId = userDetails.getId();

        // Find existing car
        Car existingCar = carRepository.findById(id).orElse(null);

        if (existingCar == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy xe!");
            return "redirect:/owner/owner-car-list";
        }

        // Verify car belongs to this owner
        if (!existingCar.getOwnerId().equals(ownerId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền chỉnh sửa xe này!");
            return "redirect:/owner/owner-car-list";
        }

        // ============================================================
        // IMPORTANT: DO NOT update locked fields (name, brand, model, year, licensePlate)
        // These fields are preserved from the original car registration
        // ============================================================

        // EDITABLE: Basic info (excluding locked fields)
        existingCar.setColor(updatedCar.getColor());
        existingCar.setCarType(updatedCar.getCarType());
        existingCar.setTransmissionType(updatedCar.getTransmissionType());
        existingCar.setFuelType(updatedCar.getFuelType());
        existingCar.setFuelConsumption(updatedCar.getFuelConsumption());
        existingCar.setSeats(updatedCar.getSeats());

        // EDITABLE: Price
        existingCar.setPricePerDay(updatedCar.getPricePerDay());

        // EDITABLE: Description
        existingCar.setDescription(updatedCar.getDescription());

        // EDITABLE: Location info
        existingCar.setProvince(updatedCar.getProvince());
        existingCar.setCity(updatedCar.getCity());
        existingCar.setDistrict(updatedCar.getDistrict());
        existingCar.setWard(updatedCar.getWard());
        existingCar.setAddress(updatedCar.getAddress());

        // EDITABLE: Technical info
        existingCar.setMileage(updatedCar.getMileage());

        // EDITABLE: Utilities (amenities)
        existingCar.setHasAirConditioner(updatedCar.getHasAirConditioner());
        existingCar.setHasGPS(updatedCar.getHasGPS());
        existingCar.setHasBluetooth(updatedCar.getHasBluetooth());
        existingCar.setHasDashCam(updatedCar.getHasDashCam());
        existingCar.setHasReverseCamera(updatedCar.getHasReverseCamera());
        existingCar.setHasUSB(updatedCar.getHasUSB());
        existingCar.setHasMaps(updatedCar.getHasMaps());
        existingCar.setHas360Camera(updatedCar.getHas360Camera());
        existingCar.setHasSpareWheel(updatedCar.getHasSpareWheel());
        existingCar.setHasDVDPlayer(updatedCar.getHasDVDPlayer());
        existingCar.setHasETC(updatedCar.getHasETC());
        existingCar.setHasSunroof(updatedCar.getHasSunroof());

        // Save updated car
        carRepository.save(existingCar);

        log.info("Car {} updated successfully by owner {} (locked fields preserved: name={}, brand={}, model={}, year={}, licensePlate={})",
                id, ownerId, existingCar.getName(), existingCar.getBrand(), existingCar.getModel(),
                existingCar.getYear(), existingCar.getLicensePlate());
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin xe thành công!");

        return "redirect:/owner/cars/" + id;
    }

    /**
     * View car detail page for owner
     * GET /owner/cars/{id}
     */
    @GetMapping("/owner/cars/{id}")
    public String showCarDetailPage(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Owner viewing car detail: {}", id);

        // Validate owner is authenticated
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để xem chi tiết xe!");
            return "redirect:/login";
        }

        Long ownerId = userDetails.getId();

        // Find car and verify ownership
        Car car = carRepository.findById(id).orElse(null);

        if (car == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy xe!");
            return "redirect:/owner/owner-car-list";
        }

        // Verify car belongs to this owner
        if (!car.getOwnerId().equals(ownerId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xem xe này!");
            return "redirect:/owner/owner-car-list";
        }

        // Get car documents
        List<CarDocument> documents = carDocumentRepository.findByCarId(id);
        log.info("Found {} documents for car {}", documents.size(), id);

        model.addAttribute("car", car);
        model.addAttribute("documents", documents);

        return "owner/owner-car-detail";
    }
}