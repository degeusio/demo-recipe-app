package io.degeus.recipeappapi.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

/**
 * Central place of handling exceptions thrown by the application. This is an excellent point to map exceptions to user-friendly
 * data objects being passed as part of the return payload. Here, we only use 'null' as payload.
 * This could e.g. also use parameterized messages using an {@link org.springframework.context.support.ResourceBundleMessageSource}
 */
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { AccessDeniedException.class })
    protected ResponseEntity<Object> handleConflict(org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        log.info("Access Denied Ex, to be handled by SF. Error [{}]", ex.getMessage());
        throw ex; //let it handle by SF.
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.debug("ENFE. Returning 404 (bad request). Error [{}]", ex.getMessage());
        HttpStatus mappedStatus = HttpStatus.NOT_FOUND;
        return handleExceptionInternal(ex, null, new HttpHeaders(), mappedStatus, request);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class})
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {

        log.info("Illegal argument exception encountered. Error: [{}]. Returning 400 (bad request).", ex.getMessage());
        HttpStatus mappedStatus = HttpStatus.BAD_REQUEST;
        return handleExceptionInternal(ex, null, new HttpHeaders(), mappedStatus, request);
    }

    @ExceptionHandler(value = { IOException.class})
    protected ResponseEntity<Object> handleIOException(IOException ex, WebRequest request) {
        log.debug("IOException encountered. Error: [{}]. Returning 500 (internal error).", ex.getMessage(), ex);
        log.info("IOException encountered. Error: [{}]. Returning 500 (internal error).", ex.getMessage());
        return doHandleExceptionAsInternalError(ex, request);
    }

    @ExceptionHandler(value = {UnsupportedOperationException.class})
    protected ResponseEntity<Object> handleUnsupportedOperationException(UnsupportedOperationException ex, WebRequest request) {
        log.debug("UnsupportedOperationException. Error [{}]", ex.getMessage());
        HttpStatus mappedStatus = HttpStatus.BAD_REQUEST;
        return handleExceptionInternal(ex, null, new HttpHeaders(), mappedStatus, request);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.info("Runtime exception occurred. Error [{}]", ex.getMessage());

        //method of last resort, just capture (typical service layer errors) and return 500
        log.debug("Debug logging exception stack trace", ex);
        log.warn("Error encountered. Error message: [{}]. Returning 500.",ex.getMessage());
        return doHandleExceptionAsInternalError(ex, request);
    }

    private ResponseEntity<Object> doHandleExceptionAsInternalError(Exception ex, WebRequest request) {
        HttpStatus mappedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(ex, null, new HttpHeaders(), mappedStatus, request);
    }
}
