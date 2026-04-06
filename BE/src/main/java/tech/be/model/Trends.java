package tech.be.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//기사
public class Trends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Sources sources;
    private String title;
    private String text;
    private String content;
    //private tsvector searchVec;
    private String origin_url;
    @Enumerated(EnumType.STRING)
    private Category category;
    private LocalDateTime published_at;
    private LocalDateTime collected_at;


}
