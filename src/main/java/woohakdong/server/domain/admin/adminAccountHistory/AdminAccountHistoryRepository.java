package woohakdong.server.domain.admin.adminAccountHistory;

import java.util.List;

public interface AdminAccountHistoryRepository {
    AdminAccountHistory save(AdminAccountHistory adminAccountHistory);

    List<AdminAccountHistory> getAll();
}
