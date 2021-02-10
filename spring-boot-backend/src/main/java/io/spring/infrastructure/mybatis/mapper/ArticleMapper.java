package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.article.Article;
import io.spring.core.article.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface ArticleMapper {
    void insert(@Param("article") Article article);

    Optional<Article> findById(@Param("id") String id);

    Optional<Tag> findTag(@Param("tagName") String tagName);

    void insertTag(@Param("tag") Tag tag);

    void insertArticleTagRelation(@Param("articleId") String articleId, @Param("tagId") String tagId);

    Optional<Article> findBySlug(@Param("slug") String slug);

    void update(@Param("article") Article article);

    void delete(@Param("id") String id);
}
