package ai.brave.inventory.domain.arrival.service

import ai.brave.inventory.domain.arrival.dto.ProductArrivalDto
import ai.brave.inventory.domain.arrival.model.ProductArrival

interface ProductArrivalService {
    fun registerArrival(arrivalDto: ProductArrivalDto): ProductArrival
    fun getArrivalsForProduct(productId: Long): List<ProductArrival>
}

