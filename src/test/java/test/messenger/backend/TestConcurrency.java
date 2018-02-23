package test.messenger.backend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import test.messenger.backend.entities.UserPost;
import test.messenger.backend.services.MessengerService;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestConcurrency implements PostFixture {

    @Autowired
    private MessengerService messengerService;

    @Value(value = "${message.length.max}")
    private int maxPostLength;

    @Value(value = "${message.length.min?:1}")
    private int minPostLength;


    @Test
    public void shouldConcurrentlyPostMessages() throws Exception {
        // Given
        int cnt = 100;
        final String[] users = IntStream.range(0, cnt).boxed().map(Object::toString).toArray(String[]::new);
        final String message = generateMessage(minPostLength, maxPostLength);

        // When
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch starter = new CountDownLatch(1);
        IntStream.range(0, cnt).forEach(i -> executorService.submit(() -> {
            try {
                starter.await();
                for (String user : users) {
                    for (int j = 0; j < cnt; j++) {
                        messengerService.post(user, message);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        executorService.shutdown();
        starter.countDown();
        while (!executorService.isTerminated()) {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        }

        // Then
        Set<String> resultUsers = messengerService.users();
        assertEquals(users.length, resultUsers.size());
        for (final String user : users) {
            List<UserPost> posts = messengerService.wall(user);
            assertEquals(cnt * cnt, posts.size());
            posts.forEach(p -> assertEquals(user, p.getUserName()));
        }
    }

    @Test
    public void shouldConcurrentlyFollow() throws Exception {
        // Given
        int cnt = 1001;
        int threads = 10;
        final String[] users = IntStream.range(0, cnt).boxed().map(Object::toString).toArray(String[]::new);
        final String message = generateMessage(minPostLength, maxPostLength);

        // When
        for (final String user : users) {
            messengerService.post(user, message);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch starter = new CountDownLatch(1);
        IntStream.range(0, (cnt - 1) / threads).forEach(i -> executorService.submit(() -> {
            try {
                starter.await();
                for (int j = i * threads; j < (i + 1) * threads; j++) {
                    messengerService.follow(users[cnt - 1], users[j]);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        executorService.shutdown();
        starter.countDown();
        while (!executorService.isTerminated()) {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        }

        // Then
        Set<String> followings = messengerService.followings(users[cnt - 1]);
        assertEquals(users.length - 1, followings.size());
        for (int i = 0; i < users.length - 1; i++) {
            Set<String> followers = messengerService.followers(users[i]);
            assertEquals(1, followers.size());
            assertEquals(users[cnt - 1], followers.iterator().next());
        }
    }

    @Test
    public void shouldConcurrentlyUnFollow() throws Exception {
        // Given
        int cnt = 1001;
        int threads = 10;
        final String[] users = IntStream.range(0, cnt).boxed().map(Object::toString).toArray(String[]::new);
        final String message = generateMessage(minPostLength, maxPostLength);

        // When
        messengerService.post(users[cnt - 1], message);
        for (int i = 0; i < users.length - 1; i++) {
            messengerService.post(users[i], message);
            messengerService.follow(users[cnt - 1], users[i]);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch starter = new CountDownLatch(1);
        IntStream.range(0, (cnt - 1) / threads).forEach(i -> executorService.submit(() -> {
            try {
                starter.await();
                for (int j = i * threads; j < (i + 1) * threads; j++) {
                    messengerService.unfollow(users[cnt - 1], users[j]);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        executorService.shutdown();
        starter.countDown();
        while (!executorService.isTerminated()) {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        }

        // Then
        Set<String> followings = messengerService.followings(users[cnt - 1]);
        assertEquals(0, followings.size());
        for (int i = 0; i < users.length - 1; i++) {
            Set<String> followers = messengerService.followers(users[i]);
            assertEquals(0, followers.size());
        }
    }
}
