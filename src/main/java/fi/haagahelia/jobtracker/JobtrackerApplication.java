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

	// Add some demo data when the app starts
	@Bean
	public CommandLineRunner initializeDatabase(JobApplicationRepository applicationRepository,
			AppUserRepository userRepository) {
		return (args) -> {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

			// First user: admin
			AppUser owner = new AppUser("admin", passwordEncoder.encode("admin"), "ADMIN");
			userRepository.save(owner);
			System.out.println(owner);

			JobApplication jobApp1 = new JobApplication(
					"Finnair", "Take off- Finnair Trainee Program", "Vantaa, Uusimaa, Finland",
					"https://www.linkedin.com/jobs/view/4486118762/",
					LocalDate.of(2026, 11, 5), ApplicationStatus.APPLIED);
			jobApp1.setAppUser(owner);
			applicationRepository.save(jobApp1);

			JobApplication jobApp3 = new JobApplication(
					"Konecranes", "Trainee, Business Analyst", "Hyvinkää",
					"https://www.linkedin.com/jobs/view/4484367316/",
					LocalDate.of(2026, 11, 5), ApplicationStatus.APPLIED);
			jobApp3.setAppUser(owner);
			applicationRepository.save(jobApp3);

			JobApplication jobApp4 = new JobApplication(
					"Wolt", "Software Engineer", "Finland",
					"https://careers.wolt.com/en/jobs/1/8159098",
					null, ApplicationStatus.WISHLIST);
			jobApp4.setAppUser(owner);
			applicationRepository.save(jobApp4);

			// This is the second user: demo, to test that each user sees only own
			// applications
			AppUser demo = new AppUser("demo", passwordEncoder.encode("demo"), "USER");
			userRepository.save(demo);
			System.out.println(demo);

			JobApplication jobApp5 = new JobApplication(
					"HOK-Elanto", "Junior Analyst ", "Helsinki",
					"https://www.linkedin.com/jobs/view/4485314188/?alternateChannel=search&eBP=CwEAAAGh0apSXIFbZ0DA-mXxy7AERFAwLBWDkex1AAqLaCnwoPpMg7G3nQpKOhb74KQXbkI7xV74wUnofv3Nvu3RNuNTTkxdqP5r8PiCr0N68RUW3z8QoW0gXRS8srLvUdEoPfjtQLchTwjCmRo3kzPwdHtltWYhs6jVUNvdtSR97z7fcC2LSU5QcTiRbR-sBtNAWHBrYiSP6S5x2epPqxsFfmdeBdvb9hT-Xvkr-RP41eq7PzqUzM1hcL7VSS4sAHtvJ8bYyIrIzARLlc0NJ1Nqiv2NWkEv0hijeLSgUapqBw7gv_me6HItvgRjZWhKN1-crEinHpKl0f3S3VP_t87PM5QXMzqg3bBurgjJtKMu-dPWupHcfebfL2SYxrCoPvQ2dsNIW2ZNxdc-PPkQBK3VDQ9DbxrgbsHIprdraMtTueEJBh30j4eKnOV0-zFEqmdQ1VIoafavBKhmiy3cwhIcXnNqcTgHpHr4xMR87mVetg24K63tFRc2G9zYsyKBs36QNA&refId=WMQBAZAUiDIWxrIgotOW5A%3D%3D&trackingId=DtIujyJsrj5Fx4tH7SNyZQ%3D%3D",
					LocalDate.of(2026, 11, 1), ApplicationStatus.INTERVIEW);
			jobApp5.setAppUser(demo);
			applicationRepository.save(jobApp5);

			// Print all applications to the terminal to check that saving works
			for (JobApplication application : applicationRepository.findAll()) {
				System.out.println(application);
			}
		};
	}
}