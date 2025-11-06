package ai.brave.inventory.domain.correction.model

import ai.brave.inventory.domain.product.model.Product
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.ZonedDateTime

@Entity
@Table(name = "stock_corrections")
data class StockCorrection(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(nullable = false)
    val quantity: Int, // Can be positive or negative

    @field:NotBlank
    @Column(nullable = false, length = 500)
    val reason: String,

    @Column(nullable = false)
    val correctionDate: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false)
    val createdBy: String // username
)

