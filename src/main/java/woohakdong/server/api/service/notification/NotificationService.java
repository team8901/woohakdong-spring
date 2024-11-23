package woohakdong.server.api.service.notification;

import static woohakdong.server.domain.clubmember.ClubMemberRole.PRESIDENT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.api.service.email.EmailService;
import woohakdong.server.common.util.date.DateUtil;
import woohakdong.server.common.util.security.SecurityUtil;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.club.ClubRepository;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.clubmember.ClubMemberRepository;
import woohakdong.server.domain.itemBorrowed.ItemBorrowed;
import woohakdong.server.domain.itemBorrowed.ItemBorrowedRepository;
import woohakdong.server.domain.member.Member;
import woohakdong.server.domain.schedule.Schedule;
import woohakdong.server.domain.schedule.ScheduleRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    public static final DateTimeFormatter YEAR_MONTH_DAY_HOUR = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");

    private final EmailService emailService;
    private final SecurityUtil securityUtil;
    private final DateUtil dateUtil;

    private final ClubRepository clubRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final ItemBorrowedRepository itemBorrowedRepository;

    public void sendNotificationWithClubInfoUpdate(Long clubId, LocalDate date) {
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
        checkMemberRoleInClub(club, assignedTerm);
        List<ClubMember> clubMembers = clubMemberRepository.getAllBySearchFilter(club, null, assignedTerm);

        clubMembers.stream()
                .map(ClubMember::getMember)
                .forEach(m -> emailService.sendEmailForUpdateClubInfo(
                        m.getMemberName(),
                        m.getMemberEmail(),
                        club.getClubName(),
                        club.getClubDescription(),
                        club.getClubGroupChatLink(),
                        club.getClubGroupChatPassword()
                ));
    }

    public void sendNotificationWithSchedule(Long clubId, Long scheduleId, LocalDate date) {
        Club club = clubRepository.getById(clubId);
        LocalDate assignedTerm = dateUtil.getAssignedTerm(date);
        checkMemberRoleInClub(club, assignedTerm);
        List<ClubMember> clubMembers = clubMemberRepository.getAllBySearchFilter(club, null, assignedTerm);
        Schedule schedule = scheduleRepository.getById(scheduleId);
        String scheduleDate = schedule.getScheduleDateTime().format(YEAR_MONTH_DAY_HOUR);

        clubMembers.stream()
                .map(ClubMember::getMember)
                .forEach(m -> emailService.sendEmailForSchedule(
                        m.getMemberName(),
                        m.getMemberEmail(),
                        club.getClubName(),
                        schedule.getScheduleTitle(),
                        schedule.getScheduleContent(),
                        scheduleDate
                ));
    }

    @Transactional
    public void notifyOverdueItems() {
        LocalDateTime now = LocalDateTime.now();
        List<ItemBorrowed> overdueItems = itemBorrowedRepository.getByItemBorrowedReturnDateBefore(now);

        overdueItems.forEach(itemBorrowed -> emailService.sendOverdueNotification(
                itemBorrowed.getClubMember().getMember().getMemberName(),
                itemBorrowed.getClubMember().getMember().getMemberEmail(),
                itemBorrowed.getItem().getItemName(),
                itemBorrowed.getItem().getClub().getClubName(),
                itemBorrowed.getItemBorrowedReturnDate().toString()
        ));
    }

    private void checkMemberRoleInClub(Club club, LocalDate assignedTerm) {
        Member member = securityUtil.getMember();
        ClubMember clubMember = clubMemberRepository.getByClubAndMemberAndAssignedTerm(club, member, assignedTerm);
        clubMember.hasAuthorityOf(PRESIDENT);
    }
}
