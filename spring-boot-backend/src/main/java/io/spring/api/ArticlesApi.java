package io.spring.api;

import io.spring.api.dto.ArticleCreationParams;
import io.spring.api.dto.ArticleDraftingParams;
import io.spring.api.exception.InvalidRequestException;
import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.Page;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ArticleDataList;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.service.AuthorizationService;
import io.spring.core.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(path = "/articles")
public class ArticlesApi {
    private final ArticleRepository articleRepository;
    private final ArticleQueryService articleQueryService;
    private static final String ARTICLE = "article";

    @Autowired
    public ArticlesApi(ArticleRepository articleRepository, ArticleQueryService articleQueryService) {
        this.articleRepository = articleRepository;
        this.articleQueryService = articleQueryService;
    }

    @GetMapping(path = "trending")
    public ResponseEntity<ArticleDataList> getTrending(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                       @RequestParam(value = "limit", defaultValue = "5") int limit ) {
        return ResponseEntity.ok(articleQueryService.findTrending(new Page(offset, limit)));
    }

    @GetMapping(path = "feed")
    public ResponseEntity<ArticleDataList> getFeed(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                   @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                   @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(articleQueryService.findUserFeed(user, new Page(offset, limit)));
    }

    @GetMapping
    public ResponseEntity<ArticleDataList> getArticles(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                       @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                       @RequestParam(value = "isPublished") Boolean isPublished,
                                                       @RequestParam(value = "tag", required = false) String tag,
                                                       @RequestParam(value = "favorited", required = false) String favoritedBy,
                                                       @RequestParam(value = "author", required = false) String author,
                                                       @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(articleQueryService.findRecentArticles(tag, author, favoritedBy, isPublished, new Page(offset, limit), user));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createArticle(@Valid @RequestBody ArticleCreationParams articleParams,
                                                             BindingResult bindingResult,
                                                             @AuthenticationPrincipal User user) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        if (articleQueryService.findBySlug(Article.toSlug(articleParams.getTitle()), null).isPresent()) {
            bindingResult.rejectValue("title", "DUPLICATED", "article name exists");
            throw new InvalidRequestException(bindingResult);
        }
        final var article = new Article(articleParams.getTitle(), articleParams.getDescription(),
                articleParams.getBody(), articleParams.getTagList(), user.getId(), articleParams.isPublished()
        );
        articleRepository.save(article);
        return articleQueryService.findById(article.getId(), user)
                .<ResponseEntity<Map<String, Object>>>map(a -> ResponseEntity.ok(Collections.singletonMap(ARTICLE, a)))
                .orElseGet(() -> ResponseEntity.badRequest().body(Collections.emptyMap()));
    }

    @PostMapping("/draft")
    public ResponseEntity<Map<String, Object>> saveArticleDraft(@Valid @RequestBody ArticleDraftingParams articleParams,
                                                                BindingResult bindingResult,
                                                                @AuthenticationPrincipal User user) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        if (articleQueryService.findBySlug(Article.toSlug(articleParams.getTitle()), null).isPresent()) {
            bindingResult.rejectValue("title", "DUPLICATED", "article name exists");
            throw new InvalidRequestException(bindingResult);
        }
        final String title = articleParams.getTitle();
        final var article = new Article(title, articleParams.getDescription(), articleParams.getBody(),
                articleParams.getTagList(), user.getId(), false
        );
        articleRepository.save(article);
        return articleQueryService.findById(article.getId(), user)
                .<ResponseEntity<Map<String, Object>>>map(a -> ResponseEntity.ok(Collections.singletonMap(ARTICLE, a)))
                .orElseGet(() -> ResponseEntity.badRequest().body(Collections.emptyMap()));
    }

    @PutMapping("/draft/{slug}")
    public ResponseEntity<Map<String, ArticleData>> updateArticle(@PathVariable("slug") String slug,
                                                                  @AuthenticationPrincipal User user,
                                                                  @Valid @RequestBody ArticleDraftingParams articleParams) {
        return articleRepository.findBySlug(slug).map(article -> {
            if (!AuthorizationService.canWriteArticle(user, article)) {
                throw new NoAuthorizationException();
            }
            article.update(articleParams.getTitle(), articleParams.getDescription(), articleParams.getBody(), false);
            articleRepository.save(article);
            return articleQueryService.findBySlug(slug, user)
                    .map(a -> ResponseEntity.ok(Collections.singletonMap(ARTICLE, a)))
                    .orElseThrow(ResourceNotFoundException::new);
        }).orElseThrow(ResourceNotFoundException::new);
    }
    
}