package io.spring.application;

import io.spring.infrastructure.mybatis.mapper.ArticleReactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ArticleReactionService {

    private final ArticleReactionMapper articleReactionMapper;

    @Autowired
    public ArticleReactionService(ArticleReactionMapper articleReactionMapper) {
        this.articleReactionMapper = articleReactionMapper;
    }

    public int countLikes(String articleId) {
        return this.articleReactionMapper.countArticleReactions(articleId);
    }

    public boolean isLikeExists(String userId, String articleId) {
        return this.articleReactionMapper.isReactionExists(userId, articleId);
    }

    public void saveOrRemove(String userId, String articleId, boolean isLike) {
        if (!isLikeExists(userId, articleId)) {
            this.articleReactionMapper.insert(UUID.randomUUID().toString(), userId, articleId, isLike);
        } else {
            this.articleReactionMapper.delete(userId, articleId);
        }
    }
}
