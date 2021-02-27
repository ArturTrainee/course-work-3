package io.spring.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleReactionMapper {
    boolean isReactionExists(@Param("userId") String userId, @Param("articleId") String articleId);

    void insert(@Param("id") String id, @Param("userId") String userId, @Param("articleId") String articleId, @Param("isLike") boolean isLike);

    void delete(@Param("userId") String userId, @Param("articleId") String articleId);

    int countArticleReactions(@Param("articleId") String articleId);
}
