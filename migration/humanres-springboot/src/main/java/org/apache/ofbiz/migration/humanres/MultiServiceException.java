package org.apache.ofbiz.migration.humanres;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class MultiServiceException extends RuntimeException {
    private final HttpStatus status;
    private final List<Map<String, Object>> errors;

    public MultiServiceException(HttpStatus status, List<Map<String, Object>> errors) {
        super("One or more service rows failed");
        this.status = status;
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<Map<String, Object>> getErrors() {
        return errors;
    }
}
