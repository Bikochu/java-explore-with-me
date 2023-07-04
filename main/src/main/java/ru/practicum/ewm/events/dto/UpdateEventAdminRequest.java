package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.locations.dto.LocationDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
