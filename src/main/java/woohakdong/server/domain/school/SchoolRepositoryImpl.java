package woohakdong.server.domain.school;

import static woohakdong.server.common.exception.CustomErrorInfo.INVALID_SCHOOL_DOMAIN;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomException;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class SchoolRepositoryImpl implements SchoolRepository{

    private final SchoolJpaRepository schoolJpaRepository;

    @Override
    public School save(School school) {
        return schoolJpaRepository.save(school);
    }

    @Override
    public School getBySchoolDomain(String schoolDomain) {
        return schoolJpaRepository.findBySchoolDomain(schoolDomain)
                .orElseThrow(() -> new CustomException(INVALID_SCHOOL_DOMAIN));
    }

    @Override
    public long count() {
        return schoolJpaRepository.count();
    }

    @Override
    public School getById(Long schoolId) {
        return schoolJpaRepository.findById(schoolId)
                .orElseThrow(() -> new CustomException(INVALID_SCHOOL_DOMAIN));
    }

    @Override
    public List<School> getAll() {
        return schoolJpaRepository.findAll();
    }

    @Override
    public Long countByCreatedAtAfter(LocalDateTime date) {
        return schoolJpaRepository.countByCreatedAtAfter(date);
    }

    @Override
    public List<School> getByCreatedAtAfter(LocalDateTime date) {
        return schoolJpaRepository.findByCreatedAtAfter(date);
    }
}
