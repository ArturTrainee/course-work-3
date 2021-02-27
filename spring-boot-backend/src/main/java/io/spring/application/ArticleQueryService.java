package io.spring.application;

import io.spring.application.data.ArticleData;
import io.spring.application.data.ArticleDataList;
import io.spring.application.data.ArticleFavoriteCount;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.ArticleFavoritesReadService;
import io.spring.infrastructure.mybatis.readservice.ArticleReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class ArticleQueryService {
    private final ArticleReadService articleReadService;
    private final UserRelationshipQueryService userRelationshipQueryService;
    private final ArticleFavoritesReadService articleFavoritesReadService;

    @Autowired
    public ArticleQueryService(ArticleReadService articleReadService,
                               UserRelationshipQueryService userRelationshipQueryService,
                               ArticleFavoritesReadService articleFavoritesReadService) {
        this.articleReadService = articleReadService;
        this.userRelationshipQueryService = userRelationshipQueryService;
        this.articleFavoritesReadService = articleFavoritesReadService;
    }

    public Optional<ArticleData> findById(String id, User user) {
        final ArticleData articleData = articleReadService.findById(id);
        return (articleData != null && user != null)
                ? Optional.of(addUserInfo(id, user, articleData))
                : Optional.empty();
    }

    public Optional<ArticleData> findBySlug(String slug, User user) {
        final ArticleData articleData = articleReadService.findBySlug(slug);
        return (articleData != null)
                ? Optional.of(addUserInfo(articleData.getId(), user, articleData))
                : Optional.empty();
    }

    public ArticleDataList findRecentArticles(String tag, String author, String favoritedBy, boolean isPublished,
                                              Page page, User currentUser) {
        final List<String> articleIds = articleReadService.queryArticles(tag, author, favoritedBy, isPublished, page);
        int articleCount = articleReadService.countArticle(tag, author, favoritedBy);
        return articleIds.isEmpty()
                ? new ArticleDataList(Collections.emptyList(), articleCount)
                : new ArticleDataList(addUserInfo(articleReadService.findArticles(articleIds), currentUser), articleCount);
    }

    public ArticleDataList findUserFeed(User user, Page page) {
        final List<String> followdUsers = userRelationshipQueryService.followedUsers(user.getId());
        return followdUsers.isEmpty()
                ? new ArticleDataList(Collections.emptyList(), 0)
                : new ArticleDataList(
                addUserInfo(articleReadService.findArticlesOfAuthors(followdUsers, page), user),
                articleReadService.countFeedSize(followdUsers)
        );
    }

    public ArticleDataList findByUserViews(User currentUser, Page page) {
        final List<ArticleData> articles = articleReadService.findByUserViewHistory(currentUser.getId(), page);
        return articles.isEmpty()
                ? new ArticleDataList(Collections.emptyList(), 0)
                : new ArticleDataList(articles, articles.size());
    }

    public ArticleDataList findTrending(Page page) {
        final List<ArticleData> articles =  articleReadService.findRecentlyMostViewed(page);
        return articles.isEmpty()
                ? new ArticleDataList(Collections.emptyList(), 0)
                : new ArticleDataList(articles, articles.size());
    }

    private List<ArticleData> addFavorites(List<ArticleData> articles, User currentUser) {
        final Set<String> favoritedArticles = articleFavoritesReadService.userFavorites(getIds(articles), currentUser);
        articles.forEach(article -> {
            if (favoritedArticles.contains(article.getId())) {
                article.setFavorited(true);
            }
        });
        return articles;
    }

    private List<ArticleData> addFavoritesCount(List<ArticleData> articles) {
        final List<ArticleFavoriteCount> favoritesCounts = articleFavoritesReadService.articlesFavoriteCount(getIds(articles));
        final Map<String, Integer> countMap = favoritesCounts.stream()
                .collect(toMap(ArticleFavoriteCount::getId, ArticleFavoriteCount::getCount));
        articles.forEach(article -> article.setFavoritesCount(countMap.get(article.getId())));
        return articles;
    }

    private List<ArticleData> addUserInfo(List<ArticleData> articles, User currentUser) {
        return currentUser == null
                ? addFavoritesCount(articles)
                : addFollowedAuthors(addFavorites(articles, currentUser), currentUser);
    }

    private List<ArticleData> addFollowedAuthors(List<ArticleData> articles, User currentUser) {
        final Set<String> followingAuthors = userRelationshipQueryService.followingAuthors(
                currentUser.getId(),
                articles.stream()
                        .map(article -> article.getProfileData().getId())
                        .collect(toList())
        );
        articles.forEach(article -> {
            if (followingAuthors.contains(article.getProfileData().getId())) {
                article.getProfileData().setFollowing(true);
            }
        });
        return articles;
    }

    private ArticleData addUserInfo(String id, User user, ArticleData articleData) {
        articleData.setFavorited(articleFavoritesReadService.isUserFavorite(user.getId(), id));
        articleData.setFavoritesCount(articleFavoritesReadService.articleFavoriteCount(id));
        articleData.getProfileData().setFollowing(userRelationshipQueryService.isUserFollowing(
                user.getId(),
                articleData.getProfileData().getId())
        );
        return articleData;
    }

    private List<String> getIds(List<ArticleData> articles) {
        return articles.stream()
                .map(ArticleData::getId)
                .collect(toList());
    }
}

