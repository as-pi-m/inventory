package ai.brave.inventory.domain.product.model

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

@Entity
@Table(name = "products", indexes = [Index(columnList = "sku", unique = true)])
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotBlank
    @Column(nullable = false)
    val name: String,

    @field:NotBlank
    @Column(nullable = false, unique = true)
    val sku: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @field:NotBlank
    @Column(nullable = false)
    val unit: String,

    @field:Min(0)
    val minOrderLevel: Int = 0,

    @Column(precision = 19, scale = 4)
    val unitPrice: BigDecimal? = null,

    @Column(nullable = false)
    val deleted: Boolean = false,

    @field:Min(0)
    @Column(nullable = false)
    var quantity: Int = 0
)
