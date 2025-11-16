/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package election.studentcouncil;

/**
 *
 * @author mohammed
 */

public class StudentCouncilElectionChair {

    private final String displayName;
    private final int maxSeats;

    public StudentCouncilElectionChair(String displayName, int maxSeats) {
        this.displayName = displayName;
        this.maxSeats = maxSeats;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxSeats() {
        return maxSeats;
    }
    
    
    public static enum DefaultValues {
        PRESIDENT("President", 1),
        VICE_PRESIDENT("Vice President", 1),
        SECRETARY("Secretary", 1),
        TREASURER("Treasurer", 1),
        MEMBER("Member", 5);
        
        private final StudentCouncilElectionChair seat;
        
        DefaultValues(String displayName, int maxSeats) {
            seat = new StudentCouncilElectionChair(displayName, maxSeats);
        }

        public StudentCouncilElectionChair getSeat() {
            return seat;
        }
       
        
    }
}

