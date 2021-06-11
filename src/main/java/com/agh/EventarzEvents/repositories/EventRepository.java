package com.agh.EventarzEvents.repositories;

import com.agh.EventarzEvents.model.Event;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
// TODO: I don't actually use those methods much so the Retry and CircuitBreaker probably dn't really work?
@Retry(name = "EventRepositoryRetry")
@CircuitBreaker(name = "EventRepositoryCircuitBreaker")
public interface EventRepository extends CrudRepository<Event, String> {

    Event findByUuid(String uuid);

    List<Event> findByUuidIn(List<String> uuids);

    List<Event> findByNameLikeIgnoreCase(String regex);

    @Query("SELECT e.groupUuid FROM event e WHERE e.uuid = :uuid")
    String findGroupUuidByUuid(String uuid);

    List<Event> findByGroupUuid(String groupUuid);

    @Query("SELECT COUNT(*) FROM event e WHERE e.groupUuid = :groupUuid")
    int getEventCountByGroupUuid(String groupUuid);

    @Transactional
    void deleteByUuid(String uuid);

    @Transactional
    void deleteByUuidIn(List<String> uuids);

    @Query("SELECT e FROM event e WHERE e.organizerUsername = :username")
    List<Event> findOrganizedEvents(String username);

    @Query("FROM event e INNER JOIN e.participants ep WHERE ep.username = :username")
    List<Event> findJoinedEvents(String username);

    @Transactional
    @Modifying
    @Query("DELETE FROM event e WHERE e.groupUuid = :groupUuid AND e.organizerUsername = :username")
    void deleteFromGroupByOrganizerUsername(String groupUuid, String username);
}