package ai.brave.inventory.domain.arrival.controller

import ai.brave.inventory.domain.arrival.dto.ProductArrivalDto
import ai.brave.inventory.domain.arrival.service.ProductArrivalService
import ai.brave.inventory.domain.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/arrivals")
class ProductArrivalController(
    private val productArrivalService: ProductArrivalService,
    private val productService: ProductService
) {

    @GetMapping("/new/{productId}")
    fun arrivalForm(@PathVariable productId: Long, model: Model): String {
        model.addAttribute("arrival", ProductArrivalDto(productId = 1, quantity = 1, source = ""))
        model.addAttribute("product", productService.findById(productId))
        return "arrival/form"
    }

    @PostMapping
    fun registerArrival(@Valid @ModelAttribute("arrival") arrivalDto: ProductArrivalDto, binding: BindingResult, redirect: RedirectAttributes, model: Model): String {
        if (binding.hasErrors()) {
            model.addAttribute("product", productService.findById(arrivalDto.productId))
            return "arrival/form"
        }
        productArrivalService.registerArrival(arrivalDto)
        redirect.addFlashAttribute("success", "Arrival registered successfully")
        return "redirect:/history/${arrivalDto.productId}"
    }

    @GetMapping("/product/{productId}")
    fun listArrivalsForProduct(@PathVariable productId: Long, model: Model): String {
        model.addAttribute("product", productService.findById(productId))
        model.addAttribute("arrivals", productArrivalService.getArrivalsForProduct(productId))
        return "arrival/list"
    }
}
