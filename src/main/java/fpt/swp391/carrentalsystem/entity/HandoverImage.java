package fpt.swp391.carrentalsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "handover_images")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HandoverImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "handover_id")
    private Handover handover;

    private String imageUrl;
}