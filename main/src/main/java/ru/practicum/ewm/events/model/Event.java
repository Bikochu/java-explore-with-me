package ru.practicum.ewm.events.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.enums.StatePublic;
import ru.practicum.ewm.locations.model.Location;
import ru.practicum.ewm.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @Column(nullable = false)
    Integer confirmedRequests;

    @Column(name = "created_on", nullable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime createdOn;

    @Column(nullable = false)
    String description;

    @Column(name = "event_date", nullable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    Location location;

    @Column(nullable = false)
    Boolean paid;

    @Column(nullable = false)
    Integer participantLimit;

    @Column(name = "published_on", columnDefinition = "TIMESTAMP")
    LocalDateTime publishedOn;

    @Column(nullable = false)
    Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StatePublic state;

    @Column(nullable = false)
    String title;

    @Column
    Long views;
}
