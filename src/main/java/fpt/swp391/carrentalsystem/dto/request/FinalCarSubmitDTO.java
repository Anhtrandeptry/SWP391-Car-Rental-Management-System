package fpt.swp391.carrentalsystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalCarSubmitDTO {

    // Chứa: brand, model, year, licensePlate, color, transmissionType, fuelType, seats, pricePerDay, description
    private Map<String, Object> step1Data;

    // Chứa: address, city, district, ward (hoặc các field bạn đã lưu ở Step 2)
    private Map<String, Object> step2Data;

    // Danh sách ID của các tài liệu đã upload thành công (REGISTRATION, INSURANCE, etc.)
    private List<Long> documentIds;

    // ID của người dùng đang thực hiện đăng ký
    private Long ownerId;
}