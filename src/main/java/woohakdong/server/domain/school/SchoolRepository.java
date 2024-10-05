package woohakdong.server.domain.school;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Long> {
    School findBySchoolName(String schoolName);
}
