package io.spring.application.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserArticleView {
    private String userid;
    private String articleId;
    private DateTime lastReadAt;
}
