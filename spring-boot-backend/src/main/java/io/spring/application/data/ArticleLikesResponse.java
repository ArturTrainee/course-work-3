package io.spring.application.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleLikesResponse {
    private final int count;
    private final boolean isAlreadyLiked;
}
