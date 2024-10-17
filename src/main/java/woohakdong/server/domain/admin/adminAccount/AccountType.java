package woohakdong.server.domain.admin.adminAccount;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AccountType {

    DEPOSIT("입금"),
    WITHDRAW("출금");

    private final String type;
}
