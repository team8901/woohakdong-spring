package woohakdong.server.domain.school;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SchoolJpaRepository extends JpaRepository<School, Long> {
    Optional<School> findBySchoolDomain(String schoolDomain);
    Long countByCreatedAtAfter(LocalDateTime date);
    List<School> findByCreatedAtAfter(LocalDateTime date);
}
