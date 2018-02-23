package test.messenger.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not found")
public class MessengerException404 extends RuntimeException {

    public MessengerException404(String message) {
        super(message);
    }

    public static MessengerException404 noSuchUserException(String userName) {
        return new MessengerException404("No such user: " + userName);
    }
}
