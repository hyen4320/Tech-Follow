package tech.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.be.model.Users;
@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
}
