package fi.haagahelia.jobtracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import fi.haagahelia.jobtracker.domain.AppUser;
import fi.haagahelia.jobtracker.domain.AppUserRepository;
import fi.haagahelia.jobtracker.domain.ApplicationStatus;
import fi.haagahelia.jobtracker.domain.JobApplication;
import fi.haagahelia.jobtracker.domain.JobApplicationRepository;

// TESTS THE WEB PAGES: LOGIN, SIGN UP AND THAT EACH USER SEES ONLY OWN JOBS
// THE WHOLE APP STARTS, BUT WITHOUT A REAL SERVER (MockMvc)
@SpringBootTest
@AutoConfigureMockMvc
public class WebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private JobApplicationRepository applicationRepository;

    // BEFORE EACH TEST: MAKE TWO TEST USERS WITH ONE JOB EACH (ONLY IF THEY DON'T
    // EXIST YET)
    @BeforeEach
    public void createTestData() {
        createUserWithJob("webanna", "Anna Web Job");
        createUserWithJob("webben", "Ben Web Job");
    }

    private void createUserWithJob(String username, String company) {
        if (userRepository.findByUsername(username) != null) {
            return;
        }
        AppUser user = userRepository.save(new AppUser(username, "hash", "USER"));
        JobApplication job = new JobApplication(company, "Developer", "Helsinki",
                null, LocalDate.of(2026, 11, 1), ApplicationStatus.APPLIED);
        job.setAppUser(user);
        applicationRepository.save(job);
    }

    @Test
    public void loginPageIsOpen() throws Exception {
        // LOGIN PAGE OPENS WITHOUT LOGIN
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Password")));
    }

    @Test
    public void signupPageIsOpen() throws Exception {
        // SIGN UP PAGE OPENS WITHOUT LOGIN
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Repeat Password")));
    }

    @Test
    public void jobListNeedsLogin() throws Exception {
        // WITHOUT LOGIN, THE JOB LIST SENDS THE USER TO THE LOGIN PAGE
        mockMvc.perform(get("/applicationlist"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "webanna", authorities = "USER")
    public void userSeesOnlyOwnJobs() throws Exception {
        // ANNA SEES HER OWN JOB BUT NOT BEN'S JOB
        mockMvc.perform(get("/applicationlist"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Anna Web Job")))
                .andExpect(content().string(not(containsString("Ben Web Job"))));
    }

    @Test
    public void signupWithDifferentPasswordsShowsError() throws Exception {
        // TWO DIFFERENT PASSWORDS: FORM IS SHOWN AGAIN WITH AN ERROR
        mockMvc.perform(post("/saveuser").with(csrf())
                .param("username", "webtester")
                .param("password", "1234567")
                .param("passwordCheck", "7654321"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Passwords do not match")));
    }

    @Test
    public void signupWithTakenUsernameShowsError() throws Exception {
        // USERNAME "webanna" ALREADY EXISTS: FORM IS SHOWN AGAIN WITH AN ERROR
        mockMvc.perform(post("/saveuser").with(csrf())
                .param("username", "webanna")
                .param("password", "1234567")
                .param("passwordCheck", "1234567"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Username already exists")));
    }

    @Test
    public void signupCreatesNewUser() throws Exception {
        // CORRECT FORM: USER IS SAVED AND BROWSER GOES TO LOGIN PAGE
        mockMvc.perform(post("/saveuser").with(csrf())
                .param("username", "webnewuser")
                .param("password", "1234567")
                .param("passwordCheck", "1234567"))
                .andExpect(redirectedUrl("/login?registered"));

        AppUser newUser = userRepository.findByUsername("webnewuser");
        assertThat(newUser).isNotNull();
        // NEW USER ALWAYS GETS ROLE USER, PASSWORD IS SAVED AS HASH
        assertThat(newUser.getRole()).isEqualTo("USER");
        assertThat(newUser.getPasswordHash()).isNotEqualTo("1234567");
    }
}