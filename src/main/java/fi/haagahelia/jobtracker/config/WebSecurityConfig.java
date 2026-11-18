package fi.haagahelia.jobtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// SECURITY RULES: WHO CAN OPEN WHICH PAGE
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // CSS AND SIGN UP PAGES ARE OPEN, NEW USERS HAVE NO ACCOUNT YET
                        .requestMatchers("/css/**", "/signup", "/saveuser").permitAll()
                        // ALL OTHER PAGES NEED LOGIN
                        .anyRequest().authenticated())
                .formLogin(formlogin -> formlogin
                        // OUR OWN LOGIN PAGE INSTEAD OF THE DEFAULT ONE
                        .loginPage("/login")
                        // AFTER LOGIN ALWAYS GO TO THE JOB LIST
                        .defaultSuccessUrl("/applicationlist", true)
                        .permitAll())
                .logout(logout -> logout
                        .permitAll());
        return http.build();
    }

    // PASSWORDS ARE CHECKED WITH BCRYPT, SAME AS WHEN THEY ARE SAVED
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}