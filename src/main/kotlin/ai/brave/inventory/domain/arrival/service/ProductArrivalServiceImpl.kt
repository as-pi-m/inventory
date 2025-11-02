package ai.brave.inventory.domain.arrival.service

import ai.brave.inventory.domain.arrival.dto.ProductArrivalDto
import ai.brave.inventory.domain.arrival.model.ProductArrival
import ai.brave.inventory.domain.arrival.repository.ProductArrivalRepository
import ai.brave.inventory.domain.product.service.ProductService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductArrivalServiceImpl(
    private val productArrivalRepository: ProductArrivalRepository,
    private val productService: ProductService
) : ProductArrivalService {

    @Transactional
    override fun registerArrival(arrivalDto: ProductArrivalDto): ProductArrival {
        val product = productService.findById(arrivalDto.productId)
            ?: throw NoSuchElementException("Product with id ${arrivalDto.productId} not found")

        product.quantity += arrivalDto.quantity

        val username = SecurityContextHolder.getContext().authentication.name

        val arrival = ProductArrival(
            product = product,
            quantity = arrivalDto.quantity,
            source = arrivalDto.source,
            createdBy = username
        )

        return productArrivalRepository.save(arrival)
    }

    override fun getArrivalsForProduct(productId: Long): List<ProductArrival> {
        return productArrivalRepository.findByProductId(productId)
    }
}

