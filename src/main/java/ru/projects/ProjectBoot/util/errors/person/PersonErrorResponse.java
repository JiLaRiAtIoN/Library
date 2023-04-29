package ru.projects.ProjectBoot.util.errors.person;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonErrorResponse {
    private String message;
    private long timestamp;

    public PersonErrorResponse(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
