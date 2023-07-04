package ru.practicum.ewm.requests.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.requests.enums.RequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequest {

    List<Long> requestIds;

    RequestStatus status;
}
