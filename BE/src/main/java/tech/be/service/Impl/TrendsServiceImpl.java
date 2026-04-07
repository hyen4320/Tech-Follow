package tech.be.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.be.dto.TrendsCardDto;
import tech.be.model.Category;
import tech.be.repository.TrendsRepository;
import tech.be.service.TrendsService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrendsServiceImpl implements TrendsService {

    private final TrendsRepository trendsRepository;

    @Override
    public Page<TrendsCardDto> getCards(Pageable pageable) {
        return trendsRepository.findAllWithSources(pageable)
                .map(TrendsCardDto::from);
    }

    @Override
    public Page<TrendsCardDto> getCardsByCategory(Category category, Pageable pageable) {
        return trendsRepository.findByCategoryWithSources(category, pageable)
                .map(TrendsCardDto::from);
    }
}
