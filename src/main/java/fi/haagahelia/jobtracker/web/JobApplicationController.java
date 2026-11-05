package fi.haagahelia.jobtracker.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import fi.haagahelia.jobtracker.domain.ApplicationStatus;
import fi.haagahelia.jobtracker.domain.JobApplication;
import fi.haagahelia.jobtracker.domain.JobApplicationRepository;

@Controller
public class JobApplicationController {

    private final JobApplicationRepository applicationRepository;

    // Constructor injection
    public JobApplicationController(JobApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/applicationlist";
    }

    @GetMapping("/applicationlist")
    public String applicationList(Model model) {
        model.addAttribute("applications", applicationRepository.findAll());
        return "applicationlist";
    }

    @GetMapping("/addapplication")
    public String addApplication(Model model) {
        model.addAttribute("application", new JobApplication());
        model.addAttribute("statuses", ApplicationStatus.values());
        return "addapplication";
    }

    @PostMapping("/saveapplication")
    public String saveApplication(JobApplication application) {
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