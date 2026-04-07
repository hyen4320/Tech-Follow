package tech.be.service;

import org.springframework.stereotype.Service;
import tech.be.model.Sources;

@Service
public interface SourcesService {
    void addSource(Sources sources);
    void updateSource(Sources sources);
    void deleteSource(Long id);



}
