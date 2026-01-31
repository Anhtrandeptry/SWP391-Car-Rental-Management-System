package fpt.swp391.carrentalsystem.dto.request;



import fpt.swp391.carrentalsystem.entity.CarModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarModelDTO {

    private Long id;
    private String name;
    private String engineSize;
    private String fuelType;
    private Long brandId;

    public CarModelDTO(CarModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.engineSize = model.getEngineSize();
        this.fuelType = model.getFuelType();
        this.brandId = model.getBrand().getId();
    }
}


