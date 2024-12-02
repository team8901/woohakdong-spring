package woohakdong.server.api.service.group;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.common.util.security.SecurityUtil;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.group.Group;
import woohakdong.server.domain.group.GroupRepository;
import woohakdong.server.domain.groupmember.GroupMemberRepository;
import woohakdong.server.domain.member.Member;

@Service
@RequiredArgsConstructor
public class GroupServiceTrans {

    private final SecurityUtil securityUtil;
    private final DateUtil dateUtil;

    private final ClubMemberRepository clubMemberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void processJoinGroup(Long groupId, LocalDate date) {
        Member member = securityUtil.getMember();
        Group group = groupRepository.getById(groupId);
        Club club = group.getClub();

        // 클럽에 속한 멤버인지 확인
        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member,
                dateUtil.getAssignedTerm(date));

        // 그룹에 속한 멤버인지 확인
        if (groupMemberRepository.checkAlreadyJoined(group, clubMember)) {
            throw new CustomException(CustomErrorInfo.CLUB_GROUP_ALREADY_JOINED);
        }

        group.joinNewMember(clubMember);
        groupRepository.save(group);
        groupRepository.flush();
    }
}
