/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package election;

import election.datastoreimpl.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;


/**
 *
 * @author mohammed
 */
@ApplicationScoped
public class ElectionServiceProvider {


    private ElectionDataStore electionDataStore;
    
    @PostConstruct
    public void init() {
        electionDataStore = new EmptyElectionDataStore();
        LocalDateTime preStartsAt = LocalDateTime.now(); 
        LocalDateTime startsAt = preStartsAt.plusMinutes(1);
        LocalDateTime endsAt = startsAt.plusMinutes(1);
        //electionDataStore.createElection("Student Council election",preStartsAt,startsAt,endsAt);
    }

    public ElectionDataStore getElectionDataStore() {
        return electionDataStore;
    }


}

