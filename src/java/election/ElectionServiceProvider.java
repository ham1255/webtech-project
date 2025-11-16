/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package election;

import election.datastoreimpl.MemoryElectionDataStore;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.time.LocalDateTime;


/**
 *
 * @author mohammed
 */
@Singleton
@Startup
public class ElectionServiceProvider {


    private ElectionDataStore electionDataStore;
    
    @PostConstruct
    public void init() {
        electionDataStore = new MemoryElectionDataStore();
        LocalDateTime preStartsAt = LocalDateTime.now(); 
        LocalDateTime startsAt = preStartsAt.plusMinutes(1);
        LocalDateTime endsAt = startsAt.plusMinutes(1);
        electionDataStore.createElection("Student Council election",preStartsAt,startsAt,endsAt);
    }

    public ElectionDataStore getElectionDataStore() {
        return electionDataStore;
    }


}

