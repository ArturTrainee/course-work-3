package io.spring.infrastructure.repository;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.article.Tag;
import io.spring.infrastructure.mybatis.mapper.ArticleMapper;
import io.spring.infrastructure.mybatis.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MyBatisArticleRepository implements ArticleRepository {
    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;

    @Autowired
    public MyBatisArticleRepository(ArticleMapper articleMapper, TagMapper tagMapper) {
        this.articleMapper = articleMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    @Transactional
    public void save(final Article article) {
        if (!this.articleMapper.isArticleExists(article.getSlug())) {
            article.setId(this.articleMapper.insert(article));
        } else {
            this.articleMapper.update(article);
        }
        final var currentArticleTags = new HashSet<Tag>(article.getTags().size());
        final var newTags = new HashSet<Tag>();
        article.getTags().forEach(tag -> {
            final var optionalTag = this.tagMapper.findTagByName(tag.getName());
            if (optionalTag.isEmpty()) {
                tag.setId(UUID.randomUUID().toString());
                newTags.add(tag);
                currentArticleTags.add(tag);
            } else {
                currentArticleTags.add(optionalTag.get());
            }
        });
        if (!newTags.isEmpty()) {
            this.tagMapper.insertTags(newTags);
        }
        if (!currentArticleTags.isEmpty()) {
            this.tagMapper.insertArticleTagsRelations(article.getId(), currentArticleTags);
        }
        final var beforeUpdateArticleTags = this.articleMapper.findById(article.getId()).orElseThrow().getTags();
        beforeUpdateArticleTags.removeAll(currentArticleTags);
        if (!beforeUpdateArticleTags.isEmpty()) {
            this.tagMapper.removeArticleTagsRelations(article.getId(), beforeUpdateArticleTags);
        }
    }

    @Override
    public Optional<Article> findById(String id) {
        return this.articleMapper.findById(id);
    }

    @Override
    public Optional<Article> findBySlug(String slug) {
        return this.articleMapper.findBySlug(slug);
    }

    @Override
    public void remove(Article article) {
        this.articleMapper.delete(article.getId());
    }
}
