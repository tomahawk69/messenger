package test.messenger.backend.validators;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import test.messenger.backend.exceptions.MessengerException;

/**
 * There are no validation of validators parameters
 * maxLength can be lesser then minLength
 */
@Component
public class PostValidatorLength implements PostValidator {

    @Value(value = "${message.length.max}")
    private int maxLength;
    @Value(value = "${message.length.min?:1}")
    private int minLength;

    @Override
    public void validate(String message) {
        int messageLength = message.length();
        if (messageLength < minLength) {
            throw new MessengerException(String.format("The message length should be at least %d symbols but only %d symbols passed",
                    minLength, messageLength));
        }
        if (messageLength > maxLength) {
            throw new MessengerException(String.format("The message length should be maximum %d symbols but %d symbols passed",
                    maxLength, messageLength));
        }
    }
}
