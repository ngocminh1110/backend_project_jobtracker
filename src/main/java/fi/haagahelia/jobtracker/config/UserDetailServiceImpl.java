package fi.haagahelia.jobtracker.config;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fi.haagahelia.jobtracker.domain.AppUser;
import fi.haagahelia.jobtracker.domain.AppUserRepository;

// SPRING SECURITY USES THIS CLASS TO FIND THE USER FROM THE DATABASE AT LOGIN
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final AppUserRepository userRepository;

    // CONSTRUCTOR INJECTION: SPRING GIVES US THE REPOSITORY
    public UserDetailServiceImpl(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser currentUser = userRepository.findByUsername(username);

        // WRONG USERNAME: LOGIN PAGE SHOWS AN ERROR INSTEAD OF THE APP CRASHING
        if (currentUser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // GIVES SPRING SECURITY THE USERNAME, PASSWORD HASH AND ROLE
        // SPRING SECURITY THEN CHECKS THE TYPED PASSWORD WITH BCRYPT
        return new User(username, currentUser.getPasswordHash(),
                AuthorityUtils.createAuthorityList(currentUser.getRole()));
    }
}