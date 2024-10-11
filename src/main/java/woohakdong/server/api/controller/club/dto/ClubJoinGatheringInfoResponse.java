package woohakdong.server.api.controller.club.dto;

import lombok.Builder;

@Builder
public record ClubJoinGatheringInfoResponse(
        int gatheringId,
        String gatheringName,
        String gatheringLink,
        String gatheringDescription,
        int gatheringAmount
) {
}
