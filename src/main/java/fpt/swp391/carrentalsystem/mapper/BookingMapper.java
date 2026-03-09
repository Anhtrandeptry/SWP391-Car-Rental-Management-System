package fpt.swp391.carrentalsystem.mapper;

import fpt.swp391.carrentalsystem.dto.response.BookingConfirmationDto;
import fpt.swp391.carrentalsystem.dto.response.RentalHistoryDto;
import fpt.swp391.carrentalsystem.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "car.carId", target = "carId")
    @Mapping(source = "car.name", target = "carName")
    BookingConfirmationDto toConfirmationDto(Booking booking);

    @Mapping(source = "car.carId", target = "carId")
    @Mapping(source = "car.name", target = "carName")
    @Mapping(source = "car.brand", target = "carBrand")
    @Mapping(source = "car.model", target = "carModel")
    @Mapping(source = "car.licensePlate", target = "licensePlate")
    @Mapping(source = "car.owner.firstName", target = "ownerName")
    @Mapping(source = "car.owner.phoneNumber", target = "ownerPhone")
    @Mapping(source = "car.owner.email", target = "ownerEmail")
    @Mapping(source = "customer.firstName", target = "customerName")
    @Mapping(source = "customer.phoneNumber", target = "customerPhone")
    @Mapping(source = "customer.email", target = "customerEmail")
    RentalHistoryDto toRentalHistoryDto(Booking booking);

    List<RentalHistoryDto> toRentalHistoryDtoList(List<Booking> bookings);
}
