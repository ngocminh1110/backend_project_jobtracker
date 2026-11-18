package fi.haagahelia.jobtracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import fi.haagahelia.jobtracker.domain.AppUser;
import fi.haagahelia.jobtracker.domain.AppUserRepository;
import fi.haagahelia.jobtracker.domain.ApplicationStatus;
import fi.haagahelia.jobtracker.domain.JobApplication;
import fi.haagahelia.jobtracker.domain.JobApplicationRepository;

// TESTS FOR SAVING, FINDING AND DELETING JOBS IN THE DATABASE
@DataJpaTest
public class JobApplicationRepositoryTest {

    @Autowired
    private JobApplicationRepository applicationRepository;

    @Autowired
    private AppUserRepository userRepository;

    // HELPER: MAKES AND SAVES ONE JOB FOR A USER
    private JobApplication saveJob(String company, ApplicationStatus status, AppUser user) {
        JobApplication job = new JobApplication(company, "Developer", "Helsinki",
                null, LocalDate.of(2026, 11, 1), status);
        job.setAppUser(user);
        return applicationRepository.save(job);
    }

    @Test
    public void createNewJob() {
        AppUser user = userRepository.save(new AppUser("tester", "hash", "USER"));

        JobApplication job = saveJob("Test Company", ApplicationStatus.APPLIED, user);

        // DATABASE GIVES THE NEW JOB AN ID
        assertThat(job.getId()).isNotNull();
    }

    @Test
    public void findByAppUserReturnsOnlyOwnJobs() {
        AppUser anna = userRepository.save(new AppUser("anna", "hash", "USER"));
        AppUser ben = userRepository.save(new AppUser("ben", "hash", "USER"));
        saveJob("Anna Company 1", ApplicationStatus.APPLIED, anna);
        saveJob("Anna Company 2", ApplicationStatus.WISHLIST, anna);
        saveJob("Ben Company", ApplicationStatus.APPLIED, ben);

        List<JobApplication> annaJobs = applicationRepository.findByAppUser(anna);

        // ANNA SEES ONLY HER OWN TWO JOBS, NOT BEN'S JOB
        assertThat(annaJobs).hasSize(2);
        assertThat(annaJobs).extracting(JobApplication::getCompany)
                .containsExactlyInAnyOrder("Anna Company 1", "Anna Company 2");
    }

    @Test
    public void findByStatusReturnsJobs() {
        AppUser user = userRepository.save(new AppUser("statususer", "hash", "USER"));
        saveJob("Interview Company", ApplicationStatus.INTERVIEW, user);
        saveJob("Applied Company", ApplicationStatus.APPLIED, user);

        List<JobApplication> interviews = applicationRepository.findByStatus(ApplicationStatus.INTERVIEW);

        // OUR INTERVIEW JOB IS FOUND, THE APPLIED JOB IS NOT
        assertThat(interviews).extracting(JobApplication::getCompany)
                .contains("Interview Company")
                .doesNotContain("Applied Company");
        // EVERY JOB IN THE RESULT HAS STATUS INTERVIEW
        assertThat(interviews).allMatch(job -> job.getStatus() == ApplicationStatus.INTERVIEW);
    }

    @Test
    public void deleteJob() {
        AppUser user = userRepository.save(new AppUser("deleteuser", "hash", "USER"));
        JobApplication job = saveJob("Delete Company", ApplicationStatus.REJECTED, user);
        Long id = job.getId();

        applicationRepository.deleteById(id);

        Optional<JobApplication> deletedJob = applicationRepository.findById(id);
        assertThat(deletedJob).isEmpty();
    }
}