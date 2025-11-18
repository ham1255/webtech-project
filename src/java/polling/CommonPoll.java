/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package polling;

import java.time.LocalDateTime;

/**
 *
 * @author mohammed
 */
public abstract class CommonPoll {

    protected LocalDateTime startsAt;
    protected LocalDateTime endsAt;

    public CommonPoll(LocalDateTime startsAt, LocalDateTime endsAt) {
        this.startsAt = startsAt;
        this.endsAt = endsAt;
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

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

}
