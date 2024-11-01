package woohakdong.server.domain.school;

public interface SchoolRepository {
    School save(School school);

    School getById(Long schoolId);

    School getBySchoolDomain(String schoolDomain);

    long count();
}
