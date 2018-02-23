package test.messenger.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.messenger.backend.exceptions.MessengerException404;
import test.messenger.backend.storages.UserStorage;
import test.messenger.backend.validators.UserValidator;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final List<UserValidator> userValidators;
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(List<UserValidator> userValidators, UserStorage userStorage) {
        this.userValidators = userValidators;
        this.userStorage = userStorage;
    }

    @Override
    public String addUser(String userName) {
        userValidators.forEach(v -> v.validate(userName));
        return userStorage.addUserIfAbsent(userName);
    }

    @Override
    public boolean isUserExists(String userName) {
        return userStorage.isUserExists(userName);
    }

    @Override
    public void follow(String follower, String following) {
        if (!userStorage.isUserExists(follower)) {
            throw new MessengerException404("User not found: " + follower);
        }
        if (!userStorage.isUserExists(following)) {
            throw new MessengerException404("User not found: " + following);
        }
        userStorage.follow(follower, following);
    }

    @Override
    public void unfollow(String follower, String following) {
        userStorage.unfollow(follower, following);
    }

    @Override
    public Set<String> getFollowings(String follower) {
        if (!userStorage.isUserExists(follower)) {
            throw MessengerException404.noSuchUserException(follower);
        }
        return userStorage.getFollowings(follower);
    }

    @Override
    public Set<String> getFollowers(String following) {
        if (!userStorage.isUserExists(following)) {
            throw MessengerException404.noSuchUserException(following);
        }
        return userStorage.getFollowers(following);
    }

    @Override
    public Set<String> getUsers() {
        return userStorage.getUsers();
    }
}
