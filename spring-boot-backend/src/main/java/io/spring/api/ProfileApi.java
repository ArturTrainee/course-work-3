package io.spring.api;

import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.Page;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ArticleDataList;
import io.spring.application.data.ProfileData;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static java.util.Collections.singletonMap;

@RestController
@RequestMapping(path = "profiles/{username}")
public class ProfileApi {
    private final ProfileQueryService profileQueryService;
    private final UserRepository userRepository;
    private final ArticleQueryService articleQueryService;

    @Autowired
    public ProfileApi(ProfileQueryService profileQueryService, UserRepository userRepository, ArticleQueryService articleQueryService) {
        this.profileQueryService = profileQueryService;
        this.userRepository = userRepository;
        this.articleQueryService = articleQueryService;
    }

    @GetMapping
    public ResponseEntity<Map<String, ProfileData>> getProfile(@PathVariable("username") String username,
                                                               @AuthenticationPrincipal User user) {
        return profileQueryService.findByUsername(username, user)
                .map(this::profileResponse)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping("/history")
    public ResponseEntity<ArticleDataList> getHistory(@PathVariable("username") String username,
                                                      @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                      @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                      @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(articleQueryService.findByUserViews(user, new Page(offset, limit)));
    }

    @PostMapping(path = "follow")
    public ResponseEntity<Map<String, ProfileData>> follow(@PathVariable("username") String username,
                                                           @AuthenticationPrincipal User user) {
        return userRepository.findByUsername(username).map(target -> {
            FollowRelation followRelation = new FollowRelation(user.getId(), target.getId());
            userRepository.saveRelation(followRelation);
            return profileResponse(profileQueryService.findByUsername(username, user).get());
        }).orElseThrow(ResourceNotFoundException::new);
    }

    @DeleteMapping(path = "follow")
    public ResponseEntity<Map<String, ProfileData>> unfollow(@PathVariable("username") String username,
                                                             @AuthenticationPrincipal User user) {
        return userRepository.findByUsername(username).map(target ->
                userRepository.findRelation(user.getId(), target.getId()).map(relation -> {
                    userRepository.removeRelation(relation);
                    return profileQueryService.findByUsername(username, user)
                            .map(this::profileResponse)
                            .orElseThrow(ResourceNotFoundException::new);
                }).orElseThrow(ResourceNotFoundException::new)
        ).orElseThrow(ResourceNotFoundException::new);
    }

    private ResponseEntity<Map<String, ProfileData>> profileResponse(ProfileData profile) {
        return ResponseEntity.ok(singletonMap("profile", profile));
    }
}
