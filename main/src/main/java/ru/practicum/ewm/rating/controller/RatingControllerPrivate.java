package ru.practicum.ewm.rating.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.service.RatingService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/rating/{eventId}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingControllerPrivate {

    RatingService ratingService;

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE})
    public ResponseEntity<RatingDto> manageRating(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @RequestParam(name = "likes", required = false) Boolean likes,
                                                  @NotNull HttpServletRequest request) {

        if (RequestMethod.POST.toString().equals(request.getMethod())) {
            log.info("Получаем запрос на добавление лайка от {} в {} с параметром {}.", userId, eventId, likes);
            RatingDto ratingDto = ratingService.addRating(userId, eventId, likes);
            log.info("Возвращаем созданный рейтинг: {}.", ratingDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ratingDto);
        } else if (RequestMethod.PATCH.toString().equals(request.getMethod())) {
            log.info("Получаем запрос на изменение лайка 'Эвента: {} от Пользователя: {} с параметром {}'.", userId, eventId, likes);
            RatingDto ratingDto = ratingService.updateRating(userId, eventId, likes);
            log.info("Возвращаем измененный рейтинг: {}.", ratingDto);
            return ResponseEntity.ok(ratingDto);
        } else if (RequestMethod.DELETE.toString().equals(request.getMethod())) {
            log.info("Получаем запрос на удаление лайка 'Эвента: {} от  Пользователя: {}'.", eventId, userId);
            ratingService.deleteRating(userId, eventId);
            log.info("Лайк 'Эвента: {} от Пользователя: {}' - удален.", eventId, userId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.badRequest().build();
    }
}
