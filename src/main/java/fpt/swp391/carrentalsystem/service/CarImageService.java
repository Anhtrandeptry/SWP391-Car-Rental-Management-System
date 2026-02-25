package fpt.swp391.carrentalsystem.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarImage;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.repository.CarImageRepository;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CarImageService {

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;
    private final Cloudinary cloudinary;

    public List<CarImage> getImagesByCar(Long carId, User owner) {
        Car car = getOwnedCar(carId, owner);
        return carImageRepository.findByCarOrderByDisplayOrderAsc(car);
    }

    public void uploadImages(Long carId, User owner, List<MultipartFile> files) throws Exception {
        Car car = getOwnedCar(carId, owner);

        String folderName = String.format("cars/%d_%s_%s",
                car.getId(),
                slugify(car.getName()),
                slugify(owner.getFirstName() + " " + owner.getLastName()));

        int order = carImageRepository.findByCarOrderByDisplayOrderAsc(car).size();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folderName,
                    "resource_type", "auto"
            ));

            String url = (String) uploadResult.get("secure_url");

            CarImage image = CarImage.builder()
                    .car(car)
                    .imageUrl(url)
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

    private String slugify(String input) {
        if (input == null || input.isEmpty()) return "unknown";

        String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("^-+|-+$", "");
    }
}