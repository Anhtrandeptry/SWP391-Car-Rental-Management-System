package fpt.swp391.carrentalsystem.config;

import fpt.swp391.carrentalsystem.entity.Brand;
import fpt.swp391.carrentalsystem.entity.CarModel;
import fpt.swp391.carrentalsystem.repository.BrandRepository;
import fpt.swp391.carrentalsystem.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BrandSeeder implements CommandLineRunner {

    private final BrandRepository brandRepository;
    private final CarModelRepository carModelRepository;

    @Override
    public void run(String... args) throws Exception {
        // Chỉ chạy nếu database trống
        if (brandRepository.count() == 0) {
            seedData();
        }
    }

    private void seedData() {
        // TOYOTA
        Brand toyota = new Brand();
        toyota.setName("TOYOTA");
        toyota.setLogoUrl("https://example.com/toyota-logo.png");
        toyota = brandRepository.save(toyota);

        List<CarModel> toyotaModels = Arrays.asList(
                createModel("4RUNNER 2.5", "2.5", "Xăng", toyota),
                createModel("CAMRY 2.5", "2.5", "Xăng", toyota),
                createModel("VIOS 1.5", "1.5", "Xăng", toyota),
                createModel("FORTUNER 2.4", "2.4", "Dầu", toyota),
                createModel("COROLLA CROSS 1.8", "1.8", "Xăng", toyota)
        );
        carModelRepository.saveAll(toyotaModels);

        // HONDA
        Brand honda = new Brand();
        honda.setName("HONDA");
        honda.setLogoUrl("https://example.com/honda-logo.png");
        honda = brandRepository.save(honda);

        List<CarModel> hondaModels = Arrays.asList(
                createModel("CITY 1.5", "1.5", "Xăng", honda),
                createModel("CIVIC 1.5 TURBO", "1.5", "Xăng", honda),
                createModel("CR-V 1.5 TURBO", "1.5", "Xăng", honda),
                createModel("ACCORD 1.5 TURBO", "1.5", "Xăng", honda)
        );
        carModelRepository.saveAll(hondaModels);

        // MAZDA
        Brand mazda = new Brand();
        mazda.setName("MAZDA");
        mazda.setLogoUrl("https://example.com/mazda-logo.png");
        mazda = brandRepository.save(mazda);

        List<CarModel> mazdaModels = Arrays.asList(
                createModel("MAZDA 3 2.0", "2.0", "Xăng", mazda),
                createModel("MAZDA 6 2.5", "2.5", "Xăng", mazda),
                createModel("CX-5 2.0", "2.0", "Xăng", mazda),
                createModel("CX-8 2.5", "2.5", "Xăng", mazda)
        );
        carModelRepository.saveAll(mazdaModels);

        // FORD
        Brand ford = new Brand();
        ford.setName("FORD");
        ford.setLogoUrl("https://example.com/ford-logo.png");
        ford = brandRepository.save(ford);

        List<CarModel> fordModels = Arrays.asList(
                createModel("RANGER 2.0", "2.0", "Dầu", ford),
                createModel("EVEREST 2.0", "2.0", "Dầu", ford),
                createModel("EXPLORER 2.3", "2.3", "Xăng", ford)
        );
        carModelRepository.saveAll(fordModels);

        // KIA
        Brand kia = new Brand();
        kia.setName("KIA");
        kia.setLogoUrl("https://example.com/kia-logo.png");
        kia = brandRepository.save(kia);

        List<CarModel> kiaModels = Arrays.asList(
                createModel("MORNING 1.0", "1.0", "Xăng", kia),
                createModel("SELTOS 1.5", "1.5", "Xăng", kia),
                createModel("SORENTO 2.2", "2.2", "Dầu", kia)
        );
        carModelRepository.saveAll(kiaModels);

        System.out.println("✅ Đã khởi tạo dữ liệu mẫu cho brands và models!");
    }

    private CarModel createModel(String name, String engineSize, String fuelType, Brand brand) {
        CarModel model = new CarModel();
        model.setName(name);
        model.setEngineSize(engineSize);
        model.setFuelType(fuelType);
        model.setBrand(brand);
        return model;
    }
}


