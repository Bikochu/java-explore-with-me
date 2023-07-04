package ru.practicum.ewm.compilations.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {

    List<Long> events;

    Boolean pined;

    @NotBlank
    @Size(min = 1, max = 50)
    String title;
}
