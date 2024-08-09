package boerenkool.database.repository;

import boerenkool.business.model.User;
import boerenkool.database.dao.mysql.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTests {

    // Defineer een instantie van de klasse die we testen:
    private UserRepository userRepository;

    // Definieer mocks voor alle afhankelijkheden van die klasse:
    private final UserDAO userDAOMock = Mockito.mock(UserDAO.class);

    // Definieer wat testdata:
    private static final User user_0 = new User("verhuurder", "leo", "welkom");
    private static final User user_1 = new User("huurder", "bart", "welkom");
    private static final User user_2 = new User("verhuurder", "adnan", "welkom");

    public UserRepositoryTests() {
        super();
        // Voorzie de gemockte afhankelijkheden bij het aanmaken van een testinstantie
        userRepository = new UserRepository(userDAOMock);
    }

    @BeforeAll
    public void setup() {
        // Stel gedrag in voor de gemockte methoden
        Mockito.when(userDAOMock.getOneById(1)).thenReturn(Optional.of(user_1));
        Mockito.when(userDAOMock.getOneById(Mockito.anyInt())).thenReturn(Optional.empty());

        // Stel gedrag in voor niet-void methoden
        Mockito.doAnswer(invocationOnMock -> Optional.of(user_2)).when(userDAOMock).getOneById(2);
        //bestaat nog niet
       // Mockito.doAnswer(invocationOnMock -> Optional.of(user_1)).when(userDAOMock).getOneByUsername("bart");

        // Voor de void methode storeOne(), gebruik een custom Answer
        Mockito.doAnswer(invocationOnMock -> {
            User user = (User) invocationOnMock.getArgument(0);
            user.setUserId(1000); // Stel ID in, net zoals een echte DAO zou doen
            return null;
        }).when(userDAOMock).storeOne(Mockito.any(User.class));
    }

    @Test
    void saveUser_test_1() {
        userRepository.storeOne(user_0);
        assertThat(user_0).isNotNull();
        assertThat(user_0.getUserId()).isGreaterThan(0);
        assertThat(user_0.getUserId()).isEqualTo(1000);
    }

    @Test
    void findUserById_test_1() {
        Optional<User> actual = userRepository.getOneById(1);
        assertThat(actual).isNotEmpty().contains(user_1);
    }

    @Test
    void findUserById_test_2() {
        Optional<User> actual = userRepository.getOneById(5);
        assertThat(actual).isEmpty();
    }

 /*   @Test
    void findUserByUsername_test_1() {
        Optional<User> actual = userRepository.getOneByUsername("bart");
        assertThat(actual).isNotNull().isNotEmpty().contains(user_1);
        assertThat(actual.get().getUsername()).isEqualTo("bart");
    }*/

    @Test
    void getUserDao_test_1() {
        assertThat(userDAOMock).isNotNull();
    }
}
