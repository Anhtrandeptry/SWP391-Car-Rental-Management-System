package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.FinalCarSubmitDTO;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.enums.FuelType;
import fpt.swp391.carrentalsystem.mapper.CarMapper;
import fpt.swp391.carrentalsystem.repository.CarRepositoryByThinhHT;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class FinalCarCreationServiceImpl implements FinalCarCreationService {

    private final CarRepositoryByThinhHT carRepositoryByThinhHT;
    private final CarDocumentService carDocumentService;
    private final UserRepository userRepository;
    private final CarMapper carMapper;

    @Override
    @Transactional
    public CarResponseDto createCompleteCar(FinalCarSubmitDTO submitDTO) {
        // 1. Kiểm tra đầu vào
        if (submitDTO == null || submitDTO.getOwnerId() == null) {
            throw new IllegalArgumentException("Dữ liệu gửi lên hoặc ID chủ xe không được để trống.");
        }

        Map<String, Object> step1 = submitDTO.getStep1Data();
        Map<String, Object> step2 = submitDTO.getStep2Data();

        if (step1 == null || step1.isEmpty() || step2 == null || step2.isEmpty()) {
            throw new IllegalArgumentException("Dữ liệu Step 1 hoặc Step 2 không tìm thấy trong sessionStorage.");
        }

        // Get owner user
        User owner = userRepository.findById(submitDTO.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + submitDTO.getOwnerId()));

        // 2. Khởi tạo Entity Car
        Car car = new Car();
        car.setOwner(owner);

        // ================= MAP DATA STEP 1 (Thông tin cơ bản) =================
        String brand = getString(step1, "brandName");
        if (brand == null) brand = getString(step1, "brand");

        String model = getString(step1, "modelName");
        if (model == null) model = getString(step1, "model");

        car.setBrand(brand);
        car.setModel(model);
        car.setYear(getInteger(step1, "year"));
        car.setCity(getString(step1, "city"));
        car.setLicensePlate(getString(step1, "licensePlate"));
        car.setColor(getString(step1, "color"));
        car.setTransmissionType(getString(step1, "transmissionType"));
        car.setSeats(getInteger(step1, "seats"));

        String fuelTypeStr = getString(step1, "fuelType");
        if (fuelTypeStr != null) car.setFuelType(mapFuelType(fuelTypeStr));

        // Tự động tạo tên hiển thị: VD "Honda City 1.5"
        car.setName(Stream.of(brand, model)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" ")));

        // ================= MAP DATA STEP 2 (Giá & Tiện nghi) =================
        car.setPricePerDay(getBigDecimal(step1, "pricePerDay"));
        car.setAddress(getString(step2, "address"));
        car.setProvince(getString(step2, "province"));
        car.setDistrict(getString(step2, "district"));
        car.setWard(getString(step2, "ward"));

        // Xử lý Location mượt mà (Address, Ward, District, Province/City)
        String fullLocation = Stream.of(car.getAddress(), car.getWard(), car.getDistrict(), car.getProvince())
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(", "));
        car.setLocation(fullLocation);

        // Description có thể ở step1 hoặc step2
        String description = getString(step1, "description");
        if (description == null) description = getString(step2, "description");
        car.setDescription(description);

        // Map các tiện nghi (Checkbox từ Step 2)
        car.setHasAirConditioner(getBoolean(step2, "hasAirConditioner"));
        car.setHasGPS(getBoolean(step2, "hasGPS"));
        car.setHasBluetooth(getBoolean(step2, "hasBluetooth"));
        car.setHasDashCam(getBoolean(step2, "hasDashCam"));

        // ================= MAP DATA STEP 3 (Trạng thái & Ngày tạo) =================
        car.setStatus(CarStatus.PENDING);
        car.setRegistrationDate(LocalDate.now());

        // 3. LƯU XE VÀO DATABASE
        Car savedCar = carRepositoryByThinhHT.save(car);

        // 4. LIÊN KẾT GIẤY TỜ XE
        carDocumentService.attachDocumentsToCar(submitDTO.getOwnerId(), savedCar.getCarId().longValue());

        return carMapper.toDto(savedCar);
    }

    // --- Helper Methods giúp xử lý dữ liệu từ Map an toàn ---

    private String getString(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object v = map.get(key);
        return v != null ? v.toString().trim() : null;
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        try {
            Object v = map.get(key);
            if (v == null || v.toString().isEmpty()) return BigDecimal.ZERO;
            String cleanValue = v.toString().replaceAll("[^\\d.]", "");
            return new BigDecimal(cleanValue);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        try {
            Object v = map.get(key);
            if (v == null) return 0;
            if (v instanceof Number) return ((Number) v).intValue();
            return Integer.parseInt(v.toString().replaceAll("[^\\d]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private Boolean getBoolean(Map<String, Object> map, String key) {
        if (map == null) return false;
        Object v = map.get(key);
        if (v instanceof Boolean) return (Boolean) v;
        return Boolean.parseBoolean(String.valueOf(v));
    }

    private FuelType mapFuelType(String fuelType) {
        if (fuelType == null) return FuelType.PETROL;
        switch (fuelType.toLowerCase()) {
            case "xăng": case "petrol": return FuelType.PETROL;
            case "dầu": case "diesel": return FuelType.DIESEL;
            case "điện": case "electric": return FuelType.ELECTRIC;
            case "hybrid": return FuelType.HYBRID;
            default: return FuelType.PETROL;
        }
    }
}