package tech.be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.be.model.Category;
import tech.be.model.Trends;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface TrendsRepository extends JpaRepository<Trends, Long> {

    // 카드 목록 — sources fetch join, 최신순 정렬
    @Query("SELECT t FROM Trends t JOIN FETCH t.sources ORDER BY t.collectedAt DESC")
    Page<Trends> findAllWithSources(Pageable pageable);

    // 카테고리 필터 카드 목록
    @Query("SELECT t FROM Trends t JOIN FETCH t.sources WHERE t.category = :category ORDER BY t.collectedAt DESC")
    Page<Trends> findByCategoryWithSources(@Param("category") Category category, Pageable pageable);

    // 중복 방지 (단건)
    boolean existsByOriginUrl(String originUrl);

    // 최근 30일 이내 수집된 URL 만 로드 — 전체 조회 대신 사용
    @Query("SELECT t.originUrl FROM Trends t WHERE t.collectedAt >= :since")
    Set<String> findRecentOriginUrls(@Param("since") LocalDateTime since);

    // 본문 미수집 목록 조회 - Sources fetch join (LazyInitializationException 방지)
    @Query("SELECT t FROM Trends t JOIN FETCH t.sources WHERE t.content IS NULL")
    List<Trends> findByContentIsNull();

    // 번역 미완료 목록 조회 (본문은 있으나 번역 없는 것)
    @Query("SELECT t FROM Trends t WHERE t.content IS NOT NULL AND t.contentKo IS NULL")
    List<Trends> findByContentIsNotNullAndContentKoIsNull();
}
