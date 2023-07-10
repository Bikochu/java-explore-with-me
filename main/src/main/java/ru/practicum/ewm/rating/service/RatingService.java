package ru.practicum.ewm.rating.service;

import ru.practicum.ewm.rating.dto.RatingDto;

public interface RatingService {
    RatingDto addRating(Long userId, Long eventId, Boolean likes);

    RatingDto updateRating(Long userId, Long eventId, Boolean likes);

    void deleteRating(Long userId, Long eventId);
}
