package ai.brave.inventory.domain.correction.service

import ai.brave.inventory.domain.correction.dto.StockCorrectionDto
import ai.brave.inventory.domain.correction.model.StockCorrection
import ai.brave.inventory.domain.correction.repository.StockCorrectionRepository
import ai.brave.inventory.domain.product.service.ProductService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockCorrectionServiceImpl(
    private val stockCorrectionRepository: StockCorrectionRepository,
    private val productService: ProductService
) : StockCorrectionService {

    @Transactional
    override fun registerCorrection(correctionDto: StockCorrectionDto): StockCorrection {
        val product = productService.findById(correctionDto.productId)
            ?: throw NoSuchElementException("Product with id ${correctionDto.productId} not found")

        require(correctionDto.reason.isNotBlank()) { "Correction reason must be provided" }

        // Update product quantity (can be positive or negative correction)
        product.quantity += correctionDto.quantity

        val username = SecurityContextHolder.getContext().authentication.name

        val correction = StockCorrection(
            product = product,
            quantity = correctionDto.quantity,
            reason = correctionDto.reason,
            createdBy = username
        )

        return stockCorrectionRepository.save(correction)
    }

    override fun getCorrectionsForProduct(productId: Long): List<StockCorrection> {
        return stockCorrectionRepository.findByProductIdOrderByCorrectionDateDesc(productId)
    }
}

