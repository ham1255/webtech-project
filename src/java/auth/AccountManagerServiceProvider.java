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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
            accountManager.register("Mohammed sucks", "mohammed", "1111", Set.of(User.Role.ADMIN, User.Role.STUDENT, User.Role.CANDIDATE));
            accountManager.register("Rashid sheep", "rashid", "1111", Set.of());
            accountManager.register("ahmad lost", "ahmad", "1111", Set.of(User.Role.CANDIDATE));
            accountManager.register("omar nope", "omar", "1111", Set.of(User.Role.ADMIN));
            accountManager.register("ali FUNKO", "ali", "1111", Set.of(User.Role.STUDENT));
            random();
        } catch (Exception ex) {
            System.getLogger(AccountManagerServiceProvider.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    List<String> firstNames = Arrays.asList(
            "Mohammed", "Ahmed", "Saeed", "Ali", "Rashid", "Khalid", "Salman", "Yousef", "Ibrahim", "Fahad",
            "Nasser", "Abdullah", "Abdulaziz", "Hamad", "Faisal", "Saif", "Rakan", "Majed", "Suheil", "Talal",
            "Marwan", "Jaber", "Rami", "Ziyad", "Omar", "Fares", "Bader", "Anwar", "Nawaf", "Raed", "falafel"
    );

    List<String> lastNames = Arrays.asList(
            // United Arab Emirates
            "AlMansoori", "AlNuaimi", "AlKaabi", "AlKetbi", "AlDhaheri", "AlMehairi", "AlShamsi",
            "AlQasimi", "AlMarzouqi", "AlFalasi", "AlMazrouei", "AlSuwaidi", "AlAbri", "AlBlooshi",
            "AlHameli", "AlRemeithi", "AlAmeri", "AlTunaiji", "AlYasi", "AlZaabi", "AlAli", "AlShehhi",
            "AlHammadi", "AlSharqi", "AlJasmi", "AlShamsi", "AlSayegh", "AlSarkal",
            //  Saudi Arabia
            "AlSaud", "AlRasheed", "AlOtaibi", "AlMutairi", "AlHarbi", "AlQahtani", "AlDosari", "AlAjmi",
            "AlShammari", "AlSubaie", "AlAnzi", "AlGhamdi", "AlZahrani", "AlHarthy", "AlBalawi", "AlMarri",
            "AlOmari", "AlShehri", "AlSudairi", "AlHumaidi", "AlHazmi", "AlJuhani", "AlFahad", "AlMuraikhi",
            // Qatar
            "AlThani", "AlKuwari", "AlMarri", "AlSuwaidi", "AlKhalifa", "AlMahmoud", "AlMannai", "AlAnsari",
            "AlObaidly", "AlNaimi", "AlJaber", "AlMisnad", "AlBaker", "AlBuainain", "AlAbdulla", "AlSulaiti",
            "AlEmadi", "AlKawari", "AlKhater",
            //  Oman
            "AlHinai", "AlBalushi", "AlRashdi", "AlMahrouqi", "AlFarsi", "AlHabsi", "AlShibli", "AlRawahi",
            "AlZadjali", "AlMaskari", "AlLawati", "AlKhalili", "AlBusaidi", "AlYahyaei", "AlNabhani",
            "AlRiyami", "AlSiyabi", "AlMakhmari", "AlAghbari",
            //  Kuwait
            "AlSabah", "AlMutairi", "AlAjmi", "AlRashidi", "AlEnezi", "AlOtaibi", "AlFahad", "AlMazidi",
            "AlDossari", "AlHarbi", "AlMutawa", "AlMulla", "AlKhalid", "AlMansour", "AlGhanim", "AlBader",
            "AlShaya", "AlHajri", "AlOmani",
            //  Bahrain
            "AlKhalifa", "AlMannai", "AlNoaimi", "AlJowder", "AlZayani", "AlArayedh", "AlArrayed", "AlQassim",
            "AlSaeed", "AlFardan", "AlMahmood", "AlBinali", "AlAali", "AlMoayyed", "AlAmeer", "AlAnsari",
            "AlNasser", "AlAlawi",
            //  Jordan
            "AlMajali", "AlFayez", "AlKhasawneh", "AlRifai", "AlTarawneh", "AlMasri", "AlAbbadi", "AlBataineh",
            "AlZoubi", "AlMomani", "AlRawashdeh", "AlShawabkeh", "AlHuneiti", "AlKhateeb", "AlAdwan", "AlArmouti",
            "AlMadi", "AlQudah", "AlAjarmeh", "AlFarra"
    );

    private void random() throws Exception {
        Random random = new Random();
        List<User.Role> allRoles = Arrays.asList(User.Role.STUDENT, User.Role.CANDIDATE);

        // Generate 60 users
        for (int i = 1; i <= 60; i++) {
            String first = firstNames.get(random.nextInt(firstNames.size()));
            String last = lastNames.get(random.nextInt(lastNames.size()));
            String fullName = first + " " + last;

            // simple username (lowercase, no spaces)
            String username = (first.substring(0, 1) + last).toLowerCase() + i + "@mail.com";

            // Random role selection (1–3 roles)
            Set<User.Role> roles = new HashSet<>();
            int roleCount = random.nextInt(2) + 1; // between 1 and 2
            Collections.shuffle(allRoles);
            for (int j = 0; j < roleCount; j++) {
                
                roles.add(allRoles.get(j));
            }

            accountManager.register(fullName, username, "1111", roles);
        }
    }
}
