package io.spring.api.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.spring.core.article.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonRootName("article")
public class UpdatePublishedArticleParams {
    @NotBlank(message = "can't be empty")
    private String title;
    @NotBlank(message = "can't be empty")
    private String description;
    @NotBlank(message = "can't be empty")
    private String body;
    private String[] tagList;
}