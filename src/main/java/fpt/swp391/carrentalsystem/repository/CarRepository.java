package fpt.swp391.carrentalsystem.repository;



import fpt.swp391.carrentalsystem.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    // ================== BASIC QUERIES ==================

    /**
     * Tìm xe theo biển số
     */
    Optional<Car> findByLicensePlate(String licensePlate);

    /**
     * Kiểm tra biển số đã tồn tại chưa
     */
    boolean existsByLicensePlate(String licensePlate);

    /**
     * Tìm xe theo chủ xe (Owner ID)
     */
    List<Car> findByOwnerId(Long ownerId);

    /**
     * Đếm số xe của một owner
     */
    long countByOwnerId(Long ownerId);

    // ================== SEARCH BY ATTRIBUTES ==================

    /**
     * Tìm xe theo thành phố
     */
    List<Car> findByCity(String city);

    /**
     * Tìm xe theo hãng (Brand name)
     */
    @Query("SELECT c FROM Car c WHERE c.brand = :brandName")
    List<Car> findByBrand(@Param("brandName") String brandName);

    /**
     * Tìm xe theo mẫu (Model name)
     */
    @Query("SELECT c FROM Car c WHERE c.model = :modelName")
    List<Car> findByModel(@Param("modelName") String modelName);

    /**
     * Tìm xe theo năm sản xuất
     */
    List<Car> findByYear(Integer year);

    /**
     * Tìm xe theo màu sắc
     */
    List<Car> findByColor(String color);

    /**
     * Tìm xe theo loại hộp số
     */
    List<Car> findByTransmissionType(String transmissionType);

    /**
     * Tìm xe theo loại nhiên liệu
     */
    List<Car> findByFuelType(String fuelType);

    /**
     * Tìm xe theo số chỗ ngồi
     */
    List<Car> findBySeats(Integer seats);

    // ================== SEARCH BY STATUS ==================

    /**
     * Tìm xe theo trạng thái
     */
    List<Car> findByStatus(Car.CarStatus status);

    /**
     * Tìm xe có sẵn (AVAILABLE)
     */
    @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE'")
    List<Car> findAllAvailableCars();

    /**
     * Tìm xe đang cho thuê (RENTED)
     */
    @Query("SELECT c FROM Car c WHERE c.status = 'RENTED'")
    List<Car> findAllRentedCars();

    /**
     * Tìm xe có sẵn của một owner
     */
    List<Car> findByOwnerIdAndStatus(Long ownerId, Car.CarStatus status);

    // ================== SEARCH BY PRICE RANGE ==================

    /**
     * Tìm xe theo khoảng giá
     */
    @Query("SELECT c FROM Car c WHERE c.pricePerDay BETWEEN :minPrice AND :maxPrice")
    List<Car> findByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    /**
     * Tìm xe có giá dưới một mức
     */
    List<Car> findByPricePerDayLessThanEqual(BigDecimal maxPrice);

    /**
     * Tìm xe có giá trên một mức
     */
    List<Car> findByPricePerDayGreaterThanEqual(BigDecimal minPrice);

    // ================== ADVANCED SEARCH ==================

    /**
     * Tìm xe theo nhiều tiêu chí
     */
    @Query("SELECT c FROM Car c WHERE " +
            "(:city IS NULL OR c.city = :city) AND " +
            "(:brand IS NULL OR c.brand = :brand) AND " +
            "(:minSeats IS NULL OR c.seats >= :minSeats) AND " +
            "(:maxSeats IS NULL OR c.seats <= :maxSeats) AND " +
            "(:minPrice IS NULL OR c.pricePerDay >= :minPrice) AND " +
            "(:maxPrice IS NULL OR c.pricePerDay <= :maxPrice) AND " +
            "(:status IS NULL OR c.status = :status)")
    List<Car> searchCars(
            @Param("city") String city,
            @Param("brand") String brand,
            @Param("minSeats") Integer minSeats,
            @Param("maxSeats") Integer maxSeats,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") Car.CarStatus status
    );

    /**
     * Tìm xe có sẵn theo thành phố và khoảng giá
     */
    @Query("SELECT c FROM Car c WHERE " +
            "c.status = 'AVAILABLE' AND " +
            "c.city = :city AND " +
            "c.pricePerDay BETWEEN :minPrice AND :maxPrice")
    List<Car> findAvailableCarsByCityAndPriceRange(
            @Param("city") String city,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    /**
     * Tìm kiếm xe theo từ khóa (tìm trong brand, model, description)
     */
    @Query("SELECT c FROM Car c WHERE " +
            "LOWER(c.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Car> searchByKeyword(@Param("keyword") String keyword);

    // ================== STATISTICS ==================

    /**
     * Đếm tổng số xe trong hệ thống
     */
    @Query("SELECT COUNT(c) FROM Car c")
    long countTotalCars();

    /**
     * Đếm xe theo trạng thái
     */
    long countByStatus(Car.CarStatus status);

    /**
     * Đếm xe theo thành phố
     */
    long countByCity(String city);

    /**
     * Đếm xe theo hãng
     */
    @Query("SELECT COUNT(c) FROM Car c WHERE c.brand = :brand")
    long countByBrand(@Param("brand") String brand);

    /**
     * Tính tổng thu nhập ước tính của một owner
     */
    @Query("SELECT SUM(c.estimatedIncome) FROM Car c WHERE c.ownerId = :ownerId")
    BigDecimal sumEstimatedIncomeByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * Lấy giá trung bình theo thành phố
     */
    @Query("SELECT AVG(c.pricePerDay) FROM Car c WHERE c.city = :city")
    BigDecimal getAveragePriceByCity(@Param("city") String city);

    /**
     * Lấy giá trung bình theo hãng
     */
    @Query("SELECT AVG(c.pricePerDay) FROM Car c WHERE c.brand = :brand")
    BigDecimal getAveragePriceByBrand(@Param("brand") String brand);

    // ================== SORTING ==================

    /**
     * Lấy tất cả xe sắp xếp theo giá tăng dần
     */
    List<Car> findAllByOrderByPricePerDayAsc();

    /**
     * Lấy tất cả xe sắp xếp theo giá giảm dần
     */
    List<Car> findAllByOrderByPricePerDayDesc();

    /**
     * Lấy xe có sẵn sắp xếp theo giá
     */
    List<Car> findByStatusOrderByPricePerDayAsc(Car.CarStatus status);

    /**
     * Lấy xe mới nhất (theo ngày tạo)
     */
    @Query("SELECT c FROM Car c ORDER BY c.createdAt DESC")
    List<Car> findLatestCars();

    /**
     * Lấy top N xe có giá thấp nhất
     */
    @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' ORDER BY c.pricePerDay ASC")
    List<Car> findTopCheapestCars();

    /**
     * Lấy top N xe mới nhất của owner
     */
    @Query("SELECT c FROM Car c WHERE c.ownerId = :ownerId ORDER BY c.createdAt DESC")
    List<Car> findLatestCarsByOwnerId(@Param("ownerId") Long ownerId);

    // ================== CUSTOM QUERIES ==================

    /**
     * Lấy xe phổ biến nhất (được thuê nhiều nhất)
     * TODO: Implement sau khi có Booking entity
     */
    @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' ORDER BY c.createdAt DESC")
    List<Car> findPopularCars();

    /**
     * Lấy xe gần tôi (theo thành phố)
     */
    @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' AND c.city = :city ORDER BY c.pricePerDay ASC")
    List<Car> findNearMeCars(@Param("city") String city);

    /**
     * Lấy xe đề xuất (theo hãng và thành phố)
     */
    @Query("SELECT c FROM Car c WHERE " +
            "c.status = 'AVAILABLE' AND " +
            "c.brand = :brand AND " +
            "c.city = :city " +
            "ORDER BY c.pricePerDay ASC")
    List<Car> findRecommendedCars(
            @Param("brand") String brand,
            @Param("city") String city
    );

    /**
     * Kiểm tra owner có xe nào đang cho thuê không
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Car c WHERE c.ownerId = :ownerId AND c.status = 'RENTED'")
    boolean hasRentedCars(@Param("ownerId") Long ownerId);

    /**
     * Lấy danh sách xe cùng loại (cùng brand và model)
     */
    @Query("SELECT c FROM Car c WHERE " +
            "c.brand = :brand AND " +
            "c.model = :model AND " +
            "c.id != :excludeId AND " +
            "c.status = 'AVAILABLE'")
    List<Car> findSimilarCars(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("excludeId") Long excludeId
    );

    /**
     * Xóa mềm xe (chuyển status sang INACTIVE)
     */
    @Query("UPDATE Car c SET c.status = 'INACTIVE' WHERE c.id = :id")
    void softDeleteCar(@Param("id") Long id);

    /**
     * Khôi phục xe đã xóa mềm
     */
    @Query("UPDATE Car c SET c.status = 'AVAILABLE' WHERE c.id = :id")
    void restoreCar(@Param("id") Long id);
}

