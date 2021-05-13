package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.article.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface ArticleMapper {
    String insert(@Param("article") Article article);

    Optional<Article> findById(@Param("id") String id);

    boolean isArticleExists(@Param("slug") String slug);

    Optional<Article> findBySlug(@Param("slug") String slug);

    void update(@Param("article") Article article);

    void delete(@Param("id") String id);
}
