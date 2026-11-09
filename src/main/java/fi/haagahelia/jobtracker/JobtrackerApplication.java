package fi.haagahelia.jobtracker;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import fi.haagahelia.jobtracker.domain.ApplicationStatus;
import fi.haagahelia.jobtracker.domain.JobApplication;
import fi.haagahelia.jobtracker.domain.JobApplicationRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import fi.haagahelia.jobtracker.domain.AppUser;
import fi.haagahelia.jobtracker.domain.AppUserRepository;

@SpringBootApplication
public class JobtrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobtrackerApplication.class, args);
	}

	// Add some demo data when the app starts
	@Bean
	public CommandLineRunner initializeDatabase(JobApplicationRepository applicationRepository,
			AppUserRepository userRepository) {
		return (args) -> {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			AppUser owner = new AppUser("admin", passwordEncoder.encode("admin"), "ADMIN");
			userRepository.save(owner);
			System.out.println(owner);
			applicationRepository.save(new JobApplication(
					"Finnair", "Take off- Finnair Trainee Program", "Vantaa, Uusimaa, Finland",
					"https://www.linkedin.com/jobs/view/4486118762/?alternateChannel=search&eBP=NON_CHARGEABLE_CHANNEL&refId=lUngqPtJZLGGOG7mPojtCQ%3D%3D&trackingId=WBRxGeb7GUYWETcWiWtp%2FA%3D%3D",
					LocalDate.of(2026, 11, 5), ApplicationStatus.APPLIED));

			applicationRepository.save(new JobApplication(
					"Konecranes", "Trainee, Business Analyst", "Hyvinkää",
					"https://www.linkedin.com/jobs/view/4484367316/?alternateChannel=search&eBP=NON_CHARGEABLE_CHANNEL&refId=9tSglSQvv6hyNaqwCcd9ow%3D%3D&trackingId=rUNxT7hhfwvNmWWcQTktdQ%3D%3D",
					LocalDate.of(2026, 11, 5), ApplicationStatus.APPLIED));

			applicationRepository.save(new JobApplication(
					"Wolt", "Software Engineer", "Finland",
					"https://careers.wolt.com/en/jobs/1/8159098",
					null, ApplicationStatus.WISHLIST));

			// Print all applications to the terminal to check that saving works
			for (JobApplication application : applicationRepository.findAll()) {
				System.out.println(application);
			}
		};
	}
}