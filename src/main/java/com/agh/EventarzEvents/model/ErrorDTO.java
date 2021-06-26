package com.agh.EventarzEvents.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ErrorDTO {
    private int status;
    private String error;
    private String path;
    private String timestamp;
    private String message;

    public ErrorDTO(int status, String error, String path, String message) {
        this.status = status;
        this.error = error;
        this.path = path;
        this.timestamp = LocalDateTime.now().toString();
        this.message = message;
    }

    public ErrorDTO(HttpStatus status, String path, String message) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.path = path;
        this.timestamp = LocalDateTime.now().toString();
        this.message = message;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
}
