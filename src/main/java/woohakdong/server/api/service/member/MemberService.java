package woohakdong.server.api.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.controller.member.dto.CreateMemberRequest;
import woohakdong.server.api.controller.member.dto.MemberInfoResponse;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;

import static woohakdong.server.common.exception.CustomErrorInfo.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public void createMember(CreateMemberRequest createMemberRequest) {
        // SecurityContextHolder에서 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        // 사용자 정보로 회원 조회
        Member existingMember = memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        // 기존 멤버의 정보 업데이트
        existingMember.setMemberPhoneNumber(createMemberRequest.memberPhoneNumber());
        existingMember.setMemberMajor(createMemberRequest.memberMajor());
        existingMember.setMemberStudentNumber(createMemberRequest.memberStudentNumber());
        existingMember.setMemberGender(createMemberRequest.memberGender());

        // 업데이트된 멤버 정보를 다시 저장
        memberRepository.save(existingMember);
    }

    public MemberInfoResponse getMemberInfo() {
        // SecurityContextHolder에서 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        // 사용자 정보로 회원 조회
        Member member = memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        // Member 엔티티를 MemberInfoResponse로 변환
        return MemberInfoResponse.builder()
                .memberName(member.getMemberName())
                .memberPhoneNumber(member.getMemberPhoneNumber())
                .memberEmail(member.getMemberEmail())
                .memberSchool(member.getSchool().getSchoolName())
                .memberMajor(member.getMemberMajor())
                .memberStudentNumber(member.getMemberStudentNumber())
                .memberGender(member.getMemberGender())
                .build();
    }
}
