package ru.practicum.ewm.events.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.enums.StateAdmin;
import ru.practicum.ewm.events.enums.StatePrivate;
import ru.practicum.ewm.events.enums.StatePublic;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.locations.mapper.LocationMapper;
import ru.practicum.ewm.stats.client.hit.HitClient;
import ru.practicum.ewm.stats.dto.StatsDto;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {

    EventRepository eventRepository;

    UserRepository userRepository;

    CategoryRepository categoryRepository;

    HitClient hitClient;

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(StatePublic.PENDING);
        event.setConfirmedRequests(0);
        event.setViews(0L);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventFullByOwner(Long userId, Long eventId) {
        return EventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(userId, eventId));
    }

    @Override
    public List<EventShortDto> getEventsShortByOwner(Long userId, Integer from, Integer size) {
        int pageNumber = (int) Math.ceil((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);
        return eventRepository.findAllByInitiatorId(userId, pageable).map(EventMapper::toEventShortDto).getContent();
    }

    @Override
    public EventFullDto updateEventByOwner(Long userId, Long eventId, UpdateEventUserRequest eventUserRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(userId, eventId);
        Category category = categoryRepository.findById(eventUserRequest.getCategory()).orElse(null);
        event.setCategory(category);
        event.setAnnotation(eventUserRequest.getAnnotation() != null ? eventUserRequest.getAnnotation() : null);
        event.setDescription(eventUserRequest.getDescription() != null ? eventUserRequest.getDescription() : null);
        event.setEventDate(eventUserRequest.getEventDate() != null ? eventUserRequest.getEventDate() : null);
        event.setLocation(eventUserRequest.getLocation() != null ? LocationMapper.toLocation(eventUserRequest.getLocation()) : null);
        event.setPaid(eventUserRequest.getPaid() != null ? eventUserRequest.getPaid() : null);
        event.setParticipantLimit(eventUserRequest.getParticipantLimit() != null ? eventUserRequest.getParticipantLimit() : null);
        event.setRequestModeration(eventUserRequest.getRequestModeration() != null ? eventUserRequest.getRequestModeration() : null);
        event.setTitle(event.getTitle() != null ? eventUserRequest.getTitle() : null);
        if (eventUserRequest.getStateAction() != null) {
            StatePrivate statePrivate = StatePrivate.valueOf(eventUserRequest.getStateAction());
            event.setState(statePrivate == StatePrivate.CANCEL_REVIEW ? StatePublic.CANCELED : StatePublic.PENDING);
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        Category category = categoryRepository.findById(eventAdminRequest.getCategory()).orElse(null);
        event.setCategory(category);
        event.setAnnotation(eventAdminRequest.getAnnotation() != null ? eventAdminRequest.getAnnotation() : null);
        event.setDescription(eventAdminRequest.getDescription() != null ? eventAdminRequest.getDescription() : null);
        event.setEventDate(eventAdminRequest.getEventDate() != null ? eventAdminRequest.getEventDate() : null);
        event.setLocation(eventAdminRequest.getLocation() != null ? LocationMapper.toLocation(eventAdminRequest.getLocation()) : null);
        event.setPaid(eventAdminRequest.getPaid() != null ? eventAdminRequest.getPaid() : null);
        event.setParticipantLimit(eventAdminRequest.getParticipantLimit() != null ? eventAdminRequest.getParticipantLimit() : null);
        event.setRequestModeration(eventAdminRequest.getRequestModeration() != null ? eventAdminRequest.getRequestModeration() : null);
        event.setTitle(event.getTitle() != null ? eventAdminRequest.getTitle() : null);
        if (eventAdminRequest.getStateAction() != null) {
            StateAdmin statePrivate = StateAdmin.valueOf(eventAdminRequest.getStateAction());
            event.setState(statePrivate == StateAdmin.REJECT_EVENT ? StatePublic.CANCELED : StatePublic.PUBLISHED);
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Boolean onlyAvailable, String sort, Integer from,
                                         Integer size, HttpServletRequest request) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong timestamps of START or END.");
        }
        int pageNumber = (int) Math.ceil((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);

        Specification<Event> specification = Specification.where(null);

        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    ));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }

        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), StatePublic.PUBLISHED));

        List<Event> resultEvents = eventRepository.findAll(specification, pageable).getContent();
        setViewsOfEvents(resultEvents);

        return resultEvents.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> searchEvents(List<Long> users, List<String> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong timestamps of START or END.");
        }
        int pageNumber = (int) Math.ceil((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);

        Specification<Event> specification = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("initiator").get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("state").as(String.class).in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("category").get("id").in(categories));
        }

        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        return eventRepository.findAll(specification, pageable).map(EventMapper::toEventFullDto).getContent();
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        if (event.getState().equals(StatePublic.PUBLISHED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found.");
        }
        event.setViews(event.getViews() + 1);
        return EventMapper.toEventFullDto(event);
    }

    private void setViewsOfEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        List<StatsDto> viewStatsList = hitClient.getStats("2000-01-01 00:00:00", "2100-01-01 00:00:00", uris, false);

        for (Event event : events) {
            StatsDto currentViewStats = viewStatsList.stream()
                    .filter(statsDto -> {
                        Long eventIdOfViewStats = Long.parseLong(statsDto.getUri().substring("/events/".length()));
                        return eventIdOfViewStats.equals(event.getId());
                    })
                    .findFirst()
                    .orElse(null);

            Long views = (currentViewStats != null) ? currentViewStats.getHits() : 0;
            event.setViews(views);
        }
    }
}
