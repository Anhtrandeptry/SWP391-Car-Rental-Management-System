package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.HandoverImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HandoverImageRepository extends JpaRepository<HandoverImage, Integer> {
}