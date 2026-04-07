package tech.be.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        // 매일 9시 중복 체크용: WHERE collected_at >= :since
        @Index(name = "idx_trends_collected_at", columnList = "collected_at")
        // content IS NULL 부분 인덱스는 JPA 미지원 — 아래 DDL 을 직접 실행할 것
        // CREATE INDEX idx_trends_content_null ON trends (id) WHERE content IS NULL;
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Sources sources;

    @Column(columnDefinition = "TEXT")
    private String title;       // 기사 제목

    @Column(columnDefinition = "TEXT")
    private String text;        // 미사용 필드 (추후 제거 예정)

    @Column(columnDefinition = "TEXT")
    private String content;     // 본문 전문

    @Column(columnDefinition = "TEXT", unique = true)
    private String originUrl;   // 원본 URL (unique — DB 레벨 중복 방지)

    @Enumerated(EnumType.STRING)
    private Category category;

    private LocalDateTime publishedAt;
    private LocalDateTime collectedAt;
}
