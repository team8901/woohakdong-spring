package woohakdong.server.domain.school;

import java.time.LocalDateTime;
import java.util.List;

public interface SchoolRepository {
    School save(School school);

    School getById(Long schoolId);

    School getBySchoolDomain(String schoolDomain);

    long count();

    List<School> getAll();

    Long countByCreatedAtBefore(LocalDateTime date);

    List<School> getByCreatedAtAfter(LocalDateTime date);
}
