package ai.brave.inventory.domain.correction.controller

import ai.brave.inventory.domain.correction.dto.StockCorrectionDto
import ai.brave.inventory.domain.correction.service.StockCorrectionService
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
class StockCorrectionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var stockCorrectionService: StockCorrectionService

    private lateinit var testProduct: Product

    @BeforeEach
    fun setup() {
        // Create a test product
        testProduct = productService.create(
            Product(
                name = "Test Product for Corrections",
                sku = "TEST-CORR-001",
                description = "Test product for stock corrections",
                unit = "pcs",
                minOrderLevel = 10,
                unitPrice = BigDecimal("99.99"),
                quantity = 50
            )
        )
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should display correction form for product`() {
        mockMvc.perform(get("/corrections/new/{productId}", testProduct.id))
            .andExpect(status().isOk)
            .andExpect(view().name("correction/form"))
            .andExpect(model().attributeExists("correction"))
            .andExpect(model().attributeExists("product"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should register positive correction successfully`() {
        val initialQuantity = testProduct.quantity

        mockMvc.perform(
            post("/corrections")
                .with(csrf())
                .param("productId", testProduct.id.toString())
                .param("quantity", "10")
                .param("reason", "Found missing items in warehouse")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/history/${testProduct.id}"))
            .andExpect(flash().attributeExists("success"))

        // Verify that quantity was updated
        val updatedProduct = productService.findById(testProduct.id!!)
        assert(updatedProduct?.quantity == initialQuantity + 10)
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should register negative correction successfully`() {
        val initialQuantity = testProduct.quantity

        mockMvc.perform(
            post("/corrections")
                .with(csrf())
                .param("productId", testProduct.id.toString())
                .param("quantity", "-5")
                .param("reason", "Items damaged during storage")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/history/${testProduct.id}"))
            .andExpect(flash().attributeExists("success"))

        // Verify that quantity was updated
        val updatedProduct = productService.findById(testProduct.id!!)
        assert(updatedProduct?.quantity == initialQuantity - 5)
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should fail validation with blank reason`() {
        val initialQuantity = testProduct.quantity

        mockMvc.perform(
            post("/corrections")
                .with(csrf())
                .param("productId", testProduct.id.toString())
                .param("quantity", "10")
                .param("reason", "")
        )
            .andExpect(status().isBadRequest)
            .andExpect(view().name("error/400"))
            .andExpect(model().attributeExists("error"))

        // Verify quantity was not changed when validation fails
        val productAfter = productService.findById(testProduct.id!!)
        assert(productAfter?.quantity == initialQuantity)
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should register multiple corrections for same product`() {
        val initialQuantity = testProduct.quantity

        // First correction - positive
        stockCorrectionService.registerCorrection(
            StockCorrectionDto(
                productId = testProduct.id!!,
                quantity = 15,
                reason = "Found extra items"
            )
        )

        // Second correction - negative
        stockCorrectionService.registerCorrection(
            StockCorrectionDto(
                productId = testProduct.id!!,
                quantity = -8,
                reason = "Items expired"
            )
        )

        val updatedProduct = productService.findById(testProduct.id!!)
        assert(updatedProduct?.quantity == initialQuantity + 15 - 8)

        // Verify we have 2 corrections
        val corrections = stockCorrectionService.getCorrectionsForProduct(testProduct.id!!)
        assert(corrections.size == 2)
    }

    @Test
    fun `should redirect to login when not authenticated`() {
        mockMvc.perform(get("/corrections/new/{productId}", testProduct.id))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrlPattern("**/login"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should handle zero quantity correction`() {
        val initialQuantity = testProduct.quantity

        mockMvc.perform(
            post("/corrections")
                .with(csrf())
                .param("productId", testProduct.id.toString())
                .param("quantity", "0")
                .param("reason", "Inventory recount - no change")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/history/${testProduct.id}"))

        // Verify that quantity was not changed
        val updatedProduct = productService.findById(testProduct.id!!)
        assert(updatedProduct?.quantity == initialQuantity)
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should track correction author`() {
        stockCorrectionService.registerCorrection(
            StockCorrectionDto(
                productId = testProduct.id!!,
                quantity = 5,
                reason = "Manual adjustment"
            )
        )

        val corrections = stockCorrectionService.getCorrectionsForProduct(testProduct.id!!)
        assert(corrections.isNotEmpty())
        assert(corrections[0].createdBy == "testuser")
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should list corrections in descending order by date`() {
        // Register corrections with slight delay
        stockCorrectionService.registerCorrection(
            StockCorrectionDto(
                productId = testProduct.id!!,
                quantity = 5,
                reason = "First correction"
            )
        )

        Thread.sleep(10) // Small delay to ensure different timestamps

        stockCorrectionService.registerCorrection(
            StockCorrectionDto(
                productId = testProduct.id!!,
                quantity = 10,
                reason = "Second correction"
            )
        )

        val corrections = stockCorrectionService.getCorrectionsForProduct(testProduct.id!!)
        assert(corrections.size == 2)
        // Most recent should be first
        assert(corrections[0].reason == "Second correction")
        assert(corrections[1].reason == "First correction")
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should allow large positive correction`() {
        val initialQuantity = testProduct.quantity

        mockMvc.perform(
            post("/corrections")
                .with(csrf())
                .param("productId", testProduct.id.toString())
                .param("quantity", "1000")
                .param("reason", "Major stock addition from warehouse transfer")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/history/${testProduct.id}"))

        val updatedProduct = productService.findById(testProduct.id!!)
        assert(updatedProduct?.quantity == initialQuantity + 1000)
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should allow large negative correction`() {
        val initialQuantity = testProduct.quantity

        mockMvc.perform(
            post("/corrections")
                .with(csrf())
                .param("productId", testProduct.id.toString())
                .param("quantity", "-30")
                .param("reason", "Major loss due to quality issues")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/history/${testProduct.id}"))

        val updatedProduct = productService.findById(testProduct.id!!)
        assert(updatedProduct?.quantity == initialQuantity - 30)
    }
}

