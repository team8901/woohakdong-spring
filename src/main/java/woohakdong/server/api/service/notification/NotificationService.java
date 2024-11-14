package woohakdong.server.api.service.notification;

import static woohakdong.server.common.exception.CustomErrorInfo.MEMBER_NOT_FOUND;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.service.email.EmailService;
import woohakdong.server.common.exception.CustomException;
import woohakdong.server.common.security.jwt.CustomUserDetails;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.member.MemberRepository;
import woohakdong.server.domain.schedule.Schedule;
import woohakdong.server.domain.schedule.ScheduleRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    public static final DateTimeFormatter YEAR_MONTH_DAY_HOUR = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");

    private final EmailService emailService;

    private final ClubRepository clubRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final MemberRepository memberRepository;

    public void sendNotificationWithClubInfoUpdate(Long clubId, LocalDate assignedTerm) {
        getMemberFromJwtInformation(); // TODO : 임원 체크
        Club club = clubRepository.getById(clubId);
        List<ClubMember> clubMembers = clubMemberRepository.getAllBySearchFilter(club, null, assignedTerm);

        clubMembers.stream()
                .map(ClubMember::getMember)
                .forEach(member -> emailService.sendEmailForUpdateClubInfo(
                        member.getMemberName(),
                        member.getMemberEmail(),
                        club.getClubName(),
                        club.getClubDescription(),
                        club.getClubGroupChatLink(),
                        club.getClubGroupChatPassword()
                ));
    }

    public void sendNotificationWithSchedule(Long clubId, Long scheduleId, LocalDate assignedTerm) {
        getMemberFromJwtInformation(); // TODO : 임원 체크
        Club club = clubRepository.getById(clubId);
        List<ClubMember> clubMembers = clubMemberRepository.getAllBySearchFilter(club, null, assignedTerm);
        Schedule schedule = scheduleRepository.getById(scheduleId);
        String scheduleDate = schedule.getScheduleDateTime().format(YEAR_MONTH_DAY_HOUR);

        clubMembers.stream()
                .map(ClubMember::getMember)
                .forEach(member -> emailService.sendEmailForSchedule(
                        member.getMemberName(),
                        member.getMemberEmail(),
                        club.getClubName(),
                        schedule.getScheduleTitle(),
                        schedule.getScheduleContent(),
                        scheduleDate
                ));
    }

    private Member getMemberFromJwtInformation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String provideId = userDetails.getUsername();

        return memberRepository.findByMemberProvideId(provideId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }
}
