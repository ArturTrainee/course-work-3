package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.article.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper
public interface TagMapper {

    boolean isTagExists(@Param("tagName") String tagName);

    Optional<Tag> findTagByName(@Param("tagName") String tagName);

    Optional<Tag> findTagById(@Param("tagId") String tagId);

    List<Tag> findTagsByArticleId(@Param("articleId") String articleId);

    void insertTag(@Param("tag") Tag tag);

    void insertTags(@Param("tags") Collection<Tag> tags);

    void insertArticleTagRelation(@Param("articleId") String articleId, @Param("tagId") String tagId);

    void insertArticleTagsRelations(@Param("articleId") String articleId, @Param("tags") Collection<Tag> tags);

    void removeArticleTagRelation(@Param("articleId") String articleId, @Param("tagId") String tagId);

    void removeArticleTagsRelations(@Param("articleId") String articleId, @Param("tags") Collection<Tag> tags);

    void delete(@Param("id") String id);
}
