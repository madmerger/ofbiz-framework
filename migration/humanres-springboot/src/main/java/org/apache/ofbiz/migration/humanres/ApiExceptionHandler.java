package org.apache.ofbiz.migration.humanres;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MultiServiceException.class)
    public ResponseEntity<Map<String, Object>> handleMultiServiceException(MultiServiceException exception) {
        Map<String, Object> body = error(exception.getMessage());
        body.put("errors", exception.getErrors());
        return ResponseEntity.status(exception.getStatus()).body(body);
    }

    @ExceptionHandler(HumanResException.class)
    public ResponseEntity<Map<String, Object>> handleHumanResException(HumanResException exception) {
        return ResponseEntity.status(exception.getStatus()).body(error(exception.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error("ServiceValueFound"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");
        return ResponseEntity.badRequest().body(error(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error(exception.getMessage() == null ? "Internal server error" : exception.getMessage()));
    }

    private static Map<String, Object> error(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("responseMessage", "error");
        body.put("errorMessage", message);
        return body;
    }
}
