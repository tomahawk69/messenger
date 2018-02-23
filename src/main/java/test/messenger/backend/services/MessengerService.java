package test.messenger.backend.services;

import test.messenger.backend.entities.UserPost;

import java.util.List;
import java.util.Set;

/**
 * Interface service to serve web service requests
 */
public interface MessengerService {
    UserPost post(String userName, String message);

    void follow(String follower, String following);

    void unfollow(String follower, String following);

    List<UserPost> wall(String user);

    List<UserPost> timeline(String user);

    Set<String> users();

    Set<String> followers(String userName);

    Set<String> followings(String userName);
}
