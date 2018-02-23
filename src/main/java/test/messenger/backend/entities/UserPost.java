package test.messenger.backend.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * We don't care about hash and string representation in this class
 * There are no ID so we can't use direct link to the message
 */
public class UserPost implements Serializable {
    private final String userName;
    private final String message;
    private final LocalDateTime timeStamp;

    public UserPost(String userName, String message) {
        this.userName = userName;
        this.message = message;
        this.timeStamp = LocalDateTime.now();
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
}
