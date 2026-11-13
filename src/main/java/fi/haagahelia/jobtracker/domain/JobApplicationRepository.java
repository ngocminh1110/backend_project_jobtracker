package fi.haagahelia.jobtracker.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface JobApplicationRepository extends CrudRepository<JobApplication, Long> {

    // I can find all applications with one status, or all INTERVIEW
    List<JobApplication> findByStatus(ApplicationStatus status);

    List<JobApplication> findByCompany(String company);

    List<JobApplication> findByAppUser(AppUser appUser);
}