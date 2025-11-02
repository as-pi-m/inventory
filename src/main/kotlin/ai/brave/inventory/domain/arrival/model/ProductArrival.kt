package ai.brave.inventory.domain.arrival.model

import ai.brave.inventory.domain.product.model.Product
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.time.ZonedDateTime

@Entity
@Table(name = "product_arrivals")
data class ProductArrival(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @field:Min(1)
    @Column(nullable = false)
    val quantity: Int = 1,

    @field:NotBlank
    @Column(nullable = false)
    val source: String, // e.g., supplier name

    @Column(nullable = false)
    val arrivalDate: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false)
    val createdBy: String // username
)