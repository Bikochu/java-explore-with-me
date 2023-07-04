package ru.practicum.ewm.locations.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "locations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "lat", nullable = false)
    Float lat;

    @Column(name = "lon", nullable = false)
    Float lon;
}
