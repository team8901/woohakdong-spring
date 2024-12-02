package woohakdong.server;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import woohakdong.server.config.WithMockCustomUser;
import woohakdong.server.config.WithoutRedisConfig;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@WithMockCustomUser
@WithoutRedisConfig
public abstract class RepositoryTestSetup {

}
