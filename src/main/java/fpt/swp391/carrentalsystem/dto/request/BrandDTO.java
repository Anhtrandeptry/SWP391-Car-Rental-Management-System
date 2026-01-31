package fpt.swp391.carrentalsystem.dto.request;



import fpt.swp391.carrentalsystem.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandDTO {

    private Long id;
    private String name;
    private String logoUrl;
    private List<CarModelDTO> models;

    public BrandDTO(Brand brand) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.logoUrl = brand.getLogoUrl();
        if (brand.getModels() != null) {
            this.models = brand.getModels().stream()
                    .map(CarModelDTO::new)
                    .collect(Collectors.toList());
        }
    }
}


