package woohakdong.server.common.util.date;

import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class DateUtilImpl implements DateUtil {

    @Override
    public LocalDate getAssignedTerm(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int semester;
        if (month >= 3 && month <= 8) {
            // 1학기 : 3월 1일 ~ 8월 31일
            semester = 3;
        } else {
            // 2학기 : 9월 1일 ~ 2월 28일
            semester = 9;
            if (month <= 2) {
                // 2학기 2월 이전은 전년도 2학기
                year--;
            }
        }
        return LocalDate.of(year, semester, 1);
    }
}
