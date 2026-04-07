package tech.be.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sources", uniqueConstraints = @UniqueConstraint(columnNames = "base_url"))
public class Sources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // 사람이 읽을 수 있는 사이트명 ex) DeepMind Blog

    private String baseUrl;     // 크롤링 기준 URL

    @Enumerated(EnumType.STRING)
    private Category category;  // AI | BE | SECURITY

    @Enumerated(EnumType.STRING)
    private Type type;          // BLOG | NEWS | RSS

    @Builder.Default
    private boolean active = true; // false 시 크롤링 스킵
}
