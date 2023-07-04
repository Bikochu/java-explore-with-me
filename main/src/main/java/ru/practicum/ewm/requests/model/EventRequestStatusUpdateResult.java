package ru.practicum.ewm.requests.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResult {

    List<ParticipationRequestDto> confirmedRequests;

    List<ParticipationRequestDto> rejectedRequests;
}
