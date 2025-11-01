package ai.brave.inventory.domain.product.service

import ai.brave.inventory.domain.product.model.Product

interface ProductService {
    fun listAll(): List<Product>
    fun findById(id: Long): Product?
    fun create(product: Product): Product
    fun update(id: Long, updated: Product): Product
    fun softDelete(id: Long)
}