package ru.practicum.ewm.rating.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.service.RatingService;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/rating/{eventId}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingControllerPrivate {

    RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingDto addRating(@PathVariable Long userId,
                             @PathVariable Long eventId,
                             @RequestParam(defaultValue = "true") Boolean likes) {
        return ratingService.addRating(userId, eventId, likes);
    }

    @PatchMapping
    public RatingDto updateLike(@PathVariable Long userId,
                                @PathVariable Long eventId,
                                @RequestParam @NotNull Boolean likes) {
        return ratingService.updateRating(userId, eventId, likes);
    }

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long userId,
                           @PathVariable Long eventId) {
        ratingService.deleteRating(userId, eventId);
    }
}