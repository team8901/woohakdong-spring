package woohakdong.server.domain.admin.adminAccount;

import static woohakdong.server.common.exception.CustomErrorInfo.ADMIN_ACCOUNT_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import woohakdong.server.common.exception.CustomErrorInfo;
import woohakdong.server.common.exception.CustomException;

@RequiredArgsConstructor
@Repository
public class AdminAccountRepositoryImpl implements AdminAccountRepository{

    private final AdminAccountJpaRepository adminAccountJpaRepository;

    @Override
    public AdminAccount save(AdminAccount adminAccount) {
        return adminAccountJpaRepository.save(adminAccount);
    }

    @Override
    public AdminAccount getById(Long adminAccountId) {
        return adminAccountJpaRepository.findById(adminAccountId)
                .orElseThrow(() -> new CustomException(ADMIN_ACCOUNT_NOT_FOUND));
    }
}
