package fpt.swp391.carrentalsystem.config;

import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarImage;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, CarRepository carRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (carRepository.count() > 0) return; // already seeded

        String[] brands = {"Toyota", "Honda", "Ford", "BMW", "Audi", "Mercedes", "Tesla", "Hyundai", "Kia", "Subaru"};

        for (int i = 0; i < brands.length; i++) {
            String brand = brands[i];

            User owner = User.builder()
                    .firstName(brand + "Owner")
                    .lastName("Lastname")
                    .email(brand.toLowerCase() + "_owner@example.com")
                    .phoneNumber(String.format("0900%05d", i))
                    .address(brand + " Street, City")
                    .passwordHash(passwordEncoder.encode("password"))
                    .nationalId("ID" + (10000 + i))
                    .driversLicense("DL" + (20000 + i))
                    .avatarUrl("/images/avatar-placeholder.png")
                    .role(Role.CAR_OWNER)
                    .status(UserStatus.ACTIVE)
                    .build();

            userRepository.save(owner);

            Car car = new Car();
            car.setOwner(owner);
            car.setName(brand + " " + (i + 1));
            car.setBrand(brand);
            car.setModel("Model " + (i + 1));
            car.setCarType(i % 2 == 0 ? "Sedan" : "SUV");
            car.setFuelType(i == 6 ? "Electric" : "Petrol");
            car.setFuelConsumption(i == 6 ? "0" : (5 + i) + " L/100km");
            car.setSeats(4 + (i % 3));
            car.setPricePerDay(new BigDecimal(30 + i * 5));
            car.setLocation("City Center, District " + (i + 1));
            car.setDescription("This is a " + brand + " " + (i + 1) + " in excellent condition.");
            car.setRegistrationDate(LocalDate.now().minusYears(1 + i % 5));
            car.setLicensePlate("ABC-" + (1000 + i));
            car.setStatus("Available");
            car.setAverageRating(new BigDecimal("4.5"));

            List<CarImage> images = new ArrayList<>();
            CarImage img = new CarImage();
            img.setCar(car);
            img.setImageUrl("/images/cars/" + brand.toLowerCase() + ".jpg");
            img.setIsMain(true);
            img.setDisplayOrder(1);
            images.add(img);
            car.setImages(images);

            carRepository.save(car);
        }

    }
}
