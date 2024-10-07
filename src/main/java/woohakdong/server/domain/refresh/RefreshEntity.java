package woohakdong.server.domain.refresh;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RefreshEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshId;

    private String refreshProvideId;
    private String refresh;
    private String RefreshExpiration;

    @Builder
    public RefreshEntity(String refreshProvideId, String refresh, String refreshExpiration) {
        this.refreshProvideId = refreshProvideId;
        this.refresh = refresh;
        this.RefreshExpiration = refreshExpiration;
    }
}