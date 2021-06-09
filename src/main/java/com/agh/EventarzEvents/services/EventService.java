package com.agh.EventarzEvents.services;

import com.agh.EventarzEvents.exceptions.EventFullException;
import com.agh.EventarzEvents.exceptions.EventNotFoundException;
import com.agh.EventarzEvents.model.Event;
import com.agh.EventarzEvents.model.EventForm;
import com.agh.EventarzEvents.model.Participant;
import com.agh.EventarzEvents.repositories.EventRepository;
import com.agh.EventarzEvents.repositories.ParticipantRepository;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ParticipantRepository participantRepository;

    @Retry(name = "getEventByUuidRetry")
    public Event getEventByUuid(String uuid) throws EventNotFoundException {
        Event event = eventRepository.findByUuid(uuid);
        if (event == null) {
            throw new EventNotFoundException("Event " + uuid + " not found!");
        }
        // TODO: This has to be done on Gateway to notify Groups
//        event = handleEventExpiration(event);
//        if (event == null) {
//            throw new EventNotFoundException("Event " + uuid + " not found!");
//        }
        return event;
    }

    @Retry(name = "getEventsByUuidListRetry")
    public List<Event> getEventsByUuidList(String[] uuids) {
        List<Event> events = eventRepository.findByUuidIn(Arrays.asList(uuids));
        return events;
    }

    @Retry(name = "getMyEventsRetry")
    public List<Event> getMyEvents(String username) {
        // TODO: This should be doable in one query but it didn't work for some reason.
        List<Event> organizedEvents = eventRepository.findOrganizedEvents(username);
        List<Event> joinedEvents = eventRepository.findJoinedEvents(username);
        List<Event> events = new ArrayList<>(organizedEvents);
        events.addAll(joinedEvents);
        // TODO: This has to be done on Gateway to notify Groups
//        events = handleEventExpiration(events);
//        events.sort(Event::compareEventDates);
        return events;
    }

    @Retry(name = "getOrganizedEventsRetry")
    public List<Event> getOrganizedEvents(String username) {
        List<Event> events = eventRepository.findOrganizedEvents(username);
        // TODO: This has to be done on Gateway to notify Groups
//        events = handleEventExpiration(events);
//        events.sort(Event::compareEventDates);
        return events;
    }

    @Retry(name = "getJoinedEventsRetry")
    public List<Event> getJoinedEvents(String username) {
        List<Event> events = eventRepository.findJoinedEvents(username);
        // TODO: This has to be done on Gateway to notify Groups
//        events = handleEventExpiration(events);
//        events.sort(Event::compareEventDates);
        return events;
    }

    @Retry(name = "getHomeEventsRetry")
    public List<Event> getHomeEvents(String username) {
        List<Event> events = eventRepository.findOrganizedEvents(username);
        events = extractUpcomingEvents(events);
        // TODO: This has to be done on Gateway to notify Groups
//        events = handleEventExpiration(events);
//        events.sort(Event::compareEventDates);
        return events;
    }

    @Retry(name = "getEventsByNameRetry")
    public List<Event> getEventsByName(String name) {
        List<Event> events = eventRepository.findByNameLikeIgnoreCase(name);
        // TODO: This has to be done on Gateway to notify Groups
//        events = handleEventExpiration(events);
//        events.sort(Event::compareEventDates);
        return events;
    }

    @Retry(name = "getEventsByGroupUuid")
    public List<Event> getEventsByGroupUuid(String groupUuid) {
        List<Event> events = eventRepository.findByGroupUuid(groupUuid);
        return events;
    }

    @Retry(name = "createEventRetry")
    public Event createEvent(EventForm eventForm) {
        Event event = new Event(eventForm);
        if (eventForm.isParticipate()) {
            event.getParticipants().add(new Participant(event, eventForm.getOrganizerUsername()));
        }
        event = eventRepository.save(event);
        return event;
    }

    // TODO: Make resilience4j ignore not founds here so it returns quickly

    @Retry(name = "updateEventRetry")
    public Event updateEvent(String uuid, EventForm eventForm) throws EventNotFoundException {
        Event event = eventRepository.findByUuid(uuid);
        if (event == null) {
            throw new EventNotFoundException("Event " + uuid + " not gound!");
        }
        boolean clearParticipants = false;
        if (event.getMaxParticipants() > eventForm.getMaxParticipants()) {
            clearParticipants = true;
        }
        event.setName(eventForm.getName());
        event.setDescription(eventForm.getDescription());
        event.setEventDate(eventForm.getEventDate());
        event.setMaxParticipants(eventForm.getMaxParticipants());
        if (clearParticipants) {
            event.getParticipants().clear();
        }
        // Not necessary according to my tests, but I'm leaving it in
        event = eventRepository.save(event);
        return event;
    }

    @Retry(name = "getGroupUuid")
    public String getGroupUuid(String uuid) throws EventNotFoundException {
        String groupUuid = eventRepository.findGroupUuidByUuid(uuid);
        if (groupUuid == null) {
            throw new EventNotFoundException("Event " + uuid + " not found!");
        }
        return groupUuid;
    }

    @Retry(name = "joinEventRetry")
    public Event joinEvent(String uuid, String username) throws EventNotFoundException, EventFullException {
        Event event = eventRepository.findByUuid(uuid);
        if (event == null) {
            throw new EventNotFoundException("Event " + uuid + " not found!");
        }
        event.join(username);
        // This save is necessary here for some reason. Not necessary when clearing that collection though??
        event = eventRepository.save(event);
        return event;
    }

    @Retry(name = "leaveEventRetry")
    public Event leaveEvent(String uuid, String username) throws EventNotFoundException {
        Event event = eventRepository.findByUuid(uuid);
        if (event == null) {
            throw new EventNotFoundException("Event " + uuid + " not found!");
        }
        event.leave(username);
        // This save is necessary here for some reason. Not necessary when clearing that collection though??
        eventRepository.save(event);
        return event;
    }

    @Retry(name = "removeUserFromEventsByGroupUuidRetry")
    public void removeUserFromEventsByGroupUuid(String groupUuid, String username) {
        List<Event> events = eventRepository.findByGroupUuid(groupUuid);
        for (Event event : events) {
            if (event.checkIfUserIsMember(username)) {
                event.leave(username);
                eventRepository.save(event);
            }
        }
    }

    @Retry(name = "deleteEventsRetry")
    public void deleteEvents(String[] uuids) {
        eventRepository.deleteByUuidIn(Arrays.asList(uuids));
    }

    private List<Event> extractUpcomingEvents(List<Event> events) {
        List<Event> upcomingEvents = new ArrayList<>();
        for (Event event : events) {
            if (!event.isHappened() && event.getEventDateObject().isBefore(LocalDateTime.now().plusWeeks(1))) {
                upcomingEvents.add(event);
            }
        }
        return upcomingEvents;
    }

    // TODO: This has to be done on Gateway to notify Groups
//    private Event handleEventExpiration(Event event) {
//        // This, the other handleEventExpiration method and the similar methods in GroupService mutate their arguments, unfortunately
//        event.checkEventDate();
//        if (event.isExpired()) {
//            eventRepository.deleteByUuid(event.getUuid());
//            return null;
//        } else {
//            return event;
//        }
//    }
//
//    private List<Event> handleEventExpiration(List<Event> events) {
//        List<Event> eventsToDelete = new ArrayList<>();
//        List<Event> modifiedEvents = new ArrayList<>();
//        for (Event event : events) {
//            event.checkEventDate();
//            if (event.isExpired()) {
//                eventsToDelete.add(event);
//            } else {
//                modifiedEvents.add(event);
//            }
//        }
//        eventRepository.deleteAll(eventsToDelete);
//        return modifiedEvents;
//    }
}
