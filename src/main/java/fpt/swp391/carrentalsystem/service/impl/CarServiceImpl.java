package fpt.swp391.carrentalsystem.service.impl;

import fpt.swp391.carrentalsystem.dto.CarListItemDto;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarImage;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.service.CarService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<CarListItemDto> listAll() {
        return carRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<CarListItemDto> searchByName(String name) {
        return carRepository.findByNameContainingIgnoreCase(name).stream().map(this::toDto).collect(Collectors.toList());
    }

    private CarListItemDto toDto(Car car) {
        String mainImage = null;
        if (car.getImages() != null && !car.getImages().isEmpty()) {
            for (CarImage img : car.getImages()) {
                if (Boolean.TRUE.equals(img.getIsMain())) {
                    mainImage = img.getImageUrl();
                    break;
                }
            }
            if (mainImage == null && !car.getImages().isEmpty()) {
                mainImage = car.getImages().get(0).getImageUrl();
            }
        }

        BigDecimal avg = car.getAverageRating();
        if (avg == null) avg = BigDecimal.ZERO;

        return CarListItemDto.builder()
                .id(car.getId())
                .name(car.getName())
                .brand(car.getBrand())
                .model(car.getModel())
                .location(car.getLocation())
                .pricePerDay(car.getPricePerDay())
                .averageRating(avg)
                .mainImageUrl(mainImage)
                .build();
    }
}
