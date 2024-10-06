package woohakdong.server.common.config;

import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
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

        return new OpenAPI()
                .components(components)
                .info(info);
    }
}
