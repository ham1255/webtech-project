/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth;

import auth.datastoreimpls.MemoryAuthDataStore;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.time.Duration;
import java.util.Set;

/**
 *
 * @author mohammed
 */
@Singleton
@Startup
public class AccountManagerServiceProvider {
    
    
    private AccountManager accountManager;

    @PostConstruct
    public void init() {
        AuthDataStore store = new MemoryAuthDataStore();
        accountManager = new AccountManager(store, Duration.ofDays(7));
        try {
            accountManager.register("Mohammed jasem mohammed alaajel alteneiji","mohammed", "1111", Set.of(User.Role.ADMIN,User.Role.STUDENT ,User.Role.CANDIDATE));
        } catch (Exception ex) {
            System.getLogger(AccountManagerServiceProvider.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }
    
}
