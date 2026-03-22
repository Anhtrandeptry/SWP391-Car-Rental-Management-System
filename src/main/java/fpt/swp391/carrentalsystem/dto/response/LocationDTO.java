package fpt.swp391.carrentalsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho dữ liệu địa giới hành chính (tỉnh, huyện, xã)
 * Dùng để lưu trữ dữ liệu từ API provinces.open-api.vn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {

    /**
     * Mã địa giới hành chính (code từ API)
     */
    private String code;

    /**
     * Tên địa giới hành chính
     */
    private String name;

    /**
     * Constructor từ code và name
     */
    public LocationDTO(Integer code, String name) {
        this.code = String.valueOf(code);
        this.name = name;
    }
}
