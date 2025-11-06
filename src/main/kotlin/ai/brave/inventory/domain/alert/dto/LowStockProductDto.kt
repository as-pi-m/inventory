package ai.brave.inventory.domain.alert.dto

data class LowStockProductDto(
    val id: Long?,
    val name: String,
    val sku: String,
    val currentQuantity: Int,
    val minOrderLevel: Int,
    val unit: String
)
