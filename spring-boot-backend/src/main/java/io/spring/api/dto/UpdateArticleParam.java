package io.spring.api.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@JsonRootName("article")
public class UpdateArticleParam {
    private String title = "";
    private String body = "";
    private String description = "";
    @Setter
    private boolean isPublished = false;
}