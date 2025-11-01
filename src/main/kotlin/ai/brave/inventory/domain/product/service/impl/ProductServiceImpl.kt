package ai.brave.inventory.domain.product.service.impl

import ai.brave.inventory.domain.product.model.Product
import ai.brave.inventory.domain.product.repository.ProductRepository
import ai.brave.inventory.domain.product.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository
) : ProductService {

    override fun listAll(): List<Product> = productRepository.findAllByDeletedFalse()

    override fun findById(id: Long): Product? =
        productRepository.findById(id).filter { !it.deleted }.orElse(null)

    @Transactional
    override fun create(product: Product): Product {
        // simple SKU uniqueness check
        productRepository.findBySku(product.sku).ifPresent {
            throw IllegalArgumentException("SKU already exists")
        }
        return productRepository.save(product)
    }

    @Transactional
    override fun update(id: Long, updated: Product): Product {
        val existing = productRepository.findById(id).orElseThrow { NoSuchElementException("Product not found") }
        val merged = existing.copy(
            name = updated.name,
            sku = updated.sku,
            description = updated.description,
            unit = updated.unit,
            minOrderLevel = updated.minOrderLevel,
            unitPrice = updated.unitPrice,
            deleted = existing.deleted
        )
        return productRepository.save(merged)
    }

    @Transactional
    override fun softDelete(id: Long) {
        val existing = productRepository.findById(id).orElseThrow { NoSuchElementException("Product not found") }
        val marked = existing.copy(deleted = true)
        productRepository.save(marked)
    }
}