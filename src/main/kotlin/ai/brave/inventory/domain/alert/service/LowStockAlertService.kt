package ai.brave.inventory.domain.alert.service

import ai.brave.inventory.domain.alert.dto.LowStockProductDto

interface LowStockAlertService {
    fun getProductsBelowThreshold(threshold: Int): List<LowStockProductDto>
    fun generateCsvReport(products: List<LowStockProductDto>): String
}
