package io.spring.application;

import io.spring.infrastructure.mybatis.mapper.ArticleViewsHistoryMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleViewsHistoryService {

    private final ArticleViewsHistoryMapper articleViewsHistoryMapper;

    @Autowired
    public ArticleViewsHistoryService(ArticleViewsHistoryMapper articleViewsHistoryMapper) {
        this.articleViewsHistoryMapper = articleViewsHistoryMapper;
    }

    public void save(String userId, String articleId) {
        if (!this.articleViewsHistoryMapper.isViewExists(userId, articleId)) {
            this.articleViewsHistoryMapper.insert(userId, articleId, new DateTime());
        } else {
            this.articleViewsHistoryMapper.update(userId, articleId, new DateTime());
        }
    }

}
