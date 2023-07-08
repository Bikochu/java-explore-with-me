package ru.practicum.ewm.users.service;

import ru.practicum.ewm.users.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size);

    UserDto addUser(UserDto userDto);

    void deleteUser(Long userId);
}