package woohakdong.server.domain.refreshToken;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshId;

    private String refreshProvideId;
    private String refresh;
    private String RefreshExpiration;

    @Builder
    public RefreshToken(String refreshProvideId, String refresh, String refreshExpiration) {
        this.refreshProvideId = refreshProvideId;
        this.refresh = refresh;
        this.RefreshExpiration = refreshExpiration;
    }

    public static RefreshToken create(String provideId, String refresh, String expiration) {
        return RefreshToken.builder()
                .refreshProvideId(provideId)
                .refresh(refresh)
                .refreshExpiration(expiration)
                .build();
    }
}
