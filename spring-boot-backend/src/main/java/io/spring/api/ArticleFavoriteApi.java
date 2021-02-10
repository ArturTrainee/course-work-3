package io.spring.api;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.data.ArticleData;
import io.spring.application.ArticleQueryService;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(path = "articles/{slug}/favorite")
public class ArticleFavoriteApi {
    private final ArticleFavoriteRepository articleFavoriteRepository;
    private final ArticleRepository articleRepository;
    private final ArticleQueryService articleQueryService;

    @Autowired
    public ArticleFavoriteApi(ArticleFavoriteRepository articleFavoriteRepository,
                              ArticleRepository articleRepository,
                              ArticleQueryService articleQueryService) {
        this.articleFavoriteRepository = articleFavoriteRepository;
        this.articleRepository = articleRepository;
        this.articleQueryService = articleQueryService;
    }

    @PostMapping
    public ResponseEntity<Map<String, ArticleData>> favoriteArticle(@PathVariable("slug") String slug,
                                                                    @AuthenticationPrincipal User user) {
        final Article article = getArticle(slug);
        final var articleFavorite = new ArticleFavorite(article.getId(), user.getId());
        articleFavoriteRepository.save(articleFavorite);
        return articleQueryService.findBySlug(slug, user)
                .map(this::responseArticleData)
                .orElse(ResponseEntity.badRequest().body(Collections.emptyMap()));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, ArticleData>> unfavoriteArticle(@PathVariable("slug") String slug,
                                                                      @AuthenticationPrincipal User user) {
        articleFavoriteRepository.find(getArticle(slug).getId(), user.getId())
                .ifPresent(articleFavoriteRepository::remove);
        return articleQueryService.findBySlug(slug, user)
                .map(this::responseArticleData)
                .orElse(ResponseEntity.badRequest().body(Collections.emptyMap()));
    }

    private ResponseEntity<Map<String, ArticleData>> responseArticleData(final ArticleData articleData) {
        return ResponseEntity.ok(Collections.singletonMap("article", articleData));
    }

    private Article getArticle(String slug) {
        return articleRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    }
}
