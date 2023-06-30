package ru.practicum.ewm.users.mapper;

import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.model.User;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}