package io.spring.core.article;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "name")
public class Tag {
    private String id;
    private String name;

    public Tag(String name) {
        this.name = name;
    }
}
