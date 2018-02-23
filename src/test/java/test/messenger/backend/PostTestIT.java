package test.messenger.backend;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import test.messenger.backend.storages.PostStorage;
import test.messenger.backend.storages.UserStorage;

import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PostTestIT implements PostFixture {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private PostStorage postStorage;

    @Value(value = "${message.length.max}")
    private int maxPostLength;

    @Value(value = "${message.length.min?:1}")
    private int minPostLength;


    @Before
    public void setUp() throws Exception {
        userStorage.deleteAll();
        postStorage.deleteAll();
    }

    @Test
    public void shouldPostAndReturnOrderedWall() throws Exception {
        // Given
        String userName1 = UUID.randomUUID().toString();
        String userName2 = UUID.randomUUID().toString();
        String message1 = generateMessage(minPostLength, maxPostLength);
        String message2 = generateMessage(minPostLength, maxPostLength);
        String message3 = generateMessage(minPostLength, maxPostLength);

        // When
        mockMvc
                .perform(post("/post/{user}", userName1)
                        .content(message1));
        mockMvc
                .perform(post("/post/{user}", userName1)
                        .content(message2));
        mockMvc
                .perform(post("/post/{user}", userName2)
                        .content(message3));
        ResultActions resultActions = mockMvc
                .perform(get("/wall/{user}", userName1))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName", is(userName1)))
                .andExpect(jsonPath("$[0].message", is(message2)))
                .andExpect(jsonPath("$[1].userName", is(userName1)))
                .andExpect(jsonPath("$[1].message", is(message1)))
        ;
    }

    @Test
    public void shouldNotReturnWallIfUserNotExists() throws Exception {
        // Given
        String userName = UUID.randomUUID().toString();

        // When
        ResultActions resultActions = mockMvc
                .perform(get("/wall/{user}", userName))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    public void shouldCreateUserWhenPost() throws Exception {
        // Given
        String userName1 = UUID.randomUUID().toString();
        String userName2 = UUID.randomUUID().toString();
        String message1 = generateMessage(minPostLength, maxPostLength);
        String message2 = generateMessage(minPostLength, maxPostLength);
        String message3 = generateMessage(minPostLength, maxPostLength);

        // When
        mockMvc
                .perform(post("/post/{user}", userName1)
                        .content(message1));
        mockMvc
                .perform(post("/post/{user}", userName1)
                        .content(message2));
        mockMvc
                .perform(post("/post/{user}", userName2)
                        .content(message3));

        ResultActions resultActions = mockMvc
                .perform(get("/users"));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect((jsonPath("$", containsInAnyOrder(userName1, userName2))))
        ;
    }

    @Test
    public void shouldFollowAndReturnOrderedTimeline() throws Exception {
        // Given
        String userName = UUID.randomUUID().toString();
        String userName1 = UUID.randomUUID().toString();
        String userName2 = UUID.randomUUID().toString();
        String userName3 = UUID.randomUUID().toString();
        String message1 = generateMessage(minPostLength, maxPostLength);
        String message = generateMessage(minPostLength, maxPostLength);
        String message2 = generateMessage(minPostLength, maxPostLength);
        String message3 = generateMessage(minPostLength, maxPostLength);
        String message4 = generateMessage(minPostLength, maxPostLength);

        // When
        mockMvc
                .perform(post("/post/{user}", userName)
                        .content(message));
        mockMvc
                .perform(post("/post/{user}", userName1)
                        .content(message1));
        mockMvc
                .perform(post("/post/{user}", userName2)
                        .content(message3));
        mockMvc
                .perform(post("/post/{user}", userName3)
                        .content(message4));
        mockMvc
                .perform(post("/post/{user}", userName1)
                        .content(message2));

        mockMvc
                .perform(post("/follow/{follower}/{following}", userName, userName1));
        mockMvc
                .perform(post("/follow/{follower}/{following}", userName, userName2));

        ResultActions resultActions = mockMvc
                .perform(get("/timeline/{user}", userName));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].userName", is(userName1)))
                .andExpect(jsonPath("$[0].message", is(message2)))
                .andExpect(jsonPath("$[1].userName", is(userName2)))
                .andExpect(jsonPath("$[1].message", is(message3)))
                .andExpect(jsonPath("$[2].userName", is(userName1)))
                .andExpect(jsonPath("$[2].message", is(message1)))
        ;
    }

    @Test
    public void shouldUnfollow() throws Exception {
        // Given
        String userName = UUID.randomUUID().toString();
        String userName1 = UUID.randomUUID().toString();
        String userName2 = UUID.randomUUID().toString();
        String message = generateMessage(minPostLength, maxPostLength);
        String message1 = generateMessage(minPostLength, maxPostLength);
        String message2 = generateMessage(minPostLength, maxPostLength);

        // When
        mockMvc
                .perform(post("/post/{user}", userName)
                        .content(message));
        mockMvc
                .perform(post("/post/{user}", userName1)
                        .content(message1));
        mockMvc
                .perform(post("/post/{user}", userName2)
                        .content(message2));

        mockMvc
                .perform(post("/follow/{follower}/{following}", userName, userName1));
        mockMvc
                .perform(post("/follow/{follower}/{following}", userName, userName2));

        ResultActions resultActionsBefore = mockMvc
                .perform(get("/timeline/{user}", userName));

        mockMvc
                .perform(delete("/follow/{follower}/{following}", userName, userName1));

        ResultActions resultActionsAfter = mockMvc
                .perform(get("/timeline/{user}", userName))
                .andDo(print());

        // Then
        resultActionsBefore
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        resultActionsAfter
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userName", is(userName2)))
                .andExpect(jsonPath("$[0].message", is(message2)))
        ;
    }

    @Test
    public void shouldReturnEmptyTimelineIfNoFollowers() throws Exception {
        // Given
        String userName = UUID.randomUUID().toString();
        String userName1 = UUID.randomUUID().toString();
        String userName2 = UUID.randomUUID().toString();
        String message = generateMessage(minPostLength, maxPostLength);
        String message1 = generateMessage(minPostLength, maxPostLength);
        String message2 = generateMessage(minPostLength, maxPostLength);

        // When
        mockMvc
                .perform(post("/post/{user}", userName)
                        .content(message));
        mockMvc
                .perform(post("/post/{user}", userName1)
                        .content(message1));
        mockMvc
                .perform(post("/post/{user}", userName2)
                        .content(message2));

        ResultActions resultActions = mockMvc
                .perform(get("/timeline/{user}", userName))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
        ;
    }

    @Test
    public void shouldNotFollowIfFollowerNotExists() throws Exception {
        // Given
        String userName = UUID.randomUUID().toString();
        String userName1 = UUID.randomUUID().toString();
        String message = generateMessage(minPostLength, maxPostLength);

        // When
        mockMvc
                .perform(post("/post/{user}", userName)
                        .content(message));

        ResultActions resultActions = mockMvc
                .perform(post("/follow/{follower}/{following}", userName1, userName))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    public void shouldNotFollowIfFollowingNotExists() throws Exception {
        // Given
        String userName = UUID.randomUUID().toString();
        String userName1 = UUID.randomUUID().toString();
        String message = generateMessage(minPostLength, maxPostLength);

        // When
        mockMvc
                .perform(post("/post/{user}", userName)
                        .content(message));

        ResultActions resultActions = mockMvc
                .perform(post("/follow/{follower}/{following}", userName, userName1))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    public void shouldNotReciprocalFollowedUser() throws Exception {
        // Given
        String userName1 = UUID.randomUUID().toString();
        String userName2 = UUID.randomUUID().toString();
        String message1 = generateMessage(minPostLength, maxPostLength);
        String message2 = generateMessage(minPostLength, maxPostLength);

        // When
        mockMvc
                .perform(post("/post/{user}", userName1)
                        .content(message1));
        mockMvc
                .perform(post("/post/{user}", userName2)
                        .content(message2));
        mockMvc
                .perform(post("/follow/{follower}/{following}", userName1, userName2));

        ResultActions user1Followers = mockMvc
                .perform(get("/followers/{user}", userName1));
        ResultActions user2Followers = mockMvc
                .perform(get("/followers/{user}", userName2));
        ResultActions user1Followings = mockMvc
                .perform(get("/followings/{user}", userName1));
        ResultActions user2Followings = mockMvc
                .perform(get("/followings/{user}", userName2));

        // Then
        user1Followers
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
        ;
        user2Followers
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is(userName1)))
        ;
        user1Followings
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is(userName2)))
        ;
        user2Followings
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
        ;
    }

}
