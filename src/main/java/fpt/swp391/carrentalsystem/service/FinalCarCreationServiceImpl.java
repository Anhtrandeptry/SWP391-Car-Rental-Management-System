//    package fpt.swp391.carrentalsystem.service;
//
//    import fpt.swp391.carrentalsystem.dto.request.FinalCarSubmitDTO;
//    import fpt.swp391.carrentalsystem.dto.response.CarResponseDTO;
//    import fpt.swp391.carrentalsystem.entity.Car;
//    import fpt.swp391.carrentalsystem.repository.CarRepository;
//    import lombok.RequiredArgsConstructor;
//    import org.springframework.stereotype.Service;
//    import org.springframework.transaction.annotation.Transactional;
//
//    import java.math.BigDecimal;
//    import java.time.LocalDate;
//    import java.util.Map;
//
//    @Service
//    @RequiredArgsConstructor
//    @Transactional
//    public class FinalCarCreationServiceImpl implements FinalCarCreationService {
//
//        private final CarRepository carRepository;
//        private final CarDocumentService carDocumentService;
//
//        @Override
//        @Transactional
//        public CarResponseDTO createCompleteCar(FinalCarSubmitDTO submitDTO) {
//            if (submitDTO == null || submitDTO.getOwnerId() == null) {
//                throw new IllegalArgumentException("Dữ liệu submit hoặc ID chủ xe không được trống");
//            }
//
//            Map<String, Object> step1 = submitDTO.getStep1Data();
//            Map<String, Object> step2 = submitDTO.getStep2Data();
//
//            if (step1 == null || step1.isEmpty() || step2 == null || step2.isEmpty()) {
//                throw new IllegalArgumentException("Dữ liệu các bước trước đó không đầy đủ trong Session.");
//            }
//
//            // Tạo Car entity
//            Car car = new Car();
//
//            // ================= MAP DATA STEP 1 =================
//            car.setOwnerId(submitDTO.getOwnerId());
//
//            // Lưu ý: Key phải khớp với key bạn đặt trong Session ở Step 1
//            String brand = getString(step1, "brand");
//            String model = getString(step1, "model");
//            car.setBrand(brand);
//            car.setModel(model);
//            car.setYear(getInteger(step1, "year"));
//            car.setCity(getString(step1, "city"));
//            car.setLicensePlate(getString(step1, "licensePlate"));
//            car.setColor(getString(step1, "color"));
//            car.setTransmissionType(getString(step1, "transmissionType"));
//            car.setSeats(getInteger(step1, "seats"));
//
//            String fuelTypeStr = getString(step1, "fuelType");
//            if (fuelTypeStr != null) car.setFuelType(mapFuelType(fuelTypeStr));
//
//            // Tạo tên xe tự động: "Toyota Camry"
//            car.setName(((brand != null ? brand : "") + " " + (model != null ? model : "")).trim());
//
//            // ================= MAP DATA STEP 2 =================
//            car.setPricePerDay(getBigDecimal(step2, "pricePerDay")); // Thường giá nằm ở bước 2 hoặc 3
//            car.setAddress(getString(step2, "address"));
//            car.setDistrict(getString(step2, "district"));
//            car.setWard(getString(step2, "ward"));
//
//            // Hợp nhất địa chỉ hiển thị
//            String fullAddress = String.format("%s, %s, %s, %s",
//                            car.getAddress(), car.getWard(), car.getDistrict(), car.getCity())
//                    .replaceAll(", null", "").replaceAll("^, ", "");
//            car.setLocation(fullAddress);
//
//            car.setLatitude(getString(step2, "latitude"));
//            car.setLongitude(getString(step2, "longitude"));
//            car.setDescription(getString(step2, "description"));
//
//            // Tiện nghi (Boolean)
//            car.setHasAirConditioner(getBoolean(step2, "hasAirConditioner"));
//            car.setHasGPS(getBoolean(step2, "hasGPS"));
//            car.setHasBluetooth(getBoolean(step2, "hasBluetooth"));
//            // ... (Bạn có thể set thêm các field khác tương tự)
//
//            // Trạng thái mặc định khi mới tạo
//            car.setStatus(Car.CarStatus.PENDING);
//            car.setRegistrationDate(LocalDate.now());
//
//            // 3. LƯU DATABASE
//            Car savedCar = carRepository.save(car);
//
//            // 4. Liên kết giấy tờ (nếu có)
//            carDocumentService.attachDocumentsToCar(submitDTO.getOwnerId(), savedCar.getId());
//
//            return new CarResponseDTO(savedCar);
//        }
//
//        // Helper methods (Giữ nguyên như code cũ của bạn vì đã xử lý null tốt)
//        private String getString(Map<String, Object> map, String key) {
//            Object v = map.get(key); return v != null ? v.toString().trim() : null;
//        }
//        private Integer getInteger(Map<String, Object> map, String key) {
//            try { Object v = map.get(key); return (v instanceof Number) ? ((Number) v).intValue() : Integer.parseInt(v.toString()); }
//            catch (Exception e) { return null; }
//        }
//        private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
//            try { Object v = map.get(key); return new BigDecimal(v.toString()); }
//            catch (Exception e) { return null; }
//        }
//        private Boolean getBoolean(Map<String, Object> map, String key) {
//            Object v = map.get(key); return v instanceof Boolean ? (Boolean) v : Boolean.parseBoolean(String.valueOf(v));
//        }
//        private Car.FuelType mapFuelType(String fuelType) {
//            if (fuelType == null) return Car.FuelType.PETROL;
//            switch (fuelType.toLowerCase()) {
//                case "diesel": case "dầu": return Car.FuelType.DIESEL;
//                case "electric": case "điện": return Car.FuelType.ELECTRIC;
//                default: return Car.FuelType.PETROL;
//            }
//        }
//    }

