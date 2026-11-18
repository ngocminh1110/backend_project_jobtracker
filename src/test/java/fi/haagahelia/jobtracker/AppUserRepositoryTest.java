package fi.haagahelia.jobtracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import fi.haagahelia.jobtracker.domain.AppUser;
import fi.haagahelia.jobtracker.domain.AppUserRepository;

// TESTS FOR SAVING, FINDING AND DELETING USERS IN THE DATABASE
@DataJpaTest
public class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository userRepository;

    @Test
    public void createNewUser() {
        AppUser user = new AppUser("newtestuser", "hashedpassword", "USER");
        userRepository.save(user);

        // DATABASE GIVES THE NEW USER AN ID
        assertThat(user.getId()).isNotNull();
    }

    @Test
    public void findByUsernameReturnsUser() {
        userRepository.save(new AppUser("searchuser", "hashedpassword", "ADMIN"));

        AppUser user = userRepository.findByUsername("searchuser");

        // THE RIGHT USER IS FOUND WITH THE RIGHT ROLE
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("searchuser");
        assertThat(user.getRole()).isEqualTo("ADMIN");
    }

    @Test
    public void findByUsernameReturnsNullForUnknownUser() {
        // LOGIN USES THIS: UNKNOWN USERNAME GIVES NULL
        AppUser user = userRepository.findByUsername("nobody_like_this");

        assertThat(user).isNull();
    }

    @Test
    public void deleteUser() {
        AppUser user = new AppUser("deletetestuser", "hashedpassword", "USER");
        userRepository.save(user);
        Long id = user.getId();

        userRepository.deleteById(id);

        // USER CANNOT BE FOUND ANYMORE, BY ID OR BY USERNAME
        Optional<AppUser> deletedUser = userRepository.findById(id);
        assertThat(deletedUser).isEmpty();
        assertThat(userRepository.findByUsername("deletetestuser")).isNull();
    }
}