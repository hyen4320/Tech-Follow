package tech.be.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//수집되는 곳
public class Sources {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String baseUrl;
    @Enumerated(EnumType.STRING)
    private Type type;
}
