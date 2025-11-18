/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package polling.election;

/**
 *
 * @author mohammed
 */
import java.time.LocalDateTime;
import polling.CommonPoll;

public class Election extends CommonPoll {

    private final String electionId;
    private String name;
    private LocalDateTime registerationStartsAt;

    public Election(String electionId, String name, LocalDateTime registerationStartsAt, LocalDateTime startsAt, LocalDateTime endsAt) {
        super(startsAt, endsAt);
        this.electionId = electionId;
        this.name = name;
        this.registerationStartsAt = registerationStartsAt;

    }

    public LocalDateTime registerationStartsAt() {
        return registerationStartsAt;
    }

    public String getElectionId() {
        return electionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegisterationStartsAt(LocalDateTime registerationStartsAt) {
        this.registerationStartsAt = registerationStartsAt;
    }

    public boolean isRegisterationPeriod() {
        LocalDateTime now = LocalDateTime.now();
        return (now.isAfter(registerationStartsAt) || now.isEqual(registerationStartsAt))
                && now.isBefore(startsAt);
    }

}
