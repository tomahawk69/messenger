package test.messenger.backend.services;

import test.messenger.backend.entities.UserPost;

import java.util.List;
import java.util.Set;

/**
 * Service to manage users posts
 */
public interface PostService {
    UserPost addPost(String userName, String message);

    List<UserPost> wall(String userName);

    List<UserPost> timeline(Set<String> userNames);
}
