  package fpt.swp391.carrentalsystem.config;

import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.enums.FuelType;
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ONE-TIME INITIALIZER: Insert sample car data into database
 * After running, please delete this class to prevent duplicate insertions
 * This class automatically checks if a CAR_OWNER exists before inserting
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OneTimeCarDataInitializer implements CommandLineRunner {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if cars already exist to prevent duplicate insertion
        if (carRepository.count() > 0) {
            log.info("Database already contains cars. Skipping initialization.");
            return;
        }

        log.info("========== Starting OneTime Car Data Initialization ==========");

        // Find a CAR_OWNER user
        List<User> owners = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.CAR_OWNER)
                .toList();

        if (owners.isEmpty()) {
            log.warn("⚠️  Chưa thêm vì không có Owner - Không tìm thấy user nào có role CAR_OWNER");
            log.info("========== Car Data Initialization Failed ==========");
            return;
        }

        User owner = owners.get(0); // Get first CAR_OWNER
        log.info("Found CAR_OWNER: {} (ID: {})", owner.getFirstName() + " " + owner.getLastName(), owner.getId());

        // Create sample cars
        Car[] carsData = {
            Car.builder()
                    .owner(owner)
                    .name("Toyota Camry")
                    .brand("Toyota")
                    .model("Camry 2024")
                    .carType("Sedan")
                    .fuelType(FuelType.PETROL)
                    .fuelConsumption("7.5L/100km")
                    .seats(5)
                    .pricePerDay(BigDecimal.valueOf(1500000))
                    .location("Hà Nội")
                    .description("Xe sedan sang trọng, tiện nghi, phù hợp cho chuyến đi dài")
                    .registrationDate(LocalDate.of(2023, 1, 15))
                    .licensePlate("30F-12345")
                    .status(CarStatus.AVAILABLE)
                    .averageRating(BigDecimal.valueOf(4.5))
                    .build(),

            Car.builder()
                    .owner(owner)
                    .name("Honda Accord")
                    .brand("Honda")
                    .model("Accord 2024")
                    .carType("Sedan")
                    .fuelType(FuelType.PETROL)
                    .fuelConsumption("8L/100km")
                    .seats(5)
                    .pricePerDay(BigDecimal.valueOf(1300000))
                    .location("TP. Hồ Chí Minh")
                    .description("Xe sedan cao cấp, an toàn, tiết kiệm nhiên liệu")
                    .registrationDate(LocalDate.of(2023, 3, 20))
                    .licensePlate("51F-54321")
                    .status(CarStatus.AVAILABLE)
                    .averageRating(BigDecimal.valueOf(4.7))
                    .build(),

            Car.builder()
                    .owner(owner)
                    .name("Kia Sorento")
                    .brand("Kia")
                    .model("Sorento 2024")
                    .carType("SUV")
                    .fuelType(FuelType.DIESEL)
                    .fuelConsumption("8.5L/100km")
                    .seats(7)
                    .pricePerDay(BigDecimal.valueOf(2000000))
                    .location("Đà Nẵng")
                    .description("Xe SUV 7 chỗ rộng rãi, phù hợp cho gia đình lớn")
                    .registrationDate(LocalDate.of(2022, 6, 10))
                    .licensePlate("43F-99999")
                    .status(CarStatus.AVAILABLE)
                    .averageRating(BigDecimal.valueOf(4.8))
                    .build(),

            Car.builder()
                    .owner(owner)
                    .name("Hyundai Elantra")
                    .brand("Hyundai")
                    .model("Elantra 2024")
                    .carType("Sedan")
                    .fuelType(FuelType.PETROL)
                    .fuelConsumption("6.5L/100km")
                    .seats(5)
                    .pricePerDay(BigDecimal.valueOf(1200000))
                    .location("Hải Phòng")
                    .description("Xe sedan nhỏ gọn, kinh tế, dễ di chuyển trong phố")
                    .registrationDate(LocalDate.of(2023, 8, 5))
                    .licensePlate("36F-88888")
                    .status(CarStatus.AVAILABLE)
                    .averageRating(BigDecimal.valueOf(4.3))
                    .build(),

            Car.builder()
                    .owner(owner)
                    .name("Mazda CX-5")
                    .brand("Mazda")
                    .model("CX-5 2024")
                    .carType("SUV")
                    .fuelType(FuelType.PETROL)
                    .fuelConsumption("7.8L/100km")
                    .seats(5)
                    .pricePerDay(BigDecimal.valueOf(1800000))
                    .location("Cần Thơ")
                    .description("Xe SUV thể thao, hiệu năng cao, thiết kế hiện đại")
                    .registrationDate(LocalDate.of(2023, 5, 12))
                    .licensePlate("92F-77777")
                    .status(CarStatus.AVAILABLE)
                    .averageRating(BigDecimal.valueOf(4.6))
                    .build(),

            Car.builder()
                    .owner(owner)
                    .name("Toyota Innova")
                    .brand("Toyota")
                    .model("Innova 2024")
                    .carType("MPV")
                    .fuelType(FuelType.DIESEL)
                    .fuelConsumption("9L/100km")
                    .seats(7)
                    .pricePerDay(BigDecimal.valueOf(1700000))
                    .location("Hà Nội")
                    .description("Xe MPV 7 chỗ đa dụng, phù hợp cho gia đình và công tác")
                    .registrationDate(LocalDate.of(2023, 2, 8))
                    .licensePlate("30F-66666")
                    .status(CarStatus.AVAILABLE)
                    .averageRating(BigDecimal.valueOf(4.4))
                    .build()
        };

        // Insert all cars
        try {
            for (Car car : carsData) {
                carRepository.save(car);
            }
            log.info("✅ Thêm thành công - {} xe đã được thêm vào database", carsData.length);
            log.info("========== Car Data Initialization Completed Successfully ==========");
        } catch (Exception e) {
            log.error("❌ Lỗi khi thêm dữ liệu xe: {}", e.getMessage(), e);
            log.info("========== Car Data Initialization Failed ==========");
        }
    }
}

