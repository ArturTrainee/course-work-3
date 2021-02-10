package io.spring.infrastructure.repository;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.infrastructure.mybatis.mapper.ArticleMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class MyBatisArticleRepository implements ArticleRepository {
    private final ArticleMapper articleMapper;

    public MyBatisArticleRepository(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    @Transactional
    public void save(Article article) {
        if (articleMapper.findById(article.getId()).isEmpty()) {
            article.getTags().forEach(tag -> {
                if (articleMapper.findTag(tag.getName()).isEmpty()) {
                    articleMapper.insertTag(tag);
                }
                articleMapper.insertArticleTagRelation(article.getId(), tag.getId());
            });
            articleMapper.insert(article);
        } else {
            articleMapper.update(article);
        }
    }

    @Override
    public Optional<Article> findById(String id) {
        return articleMapper.findById(id);
    }

    @Override
    public Optional<Article> findBySlug(String slug) {
        return articleMapper.findBySlug(slug);
    }

    @Override
    public void remove(Article article) {
        articleMapper.delete(article.getId());
    }
}
