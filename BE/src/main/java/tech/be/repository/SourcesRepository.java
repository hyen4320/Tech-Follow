package tech.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.be.model.Sources;
@Repository
public interface SourcesRepository extends JpaRepository<Sources,Long> {
}
