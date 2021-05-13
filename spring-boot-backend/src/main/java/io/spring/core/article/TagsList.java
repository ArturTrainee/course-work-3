package io.spring.core.article;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TagsList {
    private List<Tag> tagsList;
}
