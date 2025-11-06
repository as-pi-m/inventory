package ai.brave.inventory.domain.alert.service

import ai.brave.inventory.domain.alert.dto.LowStockProductDto
import ai.brave.inventory.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service
import java.io.StringWriter

@Service
class LowStockAlertServiceImpl(
    private val productRepository: ProductRepository
) : LowStockAlertService {

    override fun getProductsBelowThreshold(threshold: Int): List<LowStockProductDto> {
        return productRepository.findAll()
            .filter { !it.deleted && it.quantity < threshold }
            .map { product ->
                LowStockProductDto(
                    id = product.id,
                    name = product.name,
                    sku = product.sku,
                    currentQuantity = product.quantity,
                    minOrderLevel = product.minOrderLevel,
                    unit = product.unit
                )
            }
            .sortedBy { it.currentQuantity }
    }

    override fun generateCsvReport(products: List<LowStockProductDto>): String {
        val writer = StringWriter()
        // CSV Header
        writer.append("SKU,Product Name,Current Quantity,Min Order Level,Unit,Deficit\n")
        // CSV Data
        products.forEach { product ->
            val deficit = product.minOrderLevel - product.currentQuantity
            writer.append("${escapeCsv(product.sku)},")
            writer.append("${escapeCsv(product.name)},")
            writer.append("${product.currentQuantity},")
            writer.append("${product.minOrderLevel},")
            writer.append("${escapeCsv(product.unit)},")
            writer.append("$deficit\n")
        }
        return writer.toString()
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
