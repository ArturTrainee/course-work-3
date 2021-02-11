package io.spring.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;

@Mapper
public interface ArticleViewsHistoryMapper {
    boolean isViewExists(@Param("userId") String userId, @Param("articleId") String articleId);

    void update(@Param("userId") String userId, @Param("articleId") String articleId, @Param("time") DateTime dateTime);

    void insert(@Param("userId") String userId, @Param("articleId") String articleId, @Param("time") DateTime dateTime);
}
