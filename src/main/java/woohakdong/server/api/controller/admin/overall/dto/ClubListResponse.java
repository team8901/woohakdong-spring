package woohakdong.server.api.controller.admin.overall.dto;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.school.School;

@Builder
public record ClubListResponse(
        Long clubId,
        String clubName,
        String clubEnglishName,
        String clubDescription,
        String clubImage,
        String clubRoom,
        String clubGeneration,
        String clubGroupChatLink,
        String clubGroupChatPassword,
        Integer clubDues,
        String schoolName
) {
    public static ClubListResponse from(Club club, School school) {
        return ClubListResponse.builder()
                .clubId(club.getClubId())
                .clubName(club.getClubName())
                .clubEnglishName(club.getClubEnglishName())
                .clubDescription(club.getClubDescription())
                .clubImage(club.getClubImage())
                .clubRoom(club.getClubRoom())
                .clubGeneration(club.getClubGeneration())
                .clubGroupChatLink(club.getClubGroupChatLink())
                .clubGroupChatPassword(club.getClubGroupChatPassword())
                .clubDues(club.getClubDues())
                .schoolName(school.getSchoolName())
                .build();
    }
}
