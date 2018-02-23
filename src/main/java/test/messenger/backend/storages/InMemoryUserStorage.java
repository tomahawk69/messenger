package test.messenger.backend.storages;

import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In memory users storage
 * Users as stored in a ConcurrentHashMap (actually Set) and connections between users are stored in
 * another ConcurrentHashMap (actually Set)
 * That gives atomicity however leads to performance losses
 */
@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final Map<FollowerEntity, String> followers = new ConcurrentHashMap<>();

    @Override
    public String addUserIfAbsent(String userName) {
        users.putIfAbsent(userName, userName);
        return userName;
    }

    @Override
    public Boolean isUserExists(String userName) {
        return users.containsKey(userName);
    }

    @Override
    public void follow(String follower, String following) {
        FollowerEntity entity = new FollowerEntity(follower, following);
        followers.putIfAbsent(entity, follower);
    }

    @Override
    public void unfollow(String follower, String following) {
        FollowerEntity entity = new FollowerEntity(follower, following);
        followers.remove(entity);
    }

    @Override
    public Set<String> getFollowers(String userName) {
        return followers.keySet().stream()
                .filter(e -> e.following.equals(userName))
                .map(e -> e.follower)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getFollowings(String userName) {
        return followers.keySet().stream()
                .filter(e -> e.follower.equals(userName))
                .map(e -> e.following)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getUsers() {
        return new HashSet<>(users.keySet());
    }

    @Override
    public void deleteAll() {
        followers.clear();
        users.clear();
    }

    private static class FollowerEntity {
        String follower;
        String following;

        FollowerEntity(String follower, String following) {
            this.follower = follower;
            this.following = following;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FollowerEntity that = (FollowerEntity) o;

            return follower.equals(that.follower) && following.equals(that.following);
        }

        @Override
        public int hashCode() {
            int result = follower.hashCode();
            result = 31 * result + following.hashCode();
            return result;
        }
    }
}
