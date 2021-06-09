package com.agh.EventarzEvents.model;

import com.agh.EventarzEvents.exceptions.EventFullException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "event")
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;
    private String name;
    private String description;
    @Column(name = "max_participants")
    private int maxParticipants;
    @Column(name = "event_date")
    private String eventDate;
    @Transient
    @JsonIgnore
    private LocalDateTime eventDateObject;
    @Column(name = "published_date")
    private String publishedDate;
    @Transient
    @JsonIgnore
    private LocalDateTime publishedDateObject;
    @Transient
    private boolean happened;
    @Transient
    @JsonIgnore
    private boolean expired;
    @Column(name = "organizer_username")
    private String organizerUsername;
    @Column(name = "group_uuid")
    private String groupUuid;
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants;

    public Event(EventForm eventForm) {
        this.name = eventForm.getName();
        this.description = eventForm.getDescription();
        this.eventDate = eventForm.getEventDate();
        this.publishedDate = eventForm.getPublishedDate();
        this.maxParticipants = eventForm.getMaxParticipants();
        this.organizerUsername = eventForm.getOrganizerUsername();
        this.groupUuid = eventForm.getGroupUuid();
        this.participants = new ArrayList<>();
    }

    public LocalDateTime getEventDateObject() {
        if (eventDateObject == null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            eventDateObject = LocalDateTime.parse(eventDate, dtf);
        }
        return eventDateObject;
    }

    // TODO: This has to be done on Gateway to notify Groups
//    public void checkEventDate() {
//        this.setHappened(this.getEventDateObject().isBefore(LocalDateTime.now()));
//        this.setExpired(this.getEventDateObject().isBefore(LocalDateTime.now().minusDays(1)));
//    }
//
//    public static int compareEventDates(Event a, Event b) {
//        if (a.getEventDateObject().isBefore(b.getEventDateObject())) {
//            return -1;
//        } else if (b.getEventDateObject().isBefore(a.getEventDateObject())) {
//            return 1;
//        } else {
//            return 0;
//        }
//    }

    public void join(String username) throws EventFullException {
        if (this.participants.size() >= this.maxParticipants) {
            throw new EventFullException("Event " + this.uuid + " is already full!");
        }
        this.participants.add(new Participant(this, username));
    }

    public void leave(String username) {
        for (Participant participant : this.participants) {
            if (participant.getUsername().equals(username)) {
                this.participants.remove(participant);
                return;
            }
        }
    }

    public boolean checkIfUserIsMember(String username) {
        for (Participant participant : this.participants) {
            if (participant.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
