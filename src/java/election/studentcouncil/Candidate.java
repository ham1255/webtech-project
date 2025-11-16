/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package election.studentcouncil;

import election.ElectionEntity;
import election.ElectionEntity;
import election.studentcouncil.StudentCouncilElectionChair;
import java.util.Map;

/**
 *
 * @author mohammed
 */
public class Candidate implements ElectionEntity {

    private final String userID;
    private final StudentCouncilElectionChair chair;
    private final String electionID;

    public Candidate(String userID, StudentCouncilElectionChair chair, String electionID) {
        this.userID = userID;
        this.chair = chair;
        this.electionID = electionID;
    }

    public String getUserID() {
        return userID;
    }

    public StudentCouncilElectionChair getChair() {
        return chair;
    }

    public String getElectionID() {
        return electionID;
    }

    @Override
    public String getID() {
        return userID;
    }

    @Override
    public Map<String, Object> getData() {
        return Map.of("user-id", userID, "chair", chair, "election-id", electionID);
    }

}
