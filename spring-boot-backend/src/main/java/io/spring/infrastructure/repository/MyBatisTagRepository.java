package io.spring.infrastructure.repository;


import io.spring.core.article.Tag;
import io.spring.core.article.TagRepository;
import io.spring.infrastructure.mybatis.mapper.TagMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MyBatisTagRepository implements TagRepository {
    private final TagMapper tagMapper;

    public MyBatisTagRepository(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    public Optional<Tag> findById(String id) {
        return this.tagMapper.findTagById(id);
    }

    @Override
    public void save(Tag tag) {
        this.tagMapper.insertTag(tag);
    }

    @Override
    public void remove(Tag tag) {
        this.tagMapper.delete(tag.getId());
    }
}
