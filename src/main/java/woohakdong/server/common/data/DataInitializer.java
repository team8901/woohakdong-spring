package woohakdong.server.common.data;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import woohakdong.server.domain.admin.adminAccount.AdminAccount;
import woohakdong.server.domain.admin.adminAccount.AdminAccountRepository;
import woohakdong.server.domain.school.School;
import woohakdong.server.domain.school.SchoolRepository;

@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final SchoolRepository schoolRepository;
    private final AdminAccountRepository adminAccountRepository;

    @Override
    public void run(String... args) throws Exception {
        if (schoolRepository.count() == 0) {
            schoolRepository.save(School.builder()
                    .schoolName("아주대학교")
                    .schoolDomain("ajou.ac.kr")
                    .build());
        }

        if (adminAccountRepository.count() == 0) {
            adminAccountRepository.save(AdminAccount.builder()
                    .adminAccountBankCode("011")
                    .adminAccountAmount(10000000L)
                    .adminAccountBankName("농협은행")
                    .adminAccountNumber("3020000011527")
                    .build());
        }
    }
}
