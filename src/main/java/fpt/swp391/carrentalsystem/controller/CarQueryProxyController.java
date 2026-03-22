package fpt.swp391.carrentalsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * Proxy Controller để gọi CarQuery API từ backend
 * Giải quyết vấn đề CORS khi gọi API từ frontend
 */
@RestController
@RequestMapping("/api/carquery")
@RequiredArgsConstructor
public class CarQueryProxyController {

    private static final String CARQUERY_API = "https://www.carqueryapi.com/api/0.3/";

    /**
     * Lấy danh sách tất cả hãng xe
     * GET /api/carquery/makes
     */
    @GetMapping("/makes")
    public ResponseEntity<?> getAllMakes() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = CARQUERY_API + "?cmd=getMakes";

            // CarQuery API trả về JSONP, cần parse
            String response = restTemplate.getForObject(url, String.class);

            // Parse JSON từ JSONP response nếu cần
            String jsonData = extractJsonFromJsonp(response);

            return ResponseEntity.ok(jsonData);
        } catch (Exception e) {
            // Fallback: trả về danh sách brands phổ biến
            return ResponseEntity.ok(getFallbackBrands());
        }
    }

    /**
     * Lấy danh sách models theo hãng xe
     * GET /api/carquery/models?make={makeId}
     */
    @GetMapping("/models")
    public ResponseEntity<?> getModelsByMake(@RequestParam String make) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = CARQUERY_API + "?cmd=getModels&make=" + make;

            String response = restTemplate.getForObject(url, String.class);
            String jsonData = extractJsonFromJsonp(response);

            return ResponseEntity.ok(jsonData);
        } catch (Exception e) {
            // Fallback: trả về danh sách models phổ biến
            return ResponseEntity.ok(getFallbackModels(make));
        }
    }

    /**
     * Extract JSON data từ JSONP response
     */
    private String extractJsonFromJsonp(String response) {
        if (response == null) return "{}";

        // Nếu response là JSONP format: callback({...})
        // Cần extract phần JSON bên trong
        int startIndex = response.indexOf('(');
        int endIndex = response.lastIndexOf(')');

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex + 1, endIndex);
        }

        // Nếu đã là JSON thuần
        return response;
    }

    /**
     * Fallback brands khi API không hoạt động
     */
    private String getFallbackBrands() {
        return """
            {
                "Makes": [
                    {"make_id": "audi", "make_display": "Audi", "make_country": "Germany"},
                    {"make_id": "bmw", "make_display": "BMW", "make_country": "Germany"},
                    {"make_id": "chevrolet", "make_display": "Chevrolet", "make_country": "USA"},
                    {"make_id": "ford", "make_display": "Ford", "make_country": "USA"},
                    {"make_id": "honda", "make_display": "Honda", "make_country": "Japan"},
                    {"make_id": "hyundai", "make_display": "Hyundai", "make_country": "South Korea"},
                    {"make_id": "kia", "make_display": "Kia", "make_country": "South Korea"},
                    {"make_id": "lexus", "make_display": "Lexus", "make_country": "Japan"},
                    {"make_id": "mazda", "make_display": "Mazda", "make_country": "Japan"},
                    {"make_id": "mercedes-benz", "make_display": "Mercedes-Benz", "make_country": "Germany"},
                    {"make_id": "mitsubishi", "make_display": "Mitsubishi", "make_country": "Japan"},
                    {"make_id": "nissan", "make_display": "Nissan", "make_country": "Japan"},
                    {"make_id": "subaru", "make_display": "Subaru", "make_country": "Japan"},
                    {"make_id": "suzuki", "make_display": "Suzuki", "make_country": "Japan"},
                    {"make_id": "toyota", "make_display": "Toyota", "make_country": "Japan"},
                    {"make_id": "vinfast", "make_display": "VinFast", "make_country": "Vietnam"},
                    {"make_id": "volkswagen", "make_display": "Volkswagen", "make_country": "Germany"}
                ]
            }
            """;
    }

    /**
     * Fallback models khi API không hoạt động
     */
    private String getFallbackModels(String make) {
        return switch (make.toLowerCase()) {
            case "toyota" -> """
                {"Models": [
                    {"model_name": "Camry"}, {"model_name": "Corolla"}, {"model_name": "Fortuner"},
                    {"model_name": "Innova"}, {"model_name": "Land Cruiser"}, {"model_name": "RAV4"},
                    {"model_name": "Vios"}, {"model_name": "Yaris"}, {"model_name": "Hilux"}
                ]}
                """;
            case "honda" -> """
                {"Models": [
                    {"model_name": "Accord"}, {"model_name": "City"}, {"model_name": "Civic"},
                    {"model_name": "CR-V"}, {"model_name": "HR-V"}, {"model_name": "Jazz"},
                    {"model_name": "Brio"}
                ]}
                """;
            case "mazda" -> """
                {"Models": [
                    {"model_name": "CX-3"}, {"model_name": "CX-5"}, {"model_name": "CX-8"},
                    {"model_name": "CX-30"}, {"model_name": "Mazda2"}, {"model_name": "Mazda3"},
                    {"model_name": "Mazda6"}, {"model_name": "BT-50"}
                ]}
                """;
            case "hyundai" -> """
                {"Models": [
                    {"model_name": "Accent"}, {"model_name": "Creta"}, {"model_name": "Elantra"},
                    {"model_name": "Grand i10"}, {"model_name": "Kona"}, {"model_name": "Santa Fe"},
                    {"model_name": "Tucson"}, {"model_name": "Stargazer"}
                ]}
                """;
            case "kia" -> """
                {"Models": [
                    {"model_name": "Carnival"}, {"model_name": "Cerato"}, {"model_name": "K3"},
                    {"model_name": "Morning"}, {"model_name": "Seltos"}, {"model_name": "Sorento"},
                    {"model_name": "Sportage"}, {"model_name": "Sonet"}
                ]}
                """;
            case "ford" -> """
                {"Models": [
                    {"model_name": "EcoSport"}, {"model_name": "Everest"}, {"model_name": "Explorer"},
                    {"model_name": "Ranger"}, {"model_name": "Territory"}, {"model_name": "Transit"}
                ]}
                """;
            case "mercedes-benz" -> """
                {"Models": [
                    {"model_name": "A-Class"}, {"model_name": "C-Class"}, {"model_name": "E-Class"},
                    {"model_name": "S-Class"}, {"model_name": "GLA"}, {"model_name": "GLC"},
                    {"model_name": "GLE"}, {"model_name": "GLS"}
                ]}
                """;
            case "bmw" -> """
                {"Models": [
                    {"model_name": "1 Series"}, {"model_name": "3 Series"}, {"model_name": "5 Series"},
                    {"model_name": "7 Series"}, {"model_name": "X1"}, {"model_name": "X3"},
                    {"model_name": "X5"}, {"model_name": "X7"}
                ]}
                """;
            case "vinfast" -> """
                {"Models": [
                    {"model_name": "Fadil"}, {"model_name": "Lux A2.0"}, {"model_name": "Lux SA2.0"},
                    {"model_name": "VF5"}, {"model_name": "VF6"}, {"model_name": "VF7"},
                    {"model_name": "VF8"}, {"model_name": "VF9"}
                ]}
                """;
            default -> """
                {"Models": [
                    {"model_name": "Sedan"}, {"model_name": "SUV"}, {"model_name": "Hatchback"},
                    {"model_name": "Crossover"}, {"model_name": "MPV"}, {"model_name": "Pickup"}
                ]}
                """;
        };
    }
}
