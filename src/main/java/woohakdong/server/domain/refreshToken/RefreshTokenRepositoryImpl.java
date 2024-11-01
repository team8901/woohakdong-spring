package woohakdong.server.domain.refreshToken;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository{

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenJpaRepository.save(refreshToken);
    }

    @Override
    public Boolean existsByRefresh(String refresh) {
        return refreshTokenJpaRepository.existsByRefresh(refresh);
    }

    @Override
    public void deleteByRefresh(String refresh) {
        refreshTokenJpaRepository.deleteByRefresh(refresh);
    }
}
