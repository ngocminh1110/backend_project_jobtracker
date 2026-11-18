package fi.haagahelia.jobtracker.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

// USER WHO CAN LOG IN TO THE APP
@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    // TWO USERS CANNOT HAVE THE SAME USERNAME
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    // NEVER SAVE THE REAL PASSWORD, ONLY THE BCRYPT HASH
    @Column(name = "password", nullable = false)
    private String passwordHash;

    // "USER" OR "ADMIN", SPRING SECURITY READS THIS AT LOGIN
    @Column(name = "role", nullable = false)
    private String role;

    // EACH USER HAS OWN JOBS
    // IF THE USER IS DELETED, THEIR JOBS ARE DELETED TOO (CascadeType.ALL)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appUser")
    private List<JobApplication> jobs;

    public AppUser() {
    }

    public AppUser(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<JobApplication> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobApplication> jobs) {
        this.jobs = jobs;
    }

    // DOES NOT PRINT PASSWORD OR JOBS, ONLY BASIC INFO
    @Override
    public String toString() {
        return "AppUser{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}