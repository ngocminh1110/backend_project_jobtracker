package fi.haagahelia.jobtracker;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import fi.haagahelia.jobtracker.domain.AppUser;
import fi.haagahelia.jobtracker.domain.AppUserRepository;
import fi.haagahelia.jobtracker.domain.ApplicationStatus;
import fi.haagahelia.jobtracker.domain.JobApplication;
import fi.haagahelia.jobtracker.domain.JobApplicationRepository;

@SpringBootApplication
public class JobtrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobtrackerApplication.class, args);
	}

	// ADDS DEMO USERS AND JOBS WHEN THE APP STARTS
	@Bean
	public CommandLineRunner initializeDatabase(JobApplicationRepository applicationRepository,
			AppUserRepository userRepository) {
		return (args) -> {
			// DATA STAYS IN MYSQL, SO DEMO DATA IS ADDED ONLY ON THE FIRST START
			if (userRepository.count() > 0) {
				return;
			}

			// PASSWORDS ARE SAVED ONLY AS BCRYPT HASH, NEVER AS PLAIN TEXT
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

			// FIRST USER: ADMIN
			AppUser owner = new AppUser("admin", passwordEncoder.encode("admin"), "ADMIN");
			userRepository.save(owner);
			System.out.println(owner);

			// JOBS OF ADMIN
			JobApplication jobApp1 = new JobApplication(
					"Finnair", "Take off- Finnair Trainee Program", "Vantaa, Uusimaa, Finland",
					"https://www.linkedin.com/jobs/view/4486118762/",
					LocalDate.of(2026, 11, 5), ApplicationStatus.APPLIED);
			jobApp1.setAppUser(owner);
			applicationRepository.save(jobApp1);

			JobApplication jobApp2 = new JobApplication(
					"Konecranes", "Trainee, Business Analyst", "Hyvinkää",
					"https://www.linkedin.com/jobs/view/4484367316/",
					LocalDate.of(2026, 11, 5), ApplicationStatus.APPLIED);
			jobApp2.setAppUser(owner);
			applicationRepository.save(jobApp2);

			// NO APPLIED DATE YET, BECAUSE IT IS ONLY ON THE WISHLIST
			JobApplication jobApp3 = new JobApplication(
					"Wolt", "Software Engineer", "Finland",
					"https://careers.wolt.com/en/jobs/1/8159098",
					null, ApplicationStatus.WISHLIST);
			jobApp3.setAppUser(owner);
			applicationRepository.save(jobApp3);

			// SECOND USER: DEMO, TO TEST THAT EACH USER SEES ONLY OWN JOBS
			AppUser demo = new AppUser("demo", passwordEncoder.encode("demo"), "USER");
			userRepository.save(demo);
			System.out.println(demo);

			// JOB OF DEMO
			JobApplication jobApp4 = new JobApplication(
					"HOK-Elanto", "Junior Analyst", "Helsinki",
					"https://www.linkedin.com/jobs/view/4485314188/",
					LocalDate.of(2026, 11, 1), ApplicationStatus.INTERVIEW);
			jobApp4.setAppUser(demo);
			applicationRepository.save(jobApp4);

			// PRINTS ALL JOBS TO THE TERMINAL TO CHECK THAT SAVING WORKS
			for (JobApplication application : applicationRepository.findAll()) {
				System.out.println(application);
			}
		};
	}
}