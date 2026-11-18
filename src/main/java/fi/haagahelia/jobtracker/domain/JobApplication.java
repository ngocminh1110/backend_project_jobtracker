package fi.haagahelia.jobtracker.domain;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// ONE JOB THAT THE USER WANTS TO APPLY OR HAS APPLIED TO
@Entity
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long jobApplicationId;

    // COMPANY AND POSITION ARE REQUIRED, FORM SHOWS RED ERROR IF EMPTY
    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Position is required")
    private String position;

    private String location;

    // LINKEDIN LINKS ARE LONG, SO WE ALLOW UP TO 2000 CHARACTERS (DEFAULT IS 255)
    @Size(min = 0, max = 2000)
    private String jobUrl;

    // HTML DATE INPUT SENDS DATE AS yyyy-MM-dd, THIS TURNS IT INTO LocalDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate appliedDate;

    // STATUS IS SAVED AS TEXT ("APPLIED"), NOT AS NUMBER (0, 1, 2...)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    // MANY JOBS BELONG TO ONE USER
    @ManyToOne
    @JoinColumn(name = "id")
    private AppUser appUser;

    public JobApplication() {
    }

    // USER IS SET LATER WITH setAppUser()
    public JobApplication(String company, String position, String location,
            String jobUrl, LocalDate appliedDate, ApplicationStatus status) {
        this.company = company;
        this.position = position;
        this.location = location;
        this.jobUrl = jobUrl;
        this.appliedDate = appliedDate;
        this.status = status;
    }

    // getId() AND setId() ARE USED BY THE HTML PAGES (job.id AND HIDDEN id FIELD)
    public Long getId() {
        return jobApplicationId;
    }

    public void setId(Long jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Long getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    // DOES NOT PRINT appUser, SO IT DOES NOT GO IN A LOOP WITH AppUser
    @Override
    public String toString() {
        return "JobApplication{" +
                "id=" + jobApplicationId +
                ", company='" + company + '\'' +
                ", position='" + position + '\'' +
                ", status=" + status +
                '}';
    }
}