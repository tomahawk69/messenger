package test.messenger.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.messenger.backend.entities.UserPost;
import test.messenger.backend.exceptions.MessengerException404;

import java.util.List;
import java.util.Set;

@Service
public class MessengerServiceImpl implements MessengerService {

    private final UserService userService;
    private final PostService postService;

    @Autowired
    public MessengerServiceImpl(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @Override
    public UserPost post(String userName, String message) {
        String savedUserName = userService.addUser(userName);
        return postService.addPost(savedUserName, message);
    }

    @Override
    public void follow(String follower, String following) {
        userService.follow(follower, following);
    }

    @Override
    public void unfollow(String follower, String following) {
        userService.unfollow(follower, following);
    }

    @Override
    public List<UserPost> wall(String userName) {
        if (!userService.isUserExists(userName)) {
            throw MessengerException404.noSuchUserException(userName);
        }
        return postService.wall(userName);
    }

    @Override
    public List<UserPost> timeline(String userName) {
        if (!userService.isUserExists(userName)) {
            throw MessengerException404.noSuchUserException(userName);
        }
        return postService.timeline(userService.getFollowings(userName));
    }

    @Override
    public Set<String> users() {
        return userService.getUsers();
    }

    @Override
    public Set<String> followers(String userName) {
        return userService.getFollowers(userName);
    }

    public Set<String> followings(String userName) {
        return userService.getFollowings(userName);
    }
}
