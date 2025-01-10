package com.reliaquest.api.exception.handler;

import com.reliaquest.api.utility.StringConstants;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<?> handleException(Throwable ex) {
        log.error("Error handling web request.", ex);
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler
    protected ResponseEntity<String> handleException(HttpStatusCodeException ex) {
        log.error("HttpStatusCodeException handling web request.", ex);
        return ResponseEntity.status(ex.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(ex.getResponseBodyAsString());
    }
    @ExceptionHandler
    protected ResponseEntity<String> handleException(HttpClientErrorException.TooManyRequests ex) {
        log.error("HttpStatusCodeException handling web request.", ex);
        return ResponseEntity.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(getErrorResponse(StringConstants.SERVER_OVERLOADED));
    }

    private String getErrorResponse(String msg) {
        Map<String,String> response = new HashMap<>();
        response.put("status",StringConstants.REQUEST_FAILED);
        response.put("error",msg);
        return response.toString();
    }


}
