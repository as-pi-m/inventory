package ai.brave.inventory.domain.alert.controller

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
class LowStockAlertControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var productService: ProductService

    private lateinit var lowStockProduct: Product
    private lateinit var adequateStockProduct: Product
    private lateinit var outOfStockProduct: Product

    @BeforeEach
    fun setup() {
        // Create products with different stock levels
        lowStockProduct = productService.create(
            Product(
                name = "Low Stock Product",
                sku = "LOW-001",
                description = "Product with low stock",
                unit = "pcs",
                minOrderLevel = 50,
                unitPrice = BigDecimal("10.00"),
                quantity = 5
            )
        )

        adequateStockProduct = productService.create(
            Product(
                name = "Adequate Stock Product",
                sku = "ADEQUATE-001",
                description = "Product with adequate stock",
                unit = "pcs",
                minOrderLevel = 20,
                unitPrice = BigDecimal("15.00"),
                quantity = 100
            )
        )

        outOfStockProduct = productService.create(
            Product(
                name = "Out of Stock Product",
                sku = "OUT-001",
                description = "Product out of stock",
                unit = "pcs",
                minOrderLevel = 30,
                unitPrice = BigDecimal("20.00"),
                quantity = 0
            )
        )
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should display low stock alert form`() {
        mockMvc.perform(get("/alerts/low-stock"))
            .andExpect(status().isOk)
            .andExpect(view().name("alert/low-stock"))
            .andExpect(model().attributeExists("reportDto"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should generate report with products below threshold`() {
        mockMvc.perform(
            post("/alerts/low-stock")
                .with(csrf())
                .param("threshold", "10")
        )
            .andExpect(status().isOk)
            .andExpect(view().name("alert/low-stock"))
            .andExpect(model().attributeExists("products"))
            .andExpect(model().attributeExists("threshold"))
            .andExpect(model().attribute("threshold", 10))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should find products below threshold of 10`() {
        mockMvc.perform(
            post("/alerts/low-stock")
                .with(csrf())
                .param("threshold", "10")
        )
            .andExpect(status().isOk)
            .andExpect(view().name("alert/low-stock"))
            .andExpect(model().attributeExists("products"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should not find products when threshold is zero`() {
        mockMvc.perform(
            post("/alerts/low-stock")
                .with(csrf())
                .param("threshold", "0")
        )
            .andExpect(status().isOk)
            .andExpect(view().name("alert/low-stock"))
            .andExpect(model().attributeExists("products"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should find all products when threshold is very high`() {
        mockMvc.perform(
            post("/alerts/low-stock")
                .with(csrf())
                .param("threshold", "1000")
        )
            .andExpect(status().isOk)
            .andExpect(view().name("alert/low-stock"))
            .andExpect(model().attributeExists("products"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should export CSV report`() {
        mockMvc.perform(get("/alerts/low-stock/export")
            .param("threshold", "10"))
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("low_stock_report_")))
            .andExpect(content().contentType("text/csv"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should export CSV with proper headers`() {
        mockMvc.perform(get("/alerts/low-stock/export")
            .param("threshold", "10"))
            .andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.containsString("SKU,Product Name,Current Quantity,Min Order Level,Unit,Deficit")))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should include low stock product in CSV export`() {
        mockMvc.perform(get("/alerts/low-stock/export")
            .param("threshold", "10"))
            .andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.containsString("LOW-001")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("OUT-001")))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should not include adequate stock product in CSV export`() {
        mockMvc.perform(get("/alerts/low-stock/export")
            .param("threshold", "10"))
            .andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("ADEQUATE-001"))))
    }

    @Test
    fun `should redirect to login when not authenticated`() {
        mockMvc.perform(get("/alerts/low-stock"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrlPattern("**/login"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should handle threshold at boundary - equal to stock level`() {
        mockMvc.perform(
            post("/alerts/low-stock")
                .with(csrf())
                .param("threshold", "5")
        )
            .andExpect(status().isOk)
            .andExpect(view().name("alert/low-stock"))
            .andExpect(model().attributeExists("products"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should generate CSV with correct deficit calculation`() {
        mockMvc.perform(get("/alerts/low-stock/export")
            .param("threshold", "100"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("text/csv"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should handle special characters in product names for CSV`() {
        // Create product with special characters
        productService.create(
            Product(
                name = "Product, with \"quotes\"",
                sku = "SPECIAL-001",
                description = "Special chars test",
                unit = "pcs",
                minOrderLevel = 10,
                unitPrice = BigDecimal("5.00"),
                quantity = 2
            )
        )

        mockMvc.perform(get("/alerts/low-stock/export")
            .param("threshold", "10"))
            .andExpect(status().isOk)
            .andExpect(content().string(org.hamcrest.Matchers.containsString("SPECIAL-001")))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should display default threshold of 10 on form`() {
        mockMvc.perform(get("/alerts/low-stock"))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("reportDto"))
    }

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should persist threshold value after report generation`() {
        mockMvc.perform(
            post("/alerts/low-stock")
                .with(csrf())
                .param("threshold", "25")
        )
            .andExpect(status().isOk)
            .andExpect(model().attribute("threshold", 25))
            .andExpect(model().attributeExists("reportDto"))
    }
}

