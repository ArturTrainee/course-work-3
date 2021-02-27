package io.spring.api;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.ArticleReactionService;
import io.spring.application.data.ArticleLikesResponse;
import io.spring.core.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/articles/{slug}/likes")
public class ArticleLikesApi {

    private final ArticleQueryService articleQueryService;
    private final ArticleReactionService articleReactionService;

    @Autowired
    public ArticleLikesApi(ArticleQueryService articleQueryService, ArticleReactionService articleReactionService) {
        this.articleQueryService = articleQueryService;
        this.articleReactionService = articleReactionService;
    }

    @GetMapping
    public ResponseEntity<ArticleLikesResponse> getArticleLikes(@PathVariable("slug") String slug,
                                                                @AuthenticationPrincipal User user) {
        return this.articleQueryService.findBySlug(slug, user).map(article -> {
                    final int likesCount = this.articleReactionService.countLikes(article.getId());
                    final boolean isAlreadyLiked = likesCount >= 0
                            && this.articleReactionService.isLikeExists(user.getId(), article.getId());
                    return ResponseEntity.ok(new ArticleLikesResponse(likesCount, isAlreadyLiked));
                }
        ).orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    public ResponseEntity<Object> likeArticle(@PathVariable("slug") String slug,
                                              @RequestParam(value = "isLike", defaultValue = "true") boolean isLike,
                                              @AuthenticationPrincipal User user) {
        if (!isLike) return ResponseEntity.badRequest().build();
        return this.articleQueryService.findBySlug(slug, user).map(article -> {
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            this.articleReactionService.saveOrRemove(user.getId(), article.getId(), isLike);
            return ResponseEntity.ok().build();
        }).orElseThrow(ResourceNotFoundException::new);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteArticle(@PathVariable("slug") String slug,
                                                @AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return this.articleQueryService.findBySlug(slug, user).map(article -> {
            this.articleReactionService.saveOrRemove(user.getId(), article.getId(), false);
            return ResponseEntity.noContent().build();
        }).orElseThrow(ResourceNotFoundException::new);
    }

}
