package woohakdong.server.domain.admin.adminAccount;

public interface AdminAccountRepository {
    AdminAccount save(AdminAccount adminAccount);

    AdminAccount getById(Long adminAccountId);

    AdminAccount getFirstOne();
}
