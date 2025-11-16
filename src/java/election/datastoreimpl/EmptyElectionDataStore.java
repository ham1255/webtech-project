/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package election.datastoreimpl;

import election.Election;
import election.ElectionDataStore;
import election.ElectionEntity;
import election.studentcouncil.Candidate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author mohammed
 */
public class EmptyElectionDataStore implements ElectionDataStore{

    @Override
    public Election createElection(String name, LocalDateTime preStartsAt, LocalDateTime startsAt, LocalDateTime endsAt) {
        return null;
    }

    @Override
    public void deleteElection(String id) {
       
    }

    @Override
    public Optional<Election> findElectionById(String id) {
        return Optional.empty();
    }

    @Override
    public List<Election> findActiveElections() {
        return List.of();
    }

    @Override
    public List<Election> findPastElections() {
    return List.of();
    }

    @Override
    public List<Election> findAllElections() {
    return List.of();
    }

    @Override
    public Candidate addElectionEntity(Election election, ElectionEntity entity) {
        return null;
    }

    @Override
    public void removeElectionEntity(Election election, ElectionEntity entity) {
        
    }

    @Override
    public List<ElectionEntity> findEntitiesByElection(Election election) {
        return List.of();
    }

    @Override
    public void addVoteToEntity(Election election, ElectionEntity entity) {
     }

    @Override
    public int getVotesForEntity(Election election, ElectionEntity entity) {
        return 0;
    }

    @Override
    public int getTotalVotes(Election election) {
        return 0;
    }

    @Override
    public Optional<Candidate> findCandidateById(Election election, String candidateId) {
       return Optional.empty();
    }
    
}
