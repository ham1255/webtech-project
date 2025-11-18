/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package polling.election;

import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author mohammed
 *
 */
public enum StudentCouncilElectionChair {
    PRESIDENT("President", 1),
    VICE_PRESIDENT("Vice President", 1),
    SECRETARY("Secretary", 1),
    TREASURER("Treasurer", 1),
    MEMBER("Member", 5);

    private final String displayName;
    private final int maxSeats;

    StudentCouncilElectionChair(String displayName, int maxSeats) {
        this.displayName = displayName;
        this.maxSeats = maxSeats;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxSeats() {
        return maxSeats;
    }


    // DEMO ONLY!!!!
    public static StudentCouncilElectionChair random() {
        StudentCouncilElectionChair[] vals = values();
        return vals[ThreadLocalRandom.current().nextInt(vals.length)];
    }

}
