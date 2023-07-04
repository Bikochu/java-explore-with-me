package ru.practicum.ewm.locations.mapper;

import ru.practicum.ewm.locations.dto.LocationDto;
import ru.practicum.ewm.locations.model.Location;

public class LocationMapper {
    public static Location toLocation(LocationDto locationDto) {
        return new Location(
                locationDto.getId(),
                locationDto.getLat(),
                locationDto.getLon()
        );
    }

    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getId(),
                location.getLat(),
                location.getLon()
        );
    }
}
