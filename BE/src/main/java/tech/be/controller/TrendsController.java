package tech.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.be.dto.TrendsCardDto;
import tech.be.model.Category;
import tech.be.service.TrendsService;

@RestController
@RequestMapping("/api/trends")
@RequiredArgsConstructor
public class TrendsController {

    private final TrendsService trendsService;

    /**
     * GET /api/trends?page=0&size=20
     * GET /api/trends?category=AI&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<TrendsCardDto>> getCards(
            @RequestParam(required = false) Category category,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<TrendsCardDto> result = category == null
                ? trendsService.getCards(pageable)
                : trendsService.getCardsByCategory(category, pageable);

        return ResponseEntity.ok(result);
    }
}
