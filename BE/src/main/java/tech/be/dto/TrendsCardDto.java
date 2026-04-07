package tech.be.dto;

import tech.be.model.Category;
import tech.be.model.Trends;

import java.time.LocalDateTime;

public record TrendsCardDto(
        Long id,
        String title,
        String sourceName,
        String originUrl,
        Category category,
        LocalDateTime publishedAt,
        LocalDateTime collectedAt
) {
    public static TrendsCardDto from(Trends t) {
        return new TrendsCardDto(
                t.getId(),
                t.getTitle(),
                t.getSources() != null ? t.getSources().getName() : null,
                t.getOriginUrl(),
                t.getCategory(),
                t.getPublishedAt(),
                t.getCollectedAt()
        );
    }
}
