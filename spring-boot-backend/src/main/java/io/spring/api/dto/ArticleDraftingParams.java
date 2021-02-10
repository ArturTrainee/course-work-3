package io.spring.api.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@JsonRootName("article")
@NoArgsConstructor
public class ArticleDraftingParams {
    @NotBlank(message = "can't be empty")
    private String title;
    private String description;
    private String body;
    private String[] tagList;
}
