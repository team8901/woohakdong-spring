package woohakdong.server.api.controller.club.dto;

import lombok.Builder;
import woohakdong.server.domain.gathering.Gathering;

@Builder
public record ClubJoinGatheringInfoResponse(
        Long gatheringId,
        String gatheringName,
        String gatheringLink,
        String gatheringDescription,
        Integer gatheringAmount
) {

    public static ClubJoinGatheringInfoResponse from(Gathering gathering) {
        return ClubJoinGatheringInfoResponse.builder()
                .gatheringId(gathering.getGatheringId())
                .gatheringName(gathering.getGatheringName())
                .gatheringLink(gathering.getGatheringLink())
                .gatheringDescription(gathering.getGatheringDescription())
                .gatheringAmount(gathering.getGatheringAmount())
                .build();
    }
}
