package fpt.swp391.carrentalsystem.service;



import fpt.swp391.carrentalsystem.dto.request.CarInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CarInfoServiceImpl implements CarInfoService {

    @Override
    public void validateStep2(CarInfoDTO carInfoDTO) {
        // Kiểm tra số km hợp lý theo năm xe
        if (carInfoDTO.getMileage() != null && carInfoDTO.getYear() != null) {
            int carAge = 2025 - carInfoDTO.getYear();
            int maxMileage = carAge * 10000; // Giả sử trung bình 20,000 km/năm

            if (carInfoDTO.getMileage() > maxMileage) {
                throw new IllegalArgumentException(
                        "Số km quá lớn so với năm xe (" + maxMileage + " km tối đa cho xe " + carAge + " năm tuổi)"
                );
            }
        }

        // Kiểm tra giới hạn km và phụ phí
        if (carInfoDTO.getDailyKmLimit() != null && carInfoDTO.getDailyKmLimit() < 50) {
            throw new IllegalArgumentException("Giới hạn km/ngày tối thiểu là 50km");
        }

        if (carInfoDTO.getOverLimitFee() != null &&
                carInfoDTO.getOverLimitFee().doubleValue() < 1000) {
            throw new IllegalArgumentException("Phụ phí vượt km tối thiểu 1,000 VNĐ/km");
        }

    }

    @Override
    public CarInfoDTO saveDraftStep2(CarInfoDTO carInfoDTO) {
        // TODO: Implement save to database or cache (Redis)
        // For now, just return the same data
        return carInfoDTO;
    }

    @Override
    public CarInfoDTO getDraftStep2(Long ownerId) {
        // TODO: Implement get from database or cache
        return null;
    }
}