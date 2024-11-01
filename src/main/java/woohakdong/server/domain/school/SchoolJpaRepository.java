package woohakdong.server.domain.school;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolJpaRepository extends JpaRepository<School, Long> {
    Optional<School> findBySchoolDomain(String schoolDomain);
}
