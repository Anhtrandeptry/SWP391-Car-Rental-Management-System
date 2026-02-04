package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarImage;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.repository.CarImageRepository;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarImageService {

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;


    public List<CarImage> getImagesByCar(Long carId, User owner) {
        Car car = getOwnedCar(carId, owner);
        return carImageRepository.findByCarOrderByDisplayOrderAsc(car);
    }


    public void uploadImages(Long carId, User owner, List<MultipartFile> files) throws Exception {
        Car car = getOwnedCar(carId, owner);

        int order = carImageRepository.findByCarOrderByDisplayOrderAsc(car).size();

        String uploadDir = "uploads/cars/" + carId;
        new File(uploadDir).mkdirs();

        for (MultipartFile file : files) {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + "/" + filename);
            Files.copy(file.getInputStream(), dest.toPath());

            CarImage image = CarImage.builder()
                    .car(car)
                    .imageUrl("/" + uploadDir + "/" + filename)
                    .displayOrder(order++)
                    .isMain(false)
                    .build();

            carImageRepository.save(image);
        }
    }

    public void setMainImage(Long imageId, User owner) {
        CarImage image = carImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Car car = image.getCar();
        validateOwner(car, owner);

        // unset old main
        carImageRepository.findByCarAndIsMainTrue(car)
                .forEach(img -> {
                    img.setIsMain(false);
                    carImageRepository.save(img);
                });

        image.setIsMain(true);
        carImageRepository.save(image);
    }

    public void deleteImage(Long imageId, User owner) {
        CarImage image = carImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        validateOwner(image.getCar(), owner);
        carImageRepository.delete(image);
    }


    private Car getOwnedCar(Long carId, User owner) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        validateOwner(car, owner);
        return car;
    }

    private void validateOwner(Car car, User owner) {
        if (!car.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Access denied");
        }
    }
}
