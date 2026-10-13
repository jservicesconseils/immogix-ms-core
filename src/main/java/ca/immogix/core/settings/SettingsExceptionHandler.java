package ca.immogix.core.settings;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduit les erreurs metier des 3 sous-modules de settings (apartment
 * type, property type, property status) en reponses HTTP propres. La
 * validation des DTO (@Valid) est geree par GlobalExceptionHandler.
 */
@RestControllerAdvice
public class SettingsExceptionHandler {

    @ExceptionHandler(SettingsItemNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(SettingsItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(ex.getMessage()));
    }

    private Map<String, Object> errorBody(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("message", message);
        return body;
    }
}
