package tech.be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.be.dto.TrendsCardDto;
import tech.be.model.Category;

@Service
public interface TrendsService {

    /** 전체 목록 (페이지네이션) */
    Page<TrendsCardDto> getCards(Pageable pageable);

    /** 카테고리 필터 */
    Page<TrendsCardDto> getCardsByCategory(Category category, Pageable pageable);
}