package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.FinalCarSubmitDTO;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDTO;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.repository.CarRepository;
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

    private final CarRepository carRepository;
    private final CarDocumentService carDocumentService;

    @Override
    @Transactional
    public CarResponseDTO createCompleteCar(FinalCarSubmitDTO submitDTO) {
        // 1. Kiểm tra đầu vào
        if (submitDTO == null || submitDTO.getOwnerId() == null) {
            throw new IllegalArgumentException("Dữ liệu gửi lên hoặc ID chủ xe không được để trống.");
        }

        Map<String, Object> step1 = submitDTO.getStep1Data();
        Map<String, Object> step2 = submitDTO.getStep2Data();
       // Map<String, Object> step3 = submitDTO.getStep3Data(); // Dữ liệu từ giao diện Step 3 bạn gửi

        if (step1 == null || step1.isEmpty() || step2 == null || step2.isEmpty()) {
            throw new IllegalArgumentException("Dữ liệu Step 1 hoặc Step 2 không tìm thấy trong sessionStorage.");
        }

        // 2. Khởi tạo Entity Car
        Car car = new Car();

        // ================= MAP DATA STEP 1 (Thông tin cơ bản) =================
        car.setOwnerId(submitDTO.getOwnerId());

        // Hỗ trợ cả 2 key: brand/brandName và model/modelName
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
        car.setPricePerDay(getBigDecimal(step1, "pricePerDay")); // Theo ảnh bạn gửi, giá nhập ở bước 1 hoặc 2
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
        // Vì đây là bước cuối, ta thiết lập trạng thái mặc định
        car.setStatus(CarStatus.PENDING);
        car.setRegistrationDate(LocalDate.now());

        // 3. LƯU XE VÀO DATABASE
        Car savedCar = carRepository.save(car);

        // 4. LIÊN KẾT GIẤY TỜ XE
        // Hàm này sẽ lấy các file tạm đã upload trước đó của Owner và gán ID xe vào
        carDocumentService.attachDocumentsToCar(submitDTO.getOwnerId(), savedCar.getId());

        return new CarResponseDTO(savedCar);
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
            // Xóa sạch ký tự lạ, chỉ giữ số và dấu chấm
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

    private Car.FuelType mapFuelType(String fuelType) {
        if (fuelType == null) return Car.FuelType.PETROL;
        switch (fuelType.toLowerCase()) {
            case "xăng": case "petrol": return Car.FuelType.PETROL;
            case "dầu": case "diesel": return Car.FuelType.DIESEL;
            case "điện": case "electric": return Car.FuelType.ELECTRIC;
            default: return Car.FuelType.PETROL;
        }
    }
}