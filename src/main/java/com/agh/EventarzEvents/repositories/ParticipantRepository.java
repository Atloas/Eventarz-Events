package com.agh.EventarzEvents.repositories;

import com.agh.EventarzEvents.model.Participant;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ParticipantRepository extends CrudRepository<Participant, String> {

    @Transactional
    @Modifying
    @Query("DELETE FROM participant p WHERE p.username = :username AND p.uuid IN (SELECT ps.uuid FROM event e INNER JOIN e.participants ps WHERE e.groupUuid = :groupUuid)")
    void deleteByGroupUuidAndUsername(String groupUuid, String username);
}
