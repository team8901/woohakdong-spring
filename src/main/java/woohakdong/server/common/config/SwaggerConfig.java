package woohakdong.server.common.config;

import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Woohakdong Server API")
                .description("우학동 서버 API 명세서")
                .version("0.0.1");

        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(Type.HTTP)
                .in(HEADER)
                .bearerFormat("JWT")
                .scheme("Bearer");

        Components components = new Components()
                .addSecuritySchemes("accessToken", securityScheme);

        Server httpsServer = new Server();
        httpsServer.setUrl("https://dev.woohakdong.com");
        httpsServer.setDescription("개발 서버");

        Server prodServer = new Server();
        prodServer.setUrl("https://api.woohakdong.com");
        prodServer.setDescription("운영 서버");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("로컬 서버");

        return new OpenAPI()
                .components(components)
                .servers(List.of(httpsServer, prodServer, localServer))
                .info(info);
    }
}