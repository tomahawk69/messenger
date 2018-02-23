package test.messenger.backend.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import test.messenger.backend.entities.UserPost;

import java.util.List;
import java.util.Set;

/**
 * Can be used to expose API
 */
public interface MessengerFacade {

    Set<String> getUsers();

    UserPost postMessage(String userName, String message);

    List<UserPost> wall(String userName);

    List<UserPost> timeline(String userName);

    void follow(String follower, String following);

    void unfollow(String follower, String following);

    Set<String> followers(String userName);

    Set<String> followings(String userName);
}
