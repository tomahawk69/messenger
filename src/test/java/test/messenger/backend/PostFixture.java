package test.messenger.backend;

import java.util.concurrent.ThreadLocalRandom;

public interface PostFixture {

    default String generateMessage(int minPostLength, int maxPostLength) {
        return ThreadLocalRandom.current().ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(ThreadLocalRandom.current().nextInt(minPostLength, maxPostLength))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
