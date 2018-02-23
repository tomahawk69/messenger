package test.messenger.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.messenger.backend.entities.UserPost;
import test.messenger.backend.storages.PostStorage;
import test.messenger.backend.validators.PostValidator;

import java.util.List;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {
    private final PostStorage postStorage;
    private final List<PostValidator> postValidators;

    @Autowired
    public PostServiceImpl(PostStorage postStorage,
                           List<PostValidator> postValidators) {
        this.postStorage = postStorage;
        this.postValidators = postValidators;
    }

    @Override
    public UserPost addPost(String userName, String message) {
        postValidators.forEach(s -> s.validate(message));
        return postStorage.savePost(userName, message);
    }

    @Override
    public List<UserPost> wall(String userName) {
        return postStorage.getUserPosts(userName);
    }

    @Override
    public List<UserPost> timeline(Set<String> userNames) {
        return postStorage.getUsersPosts(userNames);
    }
}
