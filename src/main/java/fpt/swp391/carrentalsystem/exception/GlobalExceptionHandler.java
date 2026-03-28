package fpt.swp391.carrentalsystem.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());

        if (isApiRequest(request)) {
            Map<String, Object> error = createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        ModelAndView mav = new ModelAndView("error/error");
        mav.addObject("status", 404);
        mav.addObject("error", "Không tìm thấy");
        mav.addObject("message", ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    /**
     * Handle generic RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);

        if (isApiRequest(request)) {
            Map<String, Object> error = createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        ModelAndView mav = new ModelAndView("error/error");
        mav.addObject("status", 400);
        mav.addObject("error", "Lỗi xử lý");
        mav.addObject("message", ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        if (isApiRequest(request)) {
            Map<String, Object> error = createErrorResponse(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ", request.getRequestURI());
            error.put("fieldErrors", fieldErrors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        ModelAndView mav = new ModelAndView("error/error");
        mav.addObject("status", 400);
        mav.addObject("error", "Dữ liệu không hợp lệ");
        mav.addObject("message", "Vui lòng kiểm tra lại thông tin đã nhập");
        mav.addObject("fieldErrors", fieldErrors);
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    /**
     * Handle BindException (form binding errors)
     */
    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindException ex, HttpServletRequest request) {
        log.warn("Bind error: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        if (isApiRequest(request)) {
            Map<String, Object> error = createErrorResponse(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ", request.getRequestURI());
            error.put("fieldErrors", fieldErrors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        ModelAndView mav = new ModelAndView("error/error");
        mav.addObject("status", 400);
        mav.addObject("error", "Dữ liệu không hợp lệ");
        mav.addObject("message", "Vui lòng kiểm tra lại thông tin đã nhập");
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        if (isApiRequest(request)) {
            Map<String, Object> error = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        ModelAndView mav = new ModelAndView("error/error");
        mav.addObject("status", 500);
        mav.addObject("error", "Lỗi hệ thống");
        mav.addObject("message", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    /**
     * Check if request is an API request (expects JSON response)
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        String contentType = request.getContentType();

        return uri.contains("/api/")
            || uri.contains("/webhook")
            || (accept != null && accept.contains("application/json"))
            || (contentType != null && contentType.contains("application/json"));
    }

    /**
     * Create standard error response map
     */
    private Map<String, Object> createErrorResponse(HttpStatus status, String message, String path) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        error.put("path", path);
        return error;
    }
}


