package ai.brave.inventory.domain.correction.controller

import ai.brave.inventory.domain.correction.dto.StockCorrectionDto
import ai.brave.inventory.domain.correction.service.StockCorrectionService
import ai.brave.inventory.domain.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/corrections")
class StockCorrectionController(
    private val stockCorrectionService: StockCorrectionService,
    private val productService: ProductService
) {

    @GetMapping("/new/{productId}")
    fun correctionForm(@PathVariable productId: Long, model: Model): String {
        model.addAttribute("correction", StockCorrectionDto(productId = productId, quantity = 0, reason = ""))
        model.addAttribute("product", productService.findById(productId))
        return "correction/form"
    }

    @PostMapping
    fun registerCorrection(
        @Valid @ModelAttribute("correction") correctionDto: StockCorrectionDto,
        binding: BindingResult,
        redirect: RedirectAttributes,
        model: Model
    ): String {
        if (binding.hasErrors()) {
            model.addAttribute("product", productService.findById(correctionDto.productId))
            return "correction/form"
        }
        stockCorrectionService.registerCorrection(correctionDto)
        redirect.addFlashAttribute("success", "Stock correction registered successfully")
        return "redirect:/history/${correctionDto.productId}"
    }
}

