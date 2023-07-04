package ru.practicum.ewm.compilations.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {

    List<EventShortDto> events;

    @NotBlank
    Long id;

    @NotNull
    Boolean pined;

    @NotBlank
    String title;
}
