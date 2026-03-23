package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.LocationDTO;

import java.util.List;

/**
 * Service để lấy dữ liệu địa giới hành chính Việt Nam
 * Sử dụng API: https://provinces.open-api.vn/
 */
public interface LocationService {

    /**
     * Lấy danh sách tất cả tỉnh/thành phố
     * @return danh sách tỉnh/thành phố
     */
    List<LocationDTO> getAllProvinces();

    /**
     * Lấy danh sách quận/huyện theo mã tỉnh
     * @param provinceCode mã tỉnh/thành phố
     * @return danh sách quận/huyện
     */
    List<LocationDTO> getDistrictsByProvince(String provinceCode);

    /**
     * Lấy danh sách phường/xã theo mã quận/huyện
     * @param districtCode mã quận/huyện
     * @return danh sách phường/xã
     */
    List<LocationDTO> getWardsByDistrict(String districtCode);

    /**
     * Lấy tên tỉnh/thành phố theo mã
     * @param provinceCode mã tỉnh
     * @return tên tỉnh hoặc null nếu không tìm thấy
     */
    String getProvinceName(String provinceCode);

    /**
     * Lấy tên quận/huyện theo mã
     * @param districtCode mã quận/huyện
     * @return tên quận/huyện hoặc null nếu không tìm thấy
     */
    String getDistrictName(String districtCode);

    /**
     * Lấy tên phường/xã theo mã
     * @param wardCode mã phường/xã
     * @return tên phường/xã hoặc null nếu không tìm thấy
     */
    String getWardName(String wardCode);
}
