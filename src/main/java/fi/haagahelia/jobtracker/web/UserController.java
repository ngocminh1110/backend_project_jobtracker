package fi.haagahelia.jobtracker.web;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import fi.haagahelia.jobtracker.domain.AppUser;
import fi.haagahelia.jobtracker.domain.AppUserRepository;
import fi.haagahelia.jobtracker.domain.SignupForm;
import jakarta.validation.Valid;

// HANDLES LOGIN PAGE AND SIGN UP FOR NEW USERS
@Controller
public class UserController {

    private final AppUserRepository appUserRepository;

    // CONSTRUCTOR INJECTION: SPRING GIVES US THE REPOSITORY
    public UserController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    // SHOWS OUR OWN LOGIN PAGE, NOT THE DEFAULT ONE
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // SHOWS AN EMPTY SIGN UP FORM
    @GetMapping("/signup")
    public String addUser(Model model) {
        model.addAttribute("signupform", new SignupForm());
        return "signup";
    }

    // CREATES A NEW USER FROM THE SIGN UP FORM
    @PostMapping("/saveuser")
    public String saveUser(@Valid @ModelAttribute("signupform") SignupForm signupForm,
            BindingResult bindingResult) {

        // EMPTY OR TOO SHORT FIELDS: SHOW THE FORM AGAIN WITH ERRORS
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        // PASSWORD CHECK MUST BE THE SAME AS PASSWORD
        if (!signupForm.getPassword().equals(signupForm.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "err.passCheck", "Passwords do not match");
            return "signup";
        }

        // USERNAME MUST BE FREE
        if (appUserRepository.findByUsername(signupForm.getUsername()) != null) {
            bindingResult.rejectValue("username", "err.username", "Username already exists");
            return "signup";
        }

        // SAVE ONLY THE BCRYPT HASH OF THE PASSWORD
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashPassword = passwordEncoder.encode(signupForm.getPassword());

        // NEW USER ALWAYS GETS ROLE "USER", NEVER TAKEN FROM THE FORM
        AppUser newUser = new AppUser(signupForm.getUsername(), hashPassword, "USER");
        appUserRepository.save(newUser);

        // AFTER SIGN UP GO TO LOGIN PAGE, "?registered" SHOWS THE GREEN MESSAGE
        return "redirect:/login?registered";
    }
}