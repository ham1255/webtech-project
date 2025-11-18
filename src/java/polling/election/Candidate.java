/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package polling.election;

import jakarta.annotation.Nullable;

/**
 *
 * @author mohammed
 */
public class Candidate {

    private final String userID;
    private final StudentCouncilElectionChair chair;
    private final String electionID;
    private String name;

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

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    

}
