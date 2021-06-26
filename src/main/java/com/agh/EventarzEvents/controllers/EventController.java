/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agh.EventarzEvents.controllers;

import com.agh.EventarzEvents.EventarzEventsApplication;
import com.agh.EventarzEvents.model.Event;
import com.agh.EventarzEvents.model.EventForm;
import com.agh.EventarzEvents.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class EventController {

    private final EventService eventService;

    private final static Logger log = LoggerFactory.getLogger(EventarzEventsApplication.class);

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping(value = "/events", params = {"organizerUsername"})
    public List<Event> getFoundedEvents(@RequestParam String organizerUsername) {
        return eventService.getOrganizedEvents(organizerUsername);
    }

    @GetMapping(value = "/events", params = {"memberUsername"})
    public List<Event> getJoinedEvents(@RequestParam String memberUsername) {
        return eventService.getJoinedEvents(memberUsername);
    }

    @GetMapping(value = "/events", params = {"username"})
    public List<Event> getMyEvents(@RequestParam String username) {
        return eventService.getMyEvents(username);
    }

    @GetMapping(value = "/events", params = {"username", "home"})
    public List<Event> getHomeEvents(@RequestParam String username) {
        return eventService.getHomeEvents(username);
    }

    @GetMapping(value = "/events", params = {"name"})
    public List<Event> getEventsByName(@RequestParam String name) {
        return eventService.getEventsByName(name);
    }

    @GetMapping(value = "/events", params = {"uuids"})
    public List<Event> getEventsByUuidList(@RequestParam String[] uuids) {
        return eventService.getEventsByUuidList(uuids);
    }

    @GetMapping(value = "/events", params = {"groupUuid"})
    public List<Event> getEventsByGroupUuid(@RequestParam String groupUuid) {
        return eventService.getEventsByGroupUuid(groupUuid);
    }

    @GetMapping(value = "/events", params = {"groupUuids", "counts"})
    public Map<String, Integer> getEventCountsByGroupUuids(@RequestParam String[] groupUuids) {
        return eventService.getEventCountsByGroupUuids(groupUuids);
    }

    @PostMapping(value = "/events")
    public Event createEvent(@RequestBody EventForm eventForm) {
        return eventService.createEvent(eventForm);
    }

    @GetMapping(value = "/events/{uuid}")
    public Event getEventByUuid(@PathVariable String uuid) {
        return eventService.getEventByUuid(uuid);
    }

    @PutMapping(value = "/events/{uuid}")
    public Event updateEvent(@PathVariable String uuid, @RequestBody EventForm eventForm) {
        return eventService.updateEvent(uuid, eventForm);
    }

    @DeleteMapping(value = "/events", params = {"groupUuid"})
    public void deleteEventsByGroupUuid(@RequestParam String groupUuid) {
        eventService.deleteEventsByGroupUuid(groupUuid);
    }

    @DeleteMapping(value = "/events", params = {"groupUuid", "username"})
    public void removeUserFromEventsByGroupUuid(@RequestParam String groupUuid, @RequestParam String username) {
        eventService.removeUserFromEventsByGroupUuid(groupUuid, username);
    }

    @DeleteMapping(value = "/events/{uuids}")
    public void deleteEvents(@PathVariable String[] uuids) {
        eventService.deleteEvents(uuids);
    }

    @GetMapping(value = "/events/{uuid}/groupUuid")
    public String getGroupUuid(@PathVariable String uuid) {
        return eventService.getGroupUuid(uuid);
    }

    @PostMapping(value = "/events/{uuid}/participants")
    public Event joinEvent(@PathVariable String uuid, @RequestBody String username) {
        return eventService.joinEvent(uuid, username);
    }

    @DeleteMapping(value = "/events/{uuid}/participants/{username}")
    public Event leaveEvent(@PathVariable String uuid, @PathVariable String username) {
        return eventService.leaveEvent(uuid, username);
    }
}
