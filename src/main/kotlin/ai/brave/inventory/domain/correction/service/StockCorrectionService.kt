package ai.brave.inventory.domain.correction.service

import ai.brave.inventory.domain.correction.dto.StockCorrectionDto
import ai.brave.inventory.domain.correction.model.StockCorrection

interface StockCorrectionService {
    fun registerCorrection(correctionDto: StockCorrectionDto): StockCorrection
    fun getCorrectionsForProduct(productId: Long): List<StockCorrection>
}

