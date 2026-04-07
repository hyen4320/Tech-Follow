package tech.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.be.model.Bookmarks;
@Repository
public interface BookMarksRepository extends JpaRepository<Bookmarks,Long> {
}
