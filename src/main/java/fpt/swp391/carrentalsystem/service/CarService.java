package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.CarListItemDto;

import java.util.List;

public interface CarService {
    List<CarListItemDto> listAll();
    List<CarListItemDto> searchByName(String name);
}
