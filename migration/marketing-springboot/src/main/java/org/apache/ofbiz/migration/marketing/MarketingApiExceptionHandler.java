package org.apache.ofbiz.migration.marketing;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Reports a failed multi-service call as 409 Conflict after the whole transaction has been rolled back.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MarketingApiExceptionHandler {

    @ExceptionHandler(MigrationServiceDispatcher.MultiServiceException.class)
    public ResponseEntity<Map<String, Object>> multiError(MigrationServiceDispatcher.MultiServiceException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("responseMessage", "error");
        body.put("errorMessage", exception.getMessage());
        body.put("rolledBack", true);
        body.put("errors", exception.getErrors());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
