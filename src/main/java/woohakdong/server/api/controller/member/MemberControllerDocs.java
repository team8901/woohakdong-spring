package woohakdong.server.api.controller.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import woohakdong.server.api.controller.member.dto.CreateMemberRequest;
import woohakdong.server.api.controller.member.dto.MemberInfoResponse;

@Tag(name = "Member", description = "멤버 관련 API")
public interface MemberControllerDocs {

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "멤버 상세 정보 작성", description = "동아리원, 동아리 임원진 멤버 상세 정보 작성")
    @ApiResponse(responseCode = "200", description = "멤버 상세 정보 작성 성공", useReturnTypeSchema = true)
    public void createMember(CreateMemberRequest createMemberRequest);

    @SecurityRequirement(name = "accessToken")
    @Operation(summary = "멤버 인적사항 정보 확인", description = "동아리원, 동아리 임원진 멤버 인적사항 정보 확인")
    @ApiResponse(responseCode = "200", description = "멤버 인적사항 정보 확인 성공", useReturnTypeSchema = true)
    public MemberInfoResponse getMemberInfo();

}
