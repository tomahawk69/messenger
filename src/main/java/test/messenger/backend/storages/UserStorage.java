package test.messenger.backend.storages;

import java.util.Set;

public interface UserStorage {
    String addUserIfAbsent(String userName);

    Boolean isUserExists(String userName);

    void follow(String follower, String following);

    void unfollow(String follower, String following);

    Set<String> getFollowers(String userName);

    Set<String> getFollowings(String userName);

    Set<String> getUsers();

    void deleteAll();

}
