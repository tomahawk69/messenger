package test.messenger.backend.storages;

import test.messenger.backend.entities.UserPost;

import java.util.List;
import java.util.Set;

public interface PostStorage {

    UserPost savePost(String userName, String message);

    List<UserPost> getUserPosts(String userNames);

    List<UserPost> getUsersPosts(Set<String> userNames);

    void deleteAll();
}
