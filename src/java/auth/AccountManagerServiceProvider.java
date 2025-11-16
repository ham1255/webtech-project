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
            accountManager.register("202311566","Mohammed jasem alteneiji", "202311566@ajmanuni.ac.ae", "1111", Set.of(User.Role.ADMIN, User.Role.STUDENT, User.Role.CANDIDATE));
            accountManager.register("202310776","Ahmad ashraf Tarawneh", "202310776@ajmanuni.ac.ae", "1111", Set.of(User.Role.ADMIN, User.Role.STUDENT, User.Role.CANDIDATE));
            accountManager.register("202311242","Abdijabar Ahmed Mohamed", "202311242@ajmanuni.ac.ae", "1111", Set.of(User.Role.ADMIN, User.Role.STUDENT, User.Role.CANDIDATE));
            accountManager.register("202310156","Omar Magd Juratly", "202310156@ajmanuni.ac.ae", "1111", Set.of(User.Role.ADMIN, User.Role.STUDENT, User.Role.CANDIDATE));
            accountManager.register("202310454","Omar Abdulnasser Talal Haneyeh", "202310454@ajmanuni.ac.ae", "1111", Set.of(User.Role.ADMIN, User.Role.STUDENT, User.Role.CANDIDATE));
            
            random(60);
           
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
            "Marwan", "Jaber", "Rami", "Ziyad", "Omar", "Fares", "Bader", "Anwar", "Nawaf", "Raed", "Falafel bin sheep"
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
            "AlMadi", "AlQudah", "AlAjarmeh", "AlFarra",
            // syria
            "Baggara",
            "Enezi",
            "Shammar",
            "Jubur",
            "Taie",
            "Dulaim",
            "Hadeediyin",
            "Walda",
            "Na'im",
            "Rwala",
            "Fadl",
            "Mawali",
            "Ageidat",
            "Bani Khalid",
            "Sarhan",
            "Al Bu Saraya",
            "Al Bu Kamal",
            "Al Bu Khabur",
            "Al Bu Hamdan",
            "Al Bu Hassan"
    );

    private void random(int x) throws Exception {
        Random random = new Random();
        List<User.Role> allRoles = Arrays.asList(User.Role.STUDENT, User.Role.CANDIDATE);
        if (x < 1) x = 1;
        // Generate x users
        final int base = 200000000;
        for (int i = 1; i <= x; i++) {
            String first = firstNames.get(random.nextInt(firstNames.size()));
            String last = lastNames.get(random.nextInt(lastNames.size()));
            String fullName = first + " " + last;

            // simple username (lowercase, no spaces)
            String username = (base + x) + "@ajmanuni.ac.ae";

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
