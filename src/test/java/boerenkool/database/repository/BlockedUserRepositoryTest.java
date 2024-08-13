/*
package boerenkool.database.repository;

import boerenkool.business.model.User;
import boerenkool.database.dao.mysql.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

//Deze annotatie specificeert dat er slechts één instantie van de testklasse wordt aangemaakt voor de gehele testclass
// in plaats van voor elke afzonderlijke testmethode.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BlockedUserRepositoryTest {

    // Defineer een instantie van de klasse die we testen:
    private BlockedUserRepository blockedUserRepository;

    // Definieer mocks voor alle afhankelijkheden van die klasse:
    private final BlockedUserDAO blockedUserDAOMock = Mockito.mock(BlockedUserDAO.class);
    private final UserDAO userDAOMock = Mockito.mock(UserDAO.class);
    //testdata
    // Testdata voor de eerste blokkeringsrelatie
    private static final User user1 = new User("verhuurder", "kurpie", "ww", "leo@email.nl", "123456", "leo", null, "kurpershoek", 500);
    private static final User user2 = new User("huurder", "julee", "ww", "julee@email.nl", "12345678", "julee", null, "blom", 200);
    private static final BlockedUser blockedRelation1 = new BlockedUser(user1, user2);

    // Testdata voor de tweede blokkeringsrelatie
    private static final User user3 = new User("verhuurder", "janedoe", "ww", "jane@email.nl", "987654", "jane", null, "doe", 300);
    private static final User user4 = new User("huurder", "johndoe", "ww", "john@email.nl", "87654321", "john", null, "doe", 150);
    private static final BlockedUser blockedRelation2 = new BlockedUser(user3, user4);

    @BeforeAll
    public void setUp() {
        // Instantieer de repository die we gaan testen
        blockedUserRepository = new BlockedUserRepository(blockedUserDAOMock, userDAOMock);

        // Stel het gedrag van de mocks in voor de eerste blokkeringsrelatie
        Mockito.when(blockedUserDAOMock.isUserBlocked(user1, user2)).thenReturn(true);
        Mockito.when(blockedUserDAOMock.isUserBlocked(user2, user1)).thenReturn(false);

        Mockito.doNothing().when(blockedUserDAOMock).addBlockedUser(blockedRelation1);
        Mockito.doNothing().when(blockedUserDAOMock).removeBlockedUser(blockedRelation1);

        // Stel het gedrag van de mocks in voor de tweede blokkeringsrelatie
        Mockito.when(blockedUserDAOMock.isUserBlocked(user3, user4)).thenReturn(true);
        Mockito.when(blockedUserDAOMock.isUserBlocked(user4, user3)).thenReturn(false);

        Mockito.doNothing().when(blockedUserDAOMock).addBlockedUser(blockedRelation2);
        Mockito.doNothing().when(blockedUserDAOMock).removeBlockedUser(blockedRelation2);
    }

    @Test
    void addBlockedUser_test(){
        blockedUserRepository.addBlockedUser(blockedRelation1);
        //controleert of de addBlockedUser-methode van de blockedUserDAOMock is aangeroepen
        // met het object blockedRelation1 als parameter. je kijkt of de code daadwerkelijk de verwachte interacties uitvoert
        Mockito.verify(blockedUserDAOMock).addBlockedUser(blockedRelation1);

        blockedUserRepository.addBlockedUser(blockedRelation2);
        Mockito.verify(blockedUserDAOMock).addBlockedUser(blockedRelation2);
    }

    @Test
    void removeBlockedUser_test() {
        // Mock het gedrag om true terug te geven wanneer blockedRelation1 wordt verwijderd
        Mockito.when(blockedUserDAOMock.removeBlockedUser(blockedRelation1)).thenReturn(true);

        //test het verwijderen van de relatie
        boolean result = blockedUserRepository.removeBlockedUser(blockedRelation1);
        //controleer de verwijdering
        assertThat(result).isTrue();
        // Verifieer dat de methode removeBlockedUser is aangeroepen met blockedRelation1
        Mockito.verify(blockedUserDAOMock).removeBlockedUser(blockedRelation1);

        // Mock het gedrag om false terug te geven wanneer blockedRelation2 wordt verwijderd
        Mockito.when(blockedUserDAOMock.removeBlockedUser(blockedRelation2)).thenReturn(false);

        // Test het verwijderen van blockedRelation2
        result = blockedUserRepository.removeBlockedUser(blockedRelation2);
        // Controleer of de verwijdering niet succesvol was
        assertThat(result).isFalse();
        // Verifieer dat de methode removeBlockedUser is aangeroepen met blockedRelation2
        Mockito.verify(blockedUserDAOMock).removeBlockedUser(blockedRelation2);

    }

    void isUserBlocked_test() {
        //test of user1 user2 heeft geblockkeerd
        boolean result = blockedUserRepository.isUserBlocked(user1, user2);

        assertThat(result).isTrue();
        // Verifieer dat de methode isUserBlocked is aangeroepen met user1 en user2
        Mockito.verify(blockedUserDAOMock).isUserBlocked(user1, user2);

        // Test of user3 user4 heeft geblokkeerd
        boolean result2= blockedUserRepository.isUserBlocked(user3, user4);
        // Controleer of het resultaat false is
        assertThat(result2).isFalse();
        // Verifieer dat de methode isUserBlocked is aangeroepen met user3 en user4
        Mockito.verify(blockedUserDAOMock).isUserBlocked(user3, user4);
    }

    @Test
    void getBlockedUsers_test() {
        // Test het ophalen van geblokkeerde gebruikers door user2
        List<User> blockedUsers = blockedUserRepository.getBlockedUsers(user2);
        // Controleer of user1 in de lijst van geblokkeerde gebruikers staat
        assertThat(blockedUsers).contains(user1);
        // Verifieer dat de methode getBlockedUsers is aangeroepen met user2
        Mockito.verify(blockedUserDAOMock).getBlockedUsers(user2);

        // Test het ophalen van geblokkeerde gebruikers door user4
        blockedUsers = blockedUserRepository.getBlockedUsers(user4);
        // Controleer of de lijst leeg is
        assertThat(blockedUsers).isEmpty();
        // Verifieer dat de methode getBlockedUsers is aangeroepen met user4
        Mockito.verify(blockedUserDAOMock).getBlockedUsers(user4);
    }

}
*/
