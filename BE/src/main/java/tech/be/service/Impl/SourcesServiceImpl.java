package tech.be.service.Impl;

import lombok.RequiredArgsConstructor;
import tech.be.model.Sources;
import tech.be.repository.SourcesRepository;
import tech.be.service.SourcesService;

@RequiredArgsConstructor
public class SourcesServiceImpl implements SourcesService {
    private final SourcesRepository sourcesRepository;
    @Override
    public void addSource(Sources sources) {

    }

    @Override
    public void updateSource(Sources sources) {

    }

    @Override
    public void deleteSource(Long id) {

    }
}
