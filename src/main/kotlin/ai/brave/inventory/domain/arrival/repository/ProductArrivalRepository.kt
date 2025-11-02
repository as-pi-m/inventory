package ai.brave.inventory.domain.arrival.repository

import ai.brave.inventory.domain.arrival.model.ProductArrival
import org.springframework.data.jpa.repository.JpaRepository

interface ProductArrivalRepository : JpaRepository<ProductArrival, Long> {
    fun findByProductId(productId: Long): List<ProductArrival>
}
