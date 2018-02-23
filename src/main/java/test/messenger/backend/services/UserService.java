package test.messenger.backend.services;

import java.util.Set;

/**
 * Service to manage users and users connections
 */
public interface UserService {
    String addUser(String userName);

    boolean isUserExists(String userName);

    void follow(String follower, String following);

    void unfollow(String follower, String following);

    Set<String> getFollowings(String follower);

    Set<String> getFollowers(String following);

    Set<String> getUsers();
}
