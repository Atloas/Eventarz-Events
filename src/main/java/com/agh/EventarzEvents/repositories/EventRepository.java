package com.agh.EventarzEvents.repositories;

import com.agh.EventarzEvents.model.Event;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, String> {

    Event findByUuid(String uuid);

    List<Event> findByUuidIn(List<String> uuids);

    List<Event> findByNameLikeIgnoreCase(String regex);

    @Query("SELECT e.groupUuid FROM event e WHERE e.uuid = :uuid")
    String findGroupUuidByUuid(String uuid);

    List<Event> findByGroupUuid(String groupUuid);

    @Query("SELECT COUNT(*) FROM event e WHERE e.groupUuid = :groupUuid")
    int findEventCountByGroupUuid(String groupUuid);

    @Modifying
    void deleteByUuid(String uuid);

    @Modifying
    void deleteByUuidIn(List<String> uuids);

    @Modifying
    void deleteByGroupUuid(String groupUuid);

    @Query("SELECT e FROM event e WHERE e.organizerUsername = :username")
    List<Event> findOrganizedEvents(String username);

    @Query("FROM event e INNER JOIN e.participants ep WHERE ep.username = :username")
    List<Event> findJoinedEvents(String username);

    @Modifying
    @Query("DELETE FROM event e WHERE e.groupUuid = :groupUuid AND e.organizerUsername = :username")
    void deleteFromGroupByOrganizerUsername(String groupUuid, String username);
}
