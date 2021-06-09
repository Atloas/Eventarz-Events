package com.agh.EventarzEvents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventForm {

    private String groupUuid;
    private String name;
    private String description;
    private int maxParticipants;
    private String eventDate;
    private String publishedDate;
    private String organizerUsername;
    private boolean participate;
}
