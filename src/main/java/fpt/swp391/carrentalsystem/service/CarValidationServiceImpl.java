package fpt.swp391.carrentalsystem.service;


import fpt.swp391.carrentalsystem.dto.request.CarSetupDTO;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarValidationServiceImpl implements CarValidationService {

    private final CarRepository carRepository;

    @Override
    public boolean isLicensePlateExists(String licensePlate) {
        return carRepository.existsByLicensePlate(licensePlate);
    }

    @Override
    public void validateStep1(CarSetupDTO carSetupDTO) {
        // Kiểm tra biển số đã tồn tại
        if (isLicensePlateExists(carSetupDTO.getLicensePlate())) {
            throw new IllegalArgumentException("Biển số xe '" + carSetupDTO.getLicensePlate() + "' đã tồn tại trong hệ thống");
        }

        // Kiểm tra giá hợp lý
        if (carSetupDTO.getPricePerDay().doubleValue() < 100000) {
            throw new IllegalArgumentException("Giá thuê tối thiểu là 100,000 VNĐ/ngày");
        }

        if (carSetupDTO.getPricePerDay().doubleValue() > 50000000) {
            throw new IllegalArgumentException("Giá thuê tối đa là 50,000,000 VNĐ/ngày");
        }

        // Kiểm tra số chỗ hợp lệ
        Integer[] validSeats = {2, 4, 5, 7, 9, 16};
        boolean isValidSeats = false;
        for (Integer seat : validSeats) {
            if (seat.equals(carSetupDTO.getSeats())) {
                isValidSeats = true;
                break;
            }
        }

        if (!isValidSeats) {
            throw new IllegalArgumentException("Số chỗ ngồi không hợp lệ. Chỉ chấp nhận: 2, 4, 5, 7, 9, 16 chỗ");
        }
    }
}

