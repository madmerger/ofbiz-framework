package org.apache.ofbiz.migration.humanres;

import org.springframework.http.HttpStatus;

public class HumanResException extends RuntimeException {
    private final HttpStatus status;

    public HumanResException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static HumanResException badRequest(String message) {
        return new HumanResException(HttpStatus.BAD_REQUEST, message);
    }

    public static HumanResException notFound(String message) {
        return new HumanResException(HttpStatus.NOT_FOUND, message);
    }

    public static HumanResException conflict(String message) {
        return new HumanResException(HttpStatus.CONFLICT, message);
    }
}
