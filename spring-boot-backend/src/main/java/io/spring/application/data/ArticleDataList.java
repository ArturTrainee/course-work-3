package io.spring.application.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ArticleDataList {
    @JsonProperty("articles")
    private final List<ArticleData> articleDatas;
    @JsonProperty("articlesCount")
    private final int count;
}
