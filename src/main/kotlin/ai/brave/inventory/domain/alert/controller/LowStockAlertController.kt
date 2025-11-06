package ai.brave.inventory.domain.alert.controller

import ai.brave.inventory.domain.alert.dto.LowStockReportDto
import ai.brave.inventory.domain.alert.service.LowStockAlertService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Controller
@RequestMapping("/alerts")
class LowStockAlertController(
    private val lowStockAlertService: LowStockAlertService
) {
    @GetMapping("/low-stock")
    fun lowStockForm(model: Model): String {
        model.addAttribute("reportDto", LowStockReportDto(threshold = 10))
        return "alert/low-stock"
    }

    @PostMapping("/low-stock")
    fun generateLowStockReport(@ModelAttribute reportDto: LowStockReportDto, model: Model): String {
        val products = lowStockAlertService.getProductsBelowThreshold(reportDto.threshold)
        model.addAttribute("reportDto", reportDto)
        model.addAttribute("products", products)
        model.addAttribute("threshold", reportDto.threshold)
        return "alert/low-stock"
    }

    @GetMapping("/low-stock/export")
    fun exportLowStockCsv(@RequestParam threshold: Int): ResponseEntity<String> {
        val products = lowStockAlertService.getProductsBelowThreshold(threshold)
        val csv = lowStockAlertService.generateCsvReport(products)
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val filename = "low_stock_report_$timestamp.csv"
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv)
    }
}
