package ai.brave.inventory.domain.correction.repository

import ai.brave.inventory.domain.correction.model.StockCorrection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockCorrectionRepository : JpaRepository<StockCorrection, Long> {
    fun findByProductIdOrderByCorrectionDateDesc(productId: Long): List<StockCorrection>
}

