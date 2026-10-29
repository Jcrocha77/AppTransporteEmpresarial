package com.bancoppel.transporte.api;

import com.bancoppel.transporte.api.dto.ApiError;
import com.bancoppel.transporte.integration.IntegrationPendingException;
import java.time.OffsetDateTime;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IntegrationPendingException.class)
    ResponseEntity<ApiError> handlePendingIntegration(IntegrationPendingException exception) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, "INTEGRATION_PENDING", exception.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ApiError> handleLargeUpload() {
        return error(HttpStatus.PAYLOAD_TOO_LARGE, "UPLOAD_TOO_LARGE", "El tamaño total de los archivos excede el límite permitido.");
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    ResponseEntity<ApiError> handleInvalidRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Revisa los datos enviados y vuelve a intentarlo.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException exception) {
        String code = exception.getStatusCode().toString();
        return error(exception.getStatusCode(), code, exception.getReason() == null ? "La solicitud no es válida." : exception.getReason());
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ApiError(code, message, OffsetDateTime.now()));
    }

    private ResponseEntity<ApiError> error(org.springframework.http.HttpStatusCode status, String code, String message) {
        return ResponseEntity.status(status).body(new ApiError(code, message, OffsetDateTime.now()));
    }
}
