package ai.brave.inventory.domain.product.controller

import ai.brave.inventory.domain.product.model.Product
import ai.brave.inventory.domain.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("products", productService.listAll())
        return "product/list"
    }

    @GetMapping("/new")
    fun createForm(model: Model): String {
        model.addAttribute("product", Product(name = "", sku = "", unit = "pcs"))
        return "product/form"
    }

    @PostMapping
    fun save(@Valid @ModelAttribute product: Product, binding: BindingResult, redirect: RedirectAttributes): String {
        if (binding.hasErrors()) {
            return "product/form"
        }
        productService.create(product)
        redirect.addFlashAttribute("success", "Product created")
        return "redirect:/products"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        val p = productService.findById(id) ?: throw NoSuchElementException("Product not found")
        model.addAttribute("product", p)
        return "product/form"
    }

    @PostMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @ModelAttribute product: Product, binding: BindingResult, redirect: RedirectAttributes): String {
        if (binding.hasErrors()) {
            return "product/form"
        }
        productService.update(id, product)
        redirect.addFlashAttribute("success", "Product updated")
        return "redirect:/products"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long, redirect: RedirectAttributes): String {
        productService.softDelete(id)
        redirect.addFlashAttribute("success", "Product deleted")
        return "redirect:/products"
    }

    @GetMapping("/{id}")
    fun view(@PathVariable id: Long, model: Model): String {
        val product = productService.findById(id) ?: throw NoSuchElementException("Product not found")
        model.addAttribute("product", product)
        return "product/view"
    }
}