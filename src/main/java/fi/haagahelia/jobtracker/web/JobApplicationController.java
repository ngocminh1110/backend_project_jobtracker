package fi.haagahelia.jobtracker.web;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import fi.haagahelia.jobtracker.domain.AppUser;
import fi.haagahelia.jobtracker.domain.AppUserRepository;
import fi.haagahelia.jobtracker.domain.ApplicationStatus;
import fi.haagahelia.jobtracker.domain.JobApplication;
import fi.haagahelia.jobtracker.domain.JobApplicationRepository;
import jakarta.validation.Valid;

// HANDLES THE PAGES FOR JOB APPLICATIONS: LIST, ADD, EDIT, DELETE
@Controller
public class JobApplicationController {

    private final JobApplicationRepository applicationRepository;
    private final AppUserRepository appUserRepository;

    // CONSTRUCTOR INJECTION: SPRING GIVES US BOTH REPOSITORIES
    public JobApplicationController(JobApplicationRepository applicationRepository,
            AppUserRepository appUserRepository) {
        this.applicationRepository = applicationRepository;
        this.appUserRepository = appUserRepository;
    }

    // FINDS AS WHOM LOGGED INTO NOW
    private AppUser getUserNow() {
        UserDetails appUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return appUserRepository.findByUsername(appUser.getUsername());
    }

    // TRUE ONLY IF THIS JOB BELONGS TO THE LOGGED IN USER
    private boolean isOwnJob(JobApplication job) {
        return job != null
                && job.getAppUser() != null
                && job.getAppUser().getId().equals(getUserNow().getId());
    }

    // HOME PAGE GOES STRAIGHT TO THE JOB LIST
    @GetMapping("/")
    public String home() {
        return "redirect:/applicationlist";
    }

    // SHOWS ONLY THE JOBS OF THE LOGGED IN USER
    @GetMapping("/applicationlist")
    public String applicationList(Model model) {
        // WHEN IT IS FOUND OUT WHO IS LOGGED IN, CAN FIND JOBS OF THAT PERSON
        AppUser userNow = getUserNow();
        List<JobApplication> jobApplications = applicationRepository.findByAppUser(userNow);

        model.addAttribute("applications", jobApplications);
        return "applicationlist";
    }

    // SHOWS AN EMPTY FORM FOR A NEW JOB
    @GetMapping("/addapplication")
    public String addApplication(Model model) {
        model.addAttribute("application", new JobApplication());
        // ALL ENUM VALUES FOR THE STATUS DROPDOWN
        model.addAttribute("statuses", ApplicationStatus.values());
        return "addapplication";
    }

    // SAVES A NEW JOB OR AN EDITED JOB (BOTH FORMS USE THIS)
    @PostMapping("/saveapplication")
    public String saveApplication(@Valid @ModelAttribute("application") JobApplication application,
            BindingResult bindingResult, Model model) {

        // FORM HAS ERRORS: SHOW THE SAME FORM AGAIN WITH RED MESSAGES
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", ApplicationStatus.values());
            // NO ID = NEW JOB, SO BACK TO ADD FORM. OTHERWISE BACK TO EDIT FORM
            if (application.getId() == null) {
                return "addapplication";
            }
            return "editapplication";
        }

        // WHEN EDITING, THE OLD JOB MUST BELONG TO THE LOGGED IN USER
        if (application.getId() != null) {
            JobApplication oldJob = applicationRepository.findById(application.getId()).orElse(null);
            if (!isOwnJob(oldJob)) {
                return "redirect:/applicationlist";
            }
        }

        // THE FORM DOES NOT SEND THE USER, SO WE SET THE LOGGED IN USER HERE
        application.setAppUser(getUserNow());

        // save() ADDS A NEW ROW IF ID IS EMPTY, OR UPDATES THE OLD ROW IF ID EXISTS
        applicationRepository.save(application);
        return "redirect:/applicationlist";
    }

    // DELETES ONE JOB, BUT ONLY IF IT BELONGS TO THE LOGGED IN USER
    @GetMapping("/delete/{id}")
    public String deleteApplication(@PathVariable("id") Long applicationId) {
        JobApplication job = applicationRepository.findById(applicationId).orElse(null);
        if (isOwnJob(job)) {
            applicationRepository.deleteById(applicationId);
        }
        return "redirect:/applicationlist";
    }

    // SHOWS THE EDIT FORM, BUT ONLY FOR OWN JOBS
    @GetMapping("/edit/{id}")
    public String editApplication(@PathVariable("id") Long applicationId, Model model) {
        JobApplication application = applicationRepository.findById(applicationId).orElse(null);
        // SOMEONE ELSE'S JOB (OR WRONG ID): GO BACK TO OWN LIST
        if (!isOwnJob(application)) {
            return "redirect:/applicationlist";
        }
        model.addAttribute("application", application);
        model.addAttribute("statuses", ApplicationStatus.values());
        return "editapplication";
    }
}