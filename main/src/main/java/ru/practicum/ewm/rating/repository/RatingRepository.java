package ru.practicum.ewm.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.rating.model.Rating;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Boolean existsByLikerIdAndEventId(Long userId, Long eventId);

    Optional<Rating> findByLikerIdAndEventId(Long userId, Long eventId);
}
