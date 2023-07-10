package ru.practicum.ewm.users.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.users.dto.UserRateDto;
import ru.practicum.ewm.users.service.UserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/rating")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserControllerPrivate {

    UserService userService;

    @GetMapping
    public List<UserRateDto> getRatedUsers(@PathVariable Long userId,
                                           @RequestParam(defaultValue = "HIGH") String rateSort,
                                           @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return userService.getRatedUsers(userId, rateSort, from, size);
    }
}
