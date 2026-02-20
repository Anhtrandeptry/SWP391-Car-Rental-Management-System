package fpt.swp391.carrentalsystem.mapper;
import fpt.swp391.carrentalsystem.dto.response.BookingConfirmationDto;
import fpt.swp391.carrentalsystem.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "car.carId", target = "carId")
    @Mapping(source = "car.name", target = "carName")
    BookingConfirmationDto toConfirmationDto(Booking booking);
}
