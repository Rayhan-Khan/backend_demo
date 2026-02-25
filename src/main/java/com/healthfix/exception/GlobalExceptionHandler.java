package com.healthfix.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.healthfix.utils.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler {

    public Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<JSONObject> handleNotFoundExceptions(Exception ex, WebRequest request) {
        logger.error("ResourceNotFoundException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ResponseBuilder.error(null, (ex.getMessage())).getJson(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomMessagePresentException.class)
    public ResponseEntity<JSONObject> handleCustomMessagePresentExceptions(Exception ex, WebRequest request) {
        logger.error("CustomMessagePresentException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ResponseBuilder.error(null, (ex.getMessage())).getJson(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<JSONObject> handleRuntimeExceptions(Exception ex, WebRequest request) {
        logger.error("RuntimeException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ResponseBuilder.error(null, (ex.getMessage())).getJson(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<JSONObject> handleIllegalArgumentExceptions(Exception ex, WebRequest request) {
        logger.error("IllegalArgumentException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ResponseBuilder.error(null, (ex.getMessage())).getJson(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<JSONObject> handleJsonParseException(JsonParseException exc) {
        logger.error("JsonParseException: {}", exc.getMessage(), exc);
        return new ResponseEntity<>(ResponseBuilder.error(null, exc.getMessage()).getJson(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JSONObject> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            logger.error("Validation error: {} - {}", error.getField(), error.getDefaultMessage());
            errors.put(error.getField(), error.getDefaultMessage());
        }

        String firstErrorMessage = errors.values().stream().findFirst().orElse("Validation error");
        logger.error("MethodArgumentNotValidException: {}", firstErrorMessage, ex);
        return new ResponseEntity<>(ResponseBuilder.error(null, firstErrorMessage).getJson(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<JSONObject> handleValidationException(HandlerMethodValidationException ex) {
        return new ResponseEntity<>(ResponseBuilder.error(null, "Invalid email format").getJson(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<JSONObject> handleEmailNotVerifiedException(EmailNotVerifiedException exc) {
        logger.error("EmailNotVerifiedException: {}", exc.getMessage(), exc);
        return new ResponseEntity<>(ResponseBuilder.error(null, exc.getMessage()).getJson(), HttpStatus.FORBIDDEN);
    }
}
