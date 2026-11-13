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

@Controller
public class JobApplicationController {

    private final JobApplicationRepository applicationRepository;
    private final AppUserRepository appUserRepository;

    public JobApplicationController(JobApplicationRepository applicationRepository,
            AppUserRepository appUserRepository) {
        this.applicationRepository = applicationRepository;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/applicationlist";
    }

    @GetMapping("/applicationlist")
    public String applicationList(Model model) {
        // FINDS AS WHOM LOGGED INTO NOW
        UserDetails appUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // WHEN IT IS FOUND OUT CAN FIND JOBS OF THAT PERSON
        String username = appUser.getUsername();
        AppUser userNow = appUserRepository.findByUsername(username);
        List<JobApplication> jobApplications = applicationRepository.findByAppUser(userNow);

        model.addAttribute("applications", jobApplications);
        return "applicationlist";
    }

    @GetMapping("/addapplication")
    public String addApplication(Model model) {
        model.addAttribute("application", new JobApplication());
        model.addAttribute("statuses", ApplicationStatus.values());
        return "addapplication";
    }

    @PostMapping("/saveapplication")
    public String saveApplication(@Valid @ModelAttribute("application") JobApplication application,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", ApplicationStatus.values());
            if (application.getId() == null) {
                return "addapplication";
            }
            return "editapplication";
        }

        // THE FORM DOES NOT SEND THE USER, SO WE SET THE LOGGED IN USER HERE
        UserDetails appUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppUser userNow = appUserRepository.findByUsername(appUser.getUsername());
        application.setAppUser(userNow);

        applicationRepository.save(application);
        return "redirect:/applicationlist";
    }

    @GetMapping("/delete/{id}")
    public String deleteApplication(@PathVariable("id") Long applicationId) {
        applicationRepository.deleteById(applicationId);
        return "redirect:/applicationlist";
    }

    @GetMapping("/edit/{id}")
    public String editApplication(@PathVariable("id") Long applicationId, Model model) {
        JobApplication application = applicationRepository.findById(applicationId).orElse(null);
        model.addAttribute("application", application);
        model.addAttribute("statuses", ApplicationStatus.values());
        return "editapplication";
    }
}