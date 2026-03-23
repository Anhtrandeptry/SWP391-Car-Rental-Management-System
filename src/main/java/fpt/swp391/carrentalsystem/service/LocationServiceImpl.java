package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.LocationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Implementation của LocationService
 * Lấy dữ liệu từ API: https://provinces.open-api.vn/
 */
@Service
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final RestTemplate restTemplate;

    // API URLs
    private static final String PROVINCES_API = "https://provinces.open-api.vn/api/p/";
    private static final String PROVINCE_DETAIL_API = "https://provinces.open-api.vn/api/p/";
    private static final String DISTRICT_DETAIL_API = "https://provinces.open-api.vn/api/d/";

    public LocationServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Cacheable(value = "provinces", unless = "#result == null || #result.isEmpty()")
    @SuppressWarnings("unchecked")
    public List<LocationDTO> getAllProvinces() {
        try {
            log.info("Fetching all provinces from API...");
            List<Map<String, Object>> response = restTemplate.getForObject(PROVINCES_API, List.class);
            List<LocationDTO> provinces = new ArrayList<>();

            if (response != null) {
                for (Map<String, Object> item : response) {
                    LocationDTO dto = new LocationDTO();
                    dto.setCode(String.valueOf(item.get("code")));
                    dto.setName((String) item.get("name"));
                    provinces.add(dto);
                }
                // Sắp xếp theo tên
                provinces.sort(Comparator.comparing(LocationDTO::getName));
                log.info("Successfully loaded {} provinces", provinces.size());
            }
            return provinces;
        } catch (Exception e) {
            log.error("Error fetching provinces: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @Cacheable(value = "districts", key = "#provinceCode", unless = "#result == null || #result.isEmpty()")
    @SuppressWarnings("unchecked")
    public List<LocationDTO> getDistrictsByProvince(String provinceCode) {
        if (provinceCode == null || provinceCode.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            log.info("Fetching districts for province: {}", provinceCode);
            String url = PROVINCE_DETAIL_API + provinceCode + "?depth=2";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<LocationDTO> districts = new ArrayList<>();

            if (response != null && response.containsKey("districts")) {
                List<Map<String, Object>> districtList = (List<Map<String, Object>>) response.get("districts");
                for (Map<String, Object> item : districtList) {
                    LocationDTO dto = new LocationDTO();
                    dto.setCode(String.valueOf(item.get("code")));
                    dto.setName((String) item.get("name"));
                    districts.add(dto);
                }
                // Sắp xếp theo tên
                districts.sort(Comparator.comparing(LocationDTO::getName));
                log.info("Successfully loaded {} districts for province {}", districts.size(), provinceCode);
            }
            return districts;
        } catch (Exception e) {
            log.error("Error fetching districts for province {}: {}", provinceCode, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @Cacheable(value = "wards", key = "#districtCode", unless = "#result == null || #result.isEmpty()")
    @SuppressWarnings("unchecked")
    public List<LocationDTO> getWardsByDistrict(String districtCode) {
        if (districtCode == null || districtCode.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            log.info("Fetching wards for district: {}", districtCode);
            String url = DISTRICT_DETAIL_API + districtCode + "?depth=2";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<LocationDTO> wards = new ArrayList<>();

            if (response != null && response.containsKey("wards")) {
                List<Map<String, Object>> wardList = (List<Map<String, Object>>) response.get("wards");
                for (Map<String, Object> item : wardList) {
                    LocationDTO dto = new LocationDTO();
                    dto.setCode(String.valueOf(item.get("code")));
                    dto.setName((String) item.get("name"));
                    wards.add(dto);
                }
                // Sắp xếp theo tên
                wards.sort(Comparator.comparing(LocationDTO::getName));
                log.info("Successfully loaded {} wards for district {}", wards.size(), districtCode);
            }
            return wards;
        } catch (Exception e) {
            log.error("Error fetching wards for district {}: {}", districtCode, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getProvinceName(String provinceCode) {
        if (provinceCode == null || provinceCode.trim().isEmpty()) {
            return null;
        }

        try {
            String url = PROVINCE_DETAIL_API + provinceCode;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("name")) {
                return (String) response.get("name");
            }
        } catch (Exception e) {
            log.error("Error fetching province name for code {}: {}", provinceCode, e.getMessage());
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getDistrictName(String districtCode) {
        if (districtCode == null || districtCode.trim().isEmpty()) {
            return null;
        }

        try {
            String url = DISTRICT_DETAIL_API + districtCode;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("name")) {
                return (String) response.get("name");
            }
        } catch (Exception e) {
            log.error("Error fetching district name for code {}: {}", districtCode, e.getMessage());
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getWardName(String wardCode) {
        if (wardCode == null || wardCode.trim().isEmpty()) {
            return null;
        }

        try {
            String url = "https://provinces.open-api.vn/api/w/" + wardCode;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("name")) {
                return (String) response.get("name");
            }
        } catch (Exception e) {
            log.error("Error fetching ward name for code {}: {}", wardCode, e.getMessage());
        }
        return null;
    }
}
