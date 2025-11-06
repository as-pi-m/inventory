package ai.brave.inventory.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBindException(ex: BindException, model: Model): String {
        logger.warn("Validation error occurred: ${ex.message}")

        model.addAttribute("errors", ex.bindingResult.allErrors)
        model.addAttribute("error", "Validation failed. Please check your input.")

        return "error/validation"
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, model: Model): String {
        logger.warn("Method argument validation error: ${ex.message}")

        model.addAttribute("errors", ex.bindingResult.allErrors)
        model.addAttribute("error", "Validation failed. Please check your input.")

        return "error/validation"
    }

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: NoSuchElementException, model: Model): String {
        logger.warn("Resource not found: ${ex.message}")

        model.addAttribute("error", ex.message ?: "Resource not found")
        model.addAttribute("status", 404)

        return "error/404"
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgument(ex: IllegalArgumentException, model: Model): String {
        logger.warn("Illegal argument: ${ex.message}")

        model.addAttribute("error", ex.message ?: "Invalid request")
        model.addAttribute("status", 400)

        return "error/400"
    }

    @ExceptionHandler(IllegalStateException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleIllegalState(ex: IllegalStateException, redirectAttributes: RedirectAttributes): String {
        logger.warn("Illegal state: ${ex.message}")

        redirectAttributes.addFlashAttribute("error", ex.message ?: "Operation cannot be completed")

        return "redirect:/products"
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception, model: Model): String {
        logger.error("Unexpected error occurred", ex)

        model.addAttribute("error", "An unexpected error occurred. Please try again later.")
        model.addAttribute("status", 500)
        model.addAttribute("message", ex.message)

        return "error/500"
    }
}

