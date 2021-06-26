package com.agh.EventarzEvents.exceptions;

import com.agh.EventarzEvents.model.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class EventsExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity handleEventNotFoundException(EventNotFoundException exception, HttpServletRequest request) {
        return getResponse(HttpStatus.NOT_FOUND, request.getRequestURI(), "Event not found!");
    }

    @ExceptionHandler(EventFullException.class)
    public ResponseEntity handleEventFullException(EventNotFoundException exception, HttpServletRequest request) {
        return getResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), "Event full!");
    }

    private ResponseEntity<ErrorDTO> getResponse(HttpStatus status, String requestURI, String message) {
        ErrorDTO errorDTO = new ErrorDTO(status, requestURI, message);
        return ResponseEntity.status(status).body(errorDTO);
    }
}
