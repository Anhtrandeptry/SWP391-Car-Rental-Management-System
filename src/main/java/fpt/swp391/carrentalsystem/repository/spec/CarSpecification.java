package fpt.swp391.carrentalsystem.repository.spec;

import fpt.swp391.carrentalsystem.entity.Car;
import org.springframework.data.jpa.domain.Specification;

public class CarSpecification {

    public static Specification<Car> hasName(String name) {
        return (root, query, cb) ->
                name == null || name.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%");
    }

    public static Specification<Car> hasSeats(Integer seats) {
        return (root, query, cb) ->
                seats == null ? null : cb.equal(root.get("seats"), seats);
    }

    public static Specification<Car> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null || brand.isBlank()
                        ? null
                        : cb.equal(cb.lower(root.get("brand")),
                        brand.toLowerCase());
    }

    public static Specification<Car> hasCarType(String carType) {
        return (root, query, cb) ->
                carType == null || carType.isBlank()
                        ? null
                        : cb.equal(cb.lower(root.get("carType")),
                        carType.toLowerCase());
    }

    public static Specification<Car> hasFuelType(String fuelType) {
        return (root, query, cb) ->
                fuelType == null || fuelType.isBlank()
                        ? null
                        : cb.equal(cb.lower(root.get("fuelType")),
                        fuelType.toLowerCase());
    }

    public static Specification<Car> hasLocation(String location) {
        return (root, query, cb) ->
                location == null || location.isBlank()
                        ? null
                        : cb.like(cb.lower(root.get("location")),
                        "%" + location.toLowerCase() + "%");
    }
}
