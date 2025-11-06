package ai.brave.inventory.domain.history.controller

import ai.brave.inventory.domain.arrival.service.ProductArrivalService
import ai.brave.inventory.domain.correction.service.StockCorrectionService
import ai.brave.inventory.domain.product.service.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/history")
class HistoryController(
    private val productService: ProductService,
    private val productArrivalService: ProductArrivalService,
    private val stockCorrectionService: StockCorrectionService
) {

    @GetMapping("/{productId}")
    fun viewHistory(@PathVariable productId: Long, model: Model): String {
        val product = productService.findById(productId)
            ?: throw NoSuchElementException("Product not found")

        model.addAttribute("product", product)
        model.addAttribute("arrivals", productArrivalService.getArrivalsForProduct(productId))
        model.addAttribute("corrections", stockCorrectionService.getCorrectionsForProduct(productId))

        return "history/view"
    }
}

