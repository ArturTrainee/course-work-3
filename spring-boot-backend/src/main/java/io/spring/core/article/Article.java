package io.spring.core.article;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Article {
    private String userId;
    private String id;
    private String slug;
    private String title;
    private String description;
    private String body;
    private List<Tag> tags;
    private DateTime createdAt;
    private DateTime updatedAt;
    private boolean isPublished;
    private static final String EMPTY_STRING = "";
    private static final char[] cyrChars = {
            'а', 'б', 'в', 'г', 'ґ', 'д', 'е', 'є', 'ж', 'з', 'и', 'і', 'ї', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ь', 'ю', 'я', 'ё', 'ъ', 'ы', 'э'
    };
    private static final String[] latChars = {
            "a", "b", "v", "h", "g", "d", "e", "ye", "zh", "z", "y", "i", "yi", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "kh", "ts", "ch", "sh", "shch", "", "yu", "ya", "yo", "", "y", "e"
    };

    public Article(String title, String description, String body, String[] tagList, String userId) {
        this(title, description, body, tagList, userId, new DateTime(), true);
    }

    public Article(String title, String description, String body, String[] tagList, String userId, DateTime createdAt) {
        this(title, description, body, tagList, userId, createdAt, true);
    }

    public Article(String title, String description, String body, String[] tagList, String userId, boolean isPublished) {
        this(title, description, body, tagList, userId, new DateTime(), isPublished);
    }

    public Article(String title, String description, String body, String[] tagList, String userId, DateTime createdAt,
                   boolean isPublished) {
        this.id = UUID.randomUUID().toString();
        this.slug = toSlug(title);
        this.title = title;
        this.description = description;
        this.body = body;
        this.tags = Arrays.stream(tagList).collect(toSet()).stream().map(Tag::new).collect(toList());
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.isPublished = isPublished;
    }

    public void update(String title, String description, String body, boolean isPublished) {
        this.isPublished = isPublished;
        if (!title.equals(EMPTY_STRING)) {
            this.title = title;
            this.slug = toSlug(title);
        }
        if (!description.equals(EMPTY_STRING)) {
            this.description = description;
        }
        if (!body.equals(EMPTY_STRING)) {
            this.body = body;
        }
        this.updatedAt = new DateTime();
    }

    public static String toSlug(final String title) {
        return transliterateCyrToLat(title.toLowerCase()).replaceAll("[\\&|[\\uFE30-\\uFFA0]|\\’|\\”|\\s\\?\\,\\.]+", "-");
    }

    private static String transliterateCyrToLat(final String str) {
        if (!str.matches(".*[" + Arrays.toString(cyrChars) + "].*")) return str;
        final var builder = new StringBuilder(str);
        for (int i = 0; i < str.length(); i++) {
            for (int translitIndex = 0; translitIndex < cyrChars.length; translitIndex++) {
                if (builder.charAt(i) == cyrChars[translitIndex]) {
                    builder.replace(i, (i + 1), latChars[translitIndex]);
                }
            }
        }
        return builder.toString();
    }
}
