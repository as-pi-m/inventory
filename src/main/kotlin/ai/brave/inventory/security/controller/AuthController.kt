package ai.brave.inventory.security.controller

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AuthController {

    @GetMapping("/login")
    fun login(): String {
        if (SecurityContextHolder.getContext().authentication !is AnonymousAuthenticationToken) {
            return "redirect:/"
        }

        return "auth/login"
    }

    @GetMapping
    fun home(): String {
        return "layout"
    }
}