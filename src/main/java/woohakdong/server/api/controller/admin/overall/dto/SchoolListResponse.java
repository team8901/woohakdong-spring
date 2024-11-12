package woohakdong.server.api.controller.admin.overall.dto;

import lombok.Builder;
import woohakdong.server.domain.school.School;

@Builder
public record SchoolListResponse(
        Long schoolId,
        String schoolName,
        String schoolDomain
) {
    public static SchoolListResponse from(School school) {
        return SchoolListResponse.builder()
                .schoolId(school.getSchoolId())
                .schoolName(school.getSchoolName())
                .schoolDomain(school.getSchoolDomain())
                .build();
    }
}
