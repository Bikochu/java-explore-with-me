package ru.practicum.ewm.compilations.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.model.Event;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compilations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "pined")
    Boolean pined;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "compilation_events", joinColumns = @JoinColumn(name = "compilation_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    Set<Event> events;
}
