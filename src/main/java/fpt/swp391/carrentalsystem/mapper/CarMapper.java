package fpt.swp391.carrentalsystem.mapper;

import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.entity.Car;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CarResponseDto toDto(Car car);

    List<CarResponseDto> toDtoList(List<Car> cars);
}