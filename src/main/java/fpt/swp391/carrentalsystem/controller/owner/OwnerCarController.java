package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.dto.request.FinalCarSubmitDTO;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.security.CustomUserDetails;
import fpt.swp391.carrentalsystem.service.FinalCarCreationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OwnerCarController {

    private final FinalCarCreationService finalCarCreationService;
    private final CarRepository carRepository;

    // ======================================================
    // 1. ĐIỀU HƯỚNG GIAO DIỆN (VIEW)
    // ======================================================

    @GetMapping("/owner-car-list")
    public String showCarListPage() {
        // Trả về file owner-car-list.html trong folder templates
        return "owner/owner-car-list";
    }

    // ======================================================
    // 2. API XỬ LÝ DỮ LIỆU (REST)
    // ======================================================

    /**
     * API nhận toàn bộ dữ liệu từ Step 3 (gom cả sessionStorage từ Frontend)
     */
    @PostMapping("/api/owner/complete-car-registration")
    @ResponseBody
    public ResponseEntity<?> completeRegistration(@RequestBody FinalCarSubmitDTO submitDTO) {
        try {
            // Gọi Service bạn đã viết để lưu vào Database
            finalCarCreationService.createCompleteCar(submitDTO);

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
     * API lấy danh sách xe cho trang owner-car-list.html
     */
    @GetMapping("/api/owner/cars")
    @ResponseBody
    public ResponseEntity<?> getOwnerCars(HttpSession session) {
        // Lấy ownerId từ Session (giả định bạn lưu khi đăng nhập)
        Long ownerId = (Long) session.getAttribute("userId");
        if (ownerId == null) ownerId = 1L; // Giá trị mặc định để test nếu chưa có login

        // Sử dụng phương thức findByOwnerId có sẵn trong Repository của bạn
        List<Car> cars = carRepository.findByOwnerId(ownerId);

        // Trả về đúng cấu trúc JSON mà file HTML đang chờ (result.success và result.data)
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", cars
        ));
    }

    @DeleteMapping("/api/owner/cars/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails // Lấy thẳng user ở đây
    ) {
        // 1. Kiểm tra nếu chưa login (userDetails sẽ null)
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Bạn chưa đăng nhập"));
        }

        // 2. Lấy ID trực tiếp cực kỳ ngắn gọn
        Long ownerId = userDetails.getId();

        // 3. Gọi Repository thực hiện xóa mềm (như Bước 1 đã làm)
        int updatedRows = carRepository.softDeleteCar(id, ownerId);

        if (updatedRows > 0) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Xóa xe thành công!"));
        } else {
            return ResponseEntity.status(403).body(Map.of("message", "Không tìm thấy xe hoặc bạn không có quyền xóa"));
        }
    }
    // 1. API lấy dữ liệu 1 xe
    @GetMapping("/api/owner/cars/{id}")
    @ResponseBody
    public ResponseEntity<?> getCarDetail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Car car = carRepository.findById(id).orElse(null);
        if (car != null && car.getOwnerId().equals(userDetails.getId())) {
            return ResponseEntity.ok(Map.of("success", true, "data", car));
        }
        return ResponseEntity.status(404).body(Map.of("message", "Xe không tồn tại"));
    }

    // 2. API Cập nhật (PUT)
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
        // Chúng ta truyền ID vào model để Frontend có thể lấy ra dùng
        model.addAttribute("carId", id);
        return "owner/owner-edit-car"; // Trả về file owner-edit-car.html
    }
}