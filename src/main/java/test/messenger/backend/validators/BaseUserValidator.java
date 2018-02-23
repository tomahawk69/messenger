package test.messenger.backend.validators;

import org.springframework.stereotype.Component;
import test.messenger.backend.exceptions.MessengerException;

@Component
public class BaseUserValidator implements UserValidator {

    @Override
    public void validate(String user) {
        if (user == null || user.isEmpty()) {
            throw new MessengerException("User name should not be empty");
        }
    }
}
