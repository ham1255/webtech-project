/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package election;

/**
 *
 * @author mohammed
 */
import java.time.LocalDateTime;

public class Election {

    private final String electionId;
    private String name;
    private final LocalDateTime preStartsAt;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private final ElectionType type;
    
    
    public static enum ElectionType {
        STUDENT_COUNCIL, CUSTOM;
    
    }

    public Election(String electionId, String name, LocalDateTime preStartsAt, LocalDateTime startsAt, LocalDateTime endsAt, ElectionType type) {
        this.electionId = electionId;
        this.name = name;
        this.preStartsAt = preStartsAt;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.type = type;
    }

    public LocalDateTime getPreStartsAt() {
        return preStartsAt;
    }


    public String getElectionId() {
        return electionId;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public ElectionType getType() {
        return type;
    }
    
    
 
    public boolean isRegisterationPeriod() {
        LocalDateTime now = LocalDateTime.now();
        return (now.isAfter(preStartsAt) || now.isEqual(preStartsAt))
                && now.isBefore(startsAt);    
         }
    
    public boolean isOpen() {
        LocalDateTime now = LocalDateTime.now();
        return (now.isAfter(startsAt) || now.isEqual(startsAt))
                && now.isBefore(endsAt);
    }
    
    public boolean isClosed() {
         LocalDateTime now = LocalDateTime.now();
        return now.isAfter(endsAt) || now.isEqual(endsAt);
    }

}
