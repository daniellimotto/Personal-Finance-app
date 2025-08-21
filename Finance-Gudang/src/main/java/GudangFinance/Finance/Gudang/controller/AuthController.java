package GudangFinance.Finance.Gudang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import GudangFinance.Finance.Gudang.model.AppUser;
import GudangFinance.Finance.Gudang.repository.UserRepository;
import GudangFinance.Finance.Gudang.service.DashboardService;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DashboardService dashboardService;

    @Value("${app.admin.secret:}")
    private String adminSecret;

    @Value("${app.user.secret:}")
    private String userSecret;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute AppUser user,
                                 @org.springframework.web.bind.annotation.RequestParam(name = "desiredRole", required = false) String desiredRole,
                                 @org.springframework.web.bind.annotation.RequestParam(name = "registrationSecret", required = false) String providedSecret,
                                 Model model) {
        String roleToAssign = "USER";
        if ("ADMIN".equalsIgnoreCase(desiredRole)) {
            if (adminSecret == null || adminSecret.isBlank()) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Admin registration is disabled: no admin secret configured.");
                return "auth/register";
            }
            if (!adminSecret.equals(providedSecret)) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Invalid admin secret.");
                return "auth/register";
            }
            roleToAssign = "ADMIN";
        } else {
            // USER registration path requires user secret
            if (userSecret == null || userSecret.isBlank()) {
                model.addAttribute("user", user);
                model.addAttribute("error", "User registration is disabled: no user secret configured.");
                return "auth/register";
            }
            if (!userSecret.equals(providedSecret)) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Invalid user secret.");
                return "auth/register";
            }
        }

        user.setRole(roleToAssign);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Add dashboard totals for admin users
        model.addAttribute("dashboardTotals", dashboardService.getDashboardTotals());
        return "home/dashboard";
    }
}
