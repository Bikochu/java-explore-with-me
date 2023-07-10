package ru.practicum.ewm.rating.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "rating")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "liker_id", nullable = false)
    Long likerId;

    @Column(name = "event_id", nullable = false)
    Long eventId;

    @Column(name = "likes", nullable = false)
    Boolean likes;
}
