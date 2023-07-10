package ru.practicum.ewm.rating.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.mapper.RatingMapper;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.rating.repository.RatingRepository;
import ru.practicum.ewm.requests.enums.RequestStatus;
import ru.practicum.ewm.requests.model.ParticipationRequest;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingServiceImpl implements RatingService {

    RatingRepository ratingRepository;

    UserRepository userRepository;

    EventRepository eventRepository;

    RequestRepository requestRepository;

    @Override
    public RatingDto addRating(Long userId, Long eventId, Boolean likes) {
        User liker = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        if (ratingRepository.existsByLikerIdAndEventId(userId, eventId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already rate this event");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You can't rate your event.");
        }

        ParticipationRequest request = requestRepository.findByRequesterIdAndEventId(userId, eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found."));
        if (!request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You can't rate, because, you wasn't be there.");
        }

        Rating rating = Rating.builder()
                .likerId(userId)
                .eventId(eventId)
                .likes(likes)
                .build();

        if (rating.getLikes()) {
            event.setLikes(event.getLikes() != null ? (event.getLikes() + 1) : 1);
            liker.setLikes(liker.getLikes() != null ? (liker.getLikes() + 1) : 1);
        } else {
            event.setDislikes(event.getDislikes() != null ? event.getDislikes() + 1 : 1);
            liker.setDislikes(liker.getDislikes() != null ? liker.getDislikes() + 1 : 1);
        }

        setRate(event, liker);

        return RatingMapper.toRatingDto(ratingRepository.save(rating));
    }

    @Override
    public RatingDto updateRating(Long userId, Long eventId, Boolean likes) {
        User liker = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        Rating rating = ratingRepository.findByLikerIdAndEventId(userId, eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found."));

        if (rating.getLikes().equals(likes)) {
            return RatingMapper.toRatingDto(rating);
        } else {
            if (likes) {
                event.setLikes(event.getLikes() != null ? (event.getLikes() + 1) : 1);
                liker.setLikes(liker.getLikes() != null ? (liker.getLikes() + 1) : 1);
                event.setDislikes(event.getDislikes() - 1);
                liker.setDislikes(liker.getDislikes() - 1);
            } else {
                event.setLikes(event.getLikes() - 1);
                liker.setLikes(liker.getLikes() - 1);
                event.setDislikes(event.getDislikes() != null ? event.getDislikes() + 1 : 1);
                liker.setDislikes(liker.getDislikes() != null ? liker.getDislikes() + 1 : 1);
            }
            rating.setLikes(likes);
            setRate(event, liker);

            return RatingMapper.toRatingDto(ratingRepository.save(rating));
        }
    }

    @Override
    public void deleteRating(Long userId, Long eventId) {
        User liker = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        Rating rating = ratingRepository.findByLikerIdAndEventId(userId, eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found."));
        if (rating.getLikes()) {
            event.setLikes(event.getLikes() != null ? event.getLikes() - 1 : 0);
            liker.setLikes(liker.getLikes() != null ? liker.getLikes() - 1 : 0);
        } else {
            event.setDislikes(event.getDislikes() != null ? event.getDislikes() - 1 : 0);
            liker.setDislikes(liker.getDislikes() != null ? liker.getDislikes() - 1 : 0);
        }

        setRate(event, liker);

        ratingRepository.deleteById(rating.getId());
    }

    private void setRate(Event event, User liker) {
        int eventLike = event.getLikes() != null ? event.getLikes() : 0;
        int eventDislike = event.getDislikes() != null ? event.getDislikes() : 0;

        int totalEventVotes = eventLike + eventDislike;
        double rateEvent = (eventLike * 10.0) / totalEventVotes;
        event.setRate(Math.round(rateEvent * 10) / 10.0);

        int userLike = liker.getLikes() != null ? liker.getLikes() : 0;
        int userDislike = liker.getDislikes() != null ? liker.getDislikes() : 0;

        int totalUserVotes = userLike + userDislike;
        double rateUser = (userLike * 10.0) / totalUserVotes;
        liker.setRate(Math.round(rateUser * 10) / 10.0);

        eventRepository.save(event);
        userRepository.save(liker);
    }
}