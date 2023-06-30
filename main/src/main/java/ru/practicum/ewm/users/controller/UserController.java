package ru.practicum.ewm.users.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> userIds,
                                  @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Получаем запрос: userIds={}, from={}, size={}", userIds, from, size);
        List<UserDto> users = userService.getUsers(userIds, from, size);
        log.info("Возвращаем {} элемент(а/ов).", users.size());
        return users;
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        log.info("Получаем запрос: userDto={}", userDto);
        UserDto newUserDto = userService.addUser(userDto);
        log.info("Возвращаем ответ userDto={}", newUserDto);
        return newUserDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Получаем запрос на удаление: userId={}", userId);
        userService.deleteUser(userId);
        log.info("Пользователь userId={} удален.", userId);
    }
}