package ai.brave.inventory.security.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ProfileController {
    @GetMapping("/profile")
    fun userProfile(model: Model, @AuthenticationPrincipal userDetails: UserDetails): String {
        model.addAttribute("username", userDetails.getUsername())
        model.addAttribute("authorities", userDetails.getAuthorities())
        return "auth/profile"
    }
}