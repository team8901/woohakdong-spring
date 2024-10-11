package woohakdong.server.api.controller.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import woohakdong.server.api.controller.member.dto.CreateMemberRequest;
import woohakdong.server.api.controller.member.dto.MemberInfoResponse;
import woohakdong.server.api.service.member.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/info")
    public void createMember(@Valid CreateMemberRequest createMemberRequest) {

        memberService.createMember(createMemberRequest);
    }

    @GetMapping("/info")
    public MemberInfoResponse getMemberInfo() {

        return memberService.getMemberInfo();
    }
}
