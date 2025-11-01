package ai.brave.inventory.domain.product.repository

import ai.brave.inventory.domain.product.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findBySku(sku: String): Optional<Product>
    fun findAllByDeletedFalse(): List<Product>
}