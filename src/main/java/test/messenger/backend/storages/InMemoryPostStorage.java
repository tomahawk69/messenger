package test.messenger.backend.storages;

import org.springframework.stereotype.Service;
import test.messenger.backend.entities.UserPost;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Users posts organized in a Map<User, List<UserPost>>
 * To made it thread-safe ConcurrentHashMap used as Map and ConcurrentLinkedDeque as a List
 */
@Service
public class InMemoryPostStorage implements PostStorage {
    private ConcurrentHashMap<String, Deque<UserPost>> storage = new ConcurrentHashMap<>();

    @Override
    public UserPost savePost(String userName, String message) {
        storage.putIfAbsent(userName, new ConcurrentLinkedDeque<>());
        Deque<UserPost> userPosts = storage.get(userName);
        UserPost result = new UserPost(userName, message);
        userPosts.addFirst(result);
        return result;
    }

    @Override
    public List<UserPost> getUserPosts(String userName) {
        Deque<UserPost> userPosts = storage.get(userName);
        if (userPosts == null) {
            return Collections.emptyList();
        } else {
            return dequeToList(userPosts);
        }
    }

    private List<UserPost> dequeToList(Deque<UserPost> userPosts) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(userPosts.iterator(), Spliterator.ORDERED), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserPost> getUsersPosts(Set<String> userNames) {
        return storage.entrySet().stream()
                .filter(e -> userNames.contains(e.getKey()))
                .flatMap(e -> dequeToList(e.getValue()).stream())
                .sorted((o1, o2) -> o2.getTimeStamp().compareTo(o1.getTimeStamp()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
