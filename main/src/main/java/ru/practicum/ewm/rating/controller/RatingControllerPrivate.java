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
                             @RequestParam(name = "likes", defaultValue = "true") Boolean likes) {
        log.info("Получаем запрос на добавление лайка от {} в {} с параметром {}.", userId, eventId, likes);
        RatingDto ratingDto = ratingService.addRating(userId, eventId, likes);
        log.info("Возвращаем созданный рейтинг: {}.", ratingDto);
        return ratingDto;
    }

    @PatchMapping
    public RatingDto updateLike(@PathVariable Long userId,
                                @PathVariable Long eventId,
                                @RequestParam(name = "likes") @NotNull Boolean likes) {
        log.info("Получаем запрос на изменение лайка от {} в {} с параметром {}.", userId, eventId, likes);
        RatingDto ratingDto = ratingService.updateRating(userId, eventId, likes);
        log.info("Возвращаем измененный рейтинг: {}.", ratingDto);
        return ratingService.updateRating(userId, eventId, likes);
    }

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long userId,
                           @PathVariable Long eventId) {
        log.info("Получаем запрос на удаление лайка {} от {}.", eventId, userId);
        ratingService.deleteRating(userId, eventId);
        log.info("Лайк {} от {} удален.", eventId, userId);
    }
}