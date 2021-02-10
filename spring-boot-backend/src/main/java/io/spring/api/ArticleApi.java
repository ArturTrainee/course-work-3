package io.spring.api;

import io.spring.api.dto.UpdateArticleParam;
import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.data.ArticleData;
import io.spring.core.article.ArticleRepository;
import io.spring.core.service.AuthorizationService;
import io.spring.core.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(path = "/articles/{slug}")
public class ArticleApi {
    private final ArticleQueryService articleQueryService;
    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleApi(ArticleQueryService articleQueryService, ArticleRepository articleRepository) {
        this.articleQueryService = articleQueryService;
        this.articleRepository = articleRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, ArticleData>> article(@PathVariable("slug") String slug,
                                                            @AuthenticationPrincipal User user) {
        return articleQueryService.findBySlug(slug, user)
                .map(articleData -> ResponseEntity.ok(Collections.singletonMap("article", articleData)))
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PutMapping
    public ResponseEntity<Map<String, ArticleData>> updateArticle(@PathVariable("slug") String slug,
                                                             @AuthenticationPrincipal User user,
                                                             @Valid @RequestBody UpdateArticleParam updateArticleParam) {
        return articleRepository.findBySlug(slug).map(article -> {
            if (!AuthorizationService.canWriteArticle(user, article)) {
                throw new NoAuthorizationException();
            }
            article.update(updateArticleParam.getTitle(),
                    updateArticleParam.getDescription(),
                    updateArticleParam.getBody(),
                    updateArticleParam.isPublished());
            articleRepository.save(article);
            return articleQueryService.findBySlug(slug, user)
                    .map(a -> ResponseEntity.ok(Collections.singletonMap("article", a)))
                    .orElseThrow(ResourceNotFoundException::new);
        }).orElseThrow(ResourceNotFoundException::new);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteArticle(@PathVariable("slug") String slug,
                                                @AuthenticationPrincipal User user) {
        return articleRepository.findBySlug(slug).map(article -> {
            if (!AuthorizationService.canWriteArticle(user, article)) {
                throw new NoAuthorizationException();
            }
            articleRepository.remove(article);
            return ResponseEntity.noContent().build();
        }).orElseThrow(ResourceNotFoundException::new);
    }

}
