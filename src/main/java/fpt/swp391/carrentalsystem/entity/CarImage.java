package fpt.swp391.carrentalsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "car_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;

    @Column(name = "is_main")
    private Boolean isMain = false;

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}
