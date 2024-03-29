package io.spring.core.service;

import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;

public class AuthorizationService {

    private AuthorizationService() {
    }

    public static boolean canWriteArticle(User currentUser, Article article) {
        return currentUser.getId().equals(article.getUserId());
    }

    public static boolean canWriteComment(User user, Article article, Comment comment) {
        return user.getId().equals(article.getUserId()) || user.getId().equals(comment.getUserId());
    }
}
