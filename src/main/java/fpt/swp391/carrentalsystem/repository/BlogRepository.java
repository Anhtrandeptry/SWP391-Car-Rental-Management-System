package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Blog;
import fpt.swp391.carrentalsystem.enums.BlogStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    Optional<Blog> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Blog> findByStatusOrderByCreatedAtDesc(BlogStatus status);

    List<Blog> findAllByOrderByCreatedAtDesc();
}