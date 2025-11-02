package ai.brave.inventory.domain.arrival.controller

import ai.brave.inventory.domain.arrival.dto.ProductArrivalDto
import ai.brave.inventory.domain.arrival.service.ProductArrivalService
import ai.brave.inventory.domain.product.model.Product
import ai.brave.inventory.domain.product.service.ProductService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductArrivalControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var productArrivalService: ProductArrivalService

    private lateinit var testProduct: Product

    @BeforeEach
    fun setup() {
        // Create a test product
        testProduct = productService.create(
            Product(
                name = "Test Product",
                sku = "TEST-001",
                description = "Test product for arrivals",
                unit = "pcs",
                minOrderLevel = 10,
                unitPrice = BigDecimal("99.99"),
                quantity = 50
            )
        )
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should display arrival form for product`() {
        mockMvc.perform(get("/arrivals/new/{productId}", testProduct.id))
            .andExpect(status().isOk)
            .andExpect(view().name("arrival/form"))
            .andExpect(model().attributeExists("arrival"))
            .andExpect(model().attributeExists("product"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should register arrival successfully`() {
        val initialQuantity = testProduct.quantity

        mockMvc.perform(
            post("/arrivals")
                .with(csrf())
                .param("productId", testProduct.id.toString())
                .param("quantity", "10")
                .param("source", "Supplier A")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/products"))
            .andExpect(flash().attributeExists("success"))

        // Verify that quantity was updated
        val updatedProduct = productService.findById(testProduct.id!!)
        assert(updatedProduct?.quantity == initialQuantity + 10)
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should fail validation with invalid quantity`() {
        mockMvc.perform(
            post("/arrivals")
                .with(csrf())
                .param("productId", testProduct.id.toString())
                .param("quantity", "0")
                .param("source", "Supplier A")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/products"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should fail validation with blank source`() {
        mockMvc.perform(
            post("/arrivals")
                .with(csrf())
                .param("productId", testProduct.id.toString())
                .param("quantity", "10")
                .param("source", "")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/products"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should list arrivals for product`() {
        // Register some arrivals
        productArrivalService.registerArrival(
            ProductArrivalDto(
                productId = testProduct.id!!,
                quantity = 10,
                source = "Supplier A"
            )
        )
        productArrivalService.registerArrival(
            ProductArrivalDto(
                productId = testProduct.id!!,
                quantity = 20,
                source = "Supplier B"
            )
        )

        mockMvc.perform(get("/arrivals/product/{productId}", testProduct.id))
            .andExpect(status().isOk)
            .andExpect(view().name("arrival/list"))
            .andExpect(model().attributeExists("product"))
            .andExpect(model().attributeExists("arrivals"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should fail validation with negative quantity`() {
        mockMvc.perform(
            post("/arrivals")
                .with(csrf())
                .param("productId", testProduct.id.toString())
                .param("quantity", "-5")
                .param("source", "Supplier A")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/products"))
    }

    @Test
    fun `should redirect to login when not authenticated`() {
        mockMvc.perform(get("/arrivals/new/{productId}", testProduct.id))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrlPattern("**/login"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should register multiple arrivals for same product`() {
        val initialQuantity = testProduct.quantity

        // First arrival
        productArrivalService.registerArrival(
            ProductArrivalDto(
                productId = testProduct.id!!,
                quantity = 15,
                source = "Supplier A"
            )
        )

        // Second arrival
        productArrivalService.registerArrival(
            ProductArrivalDto(
                productId = testProduct.id!!,
                quantity = 25,
                source = "Supplier B"
            )
        )

        val updatedProduct = productService.findById(testProduct.id!!)
        assert(updatedProduct?.quantity == initialQuantity + 15 + 25)

        // Verify we have 2 arrivals
        val arrivals = productArrivalService.getArrivalsForProduct(testProduct.id!!)
        assert(arrivals.size == 2)
    }
}

