package fpt.swp391.carrentalsystem.mapper;

import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(source = "owner.firstName", target = "ownerName")
    @Mapping(source = "owner.phoneNumber", target = "ownerPhone")
    CarResponseDto toDto(Car car);

    List<CarResponseDto> toDtoList(List<Car> cars);
}