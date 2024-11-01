package woohakdong.server.domain.refreshToken;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);

    Boolean existsByRefresh(String refresh);

    void deleteByRefresh(String refresh);
}
