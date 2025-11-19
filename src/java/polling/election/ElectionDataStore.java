/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package polling.election;

import auth.User;
import database.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author mohammed
 */
public interface ElectionDataStore {

    public static class PageableElections extends Pageable<Election> {

        public PageableElections(Set<Election> elections, int maxPages, int currentPage) {
            super(elections, maxPages, currentPage);
        }

        public Set<Election> getElections() {
            return getT();
        }

    }

    Election createElection(String name, LocalDateTime preStartsAt, LocalDateTime startsAt, LocalDateTime endsAt);

    void deleteElection(String id);
    
    void updateElection(Election election);

    Optional<Election> findElectionById(String id);

    List<Election> findActiveElections();

    List<Election> findPastElections();

    List<Election> findAllElections();

    PageableElections findActiveElectionsByPage(int page);

    PageableElections findPastElectionsByPage(int page);

    PageableElections findAllElectionsByPage(int page);

    Candidate addCandidate(Election election, Candidate candidate);

    void removeCandidate(Election election, Candidate candidate);

    List<Candidate> findCandidatesByElection(Election election);

    void addVoteToCandidate(Election election, User voter, Candidate candidate);

    int getVotesForCandidate(Election election, Candidate candidate);
    
    Map<StudentCouncilElectionChair, Map<String, Integer>> getCandidatesVotesPerChair(Election election);

    int getTotalVotes(Election election);
    
    boolean hasUserVoted(Election election, User voter);

    Optional<Candidate> findCandidateById(Election election, String candidateId);

}
