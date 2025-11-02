package ai.brave.inventory.domain.arrival.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class ProductArrivalDto(
    @field:Min(1)
    val productId: Long,

    @field:Min(1)
    val quantity: Int,

    @field:NotBlank
    val source: String
)
