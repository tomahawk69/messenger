package test.messenger.backend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import test.messenger.backend.entities.UserPost;
import test.messenger.backend.services.MessengerService;

import java.util.List;
import java.util.Set;

@RestController
class MessengerFacadeImpl implements MessengerFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessengerFacadeImpl.class);

    private final MessengerService messengerService;

    @Autowired
    public MessengerFacadeImpl(MessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @Override
    @GetMapping("/users")
    public Set<String> getUsers() {
        return messengerService.users();
    }

    @Override
    @PostMapping(value = "/post/{user}")
    public UserPost postMessage(@PathVariable(value = "user") String userName, @RequestBody String message) {
        LOGGER.debug("postMessage {}: {}", userName, message);
        return messengerService.post(userName, message);
    }

    @Override
    @GetMapping("/wall/{user}")
    public List<UserPost> wall(@PathVariable(value = "user") String userName) {
        LOGGER.debug("wall of {}", userName);
        return messengerService.wall(userName);
    }

    @Override
    @GetMapping("/timeline/{user}")
    public List<UserPost> timeline(@PathVariable(value = "user") String userName) {
        LOGGER.debug("timeline of {}", userName);
        return messengerService.timeline(userName);
    }

    @Override
    @PostMapping("/follow/{follower}/{following}")
    public void follow(@PathVariable(name = "follower") String follower,
                       @PathVariable(name = "following") String following) {
        LOGGER.debug("follow {} of {}", follower, following);
        messengerService.follow(follower, following);
    }

    @Override
    @DeleteMapping("/follow/{follower}/{following}")
    public void unfollow(@PathVariable(name = "follower") String follower,
                         @PathVariable(name = "following") String following) {
        LOGGER.debug("unfollow {} of {}", follower, following);
        messengerService.unfollow(follower, following);
    }

    @Override
    @GetMapping("/followers/{user}")
    public Set<String> followers(@PathVariable(name = "user") String userName) {
        LOGGER.debug("getFollowers of {}", userName);
        return messengerService.followers(userName);
    }

    @Override
    @GetMapping("/followings/{user}")
    public Set<String> followings(@PathVariable(name = "user") String userName) {
        LOGGER.debug("following of {}", userName);
        return messengerService.followings(userName);
    }
}
