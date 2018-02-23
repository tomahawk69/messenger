package test.messenger.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base class for messenger exceptions
 */
@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Bad request")
public class MessengerException extends RuntimeException {

    public MessengerException(String message) {
        super(message);
    }

}
