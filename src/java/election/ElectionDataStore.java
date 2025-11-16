/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package election;

import election.studentcouncil.Candidate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author mohammed
 */
public interface ElectionDataStore {
    
    
    Election createElection(String name, LocalDateTime preStartsAt, LocalDateTime startsAt, LocalDateTime endsAt);
    void deleteElection(String id);

    Optional<Election> findElectionById(String id);
    
    List<Election> findActiveElections();
    
    List<Election> findPastElections();
    
    List<Election> findAllElections();

    Candidate addElectionEntity(Election election, ElectionEntity entity);
    
    void removeElectionEntity(Election election, ElectionEntity entity);

    List<ElectionEntity> findEntitiesByElection(Election election);

    void addVoteToEntity(Election election, ElectionEntity entity);
    int getVotesForEntity(Election election, ElectionEntity entity);
    int getTotalVotes(Election election);

    Optional<Candidate> findCandidateById(Election election, String candidateId);
    
}
