package io.spring.core.article;


import java.util.Optional;

public interface TagRepository {
    Optional<Tag> findById(String id);

    void save(Tag tag);

    void remove(Tag tag);
}
