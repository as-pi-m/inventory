package ai.brave.inventory.domain.correction.dto

import jakarta.validation.constraints.NotBlank

data class StockCorrectionDto(
    val id: Long? = null,
    val productId: Long,
    val quantity: Int,

    @field:NotBlank(message = "Reason is required")
    val reason: String
)

