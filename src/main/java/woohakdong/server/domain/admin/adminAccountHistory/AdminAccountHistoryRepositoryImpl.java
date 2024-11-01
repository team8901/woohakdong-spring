package woohakdong.server.domain.admin.adminAccountHistory;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AdminAccountHistoryRepositoryImpl implements AdminAccountHistoryRepository{

    private final AdminAccountHistoryJpaRepository adminAccountHistoryJpaRepository;

    @Override
    public AdminAccountHistory save(AdminAccountHistory adminAccountHistory) {
        return adminAccountHistoryJpaRepository.save(adminAccountHistory);
    }

    @Override
    public List<AdminAccountHistory> getAll() {
        return adminAccountHistoryJpaRepository.findAll();
    }
}
