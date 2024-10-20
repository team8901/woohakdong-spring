package woohakdong.server.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 설정
                .allowedOrigins(getAllowedOrigins())
                .allowedMethods("*") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 인증 관련 정보 허용 (쿠키 등)
    }

    private String[] getAllowedOrigins() {
        return new String[]{
                "https://d1r973zpg8hnu4.cloudfront.net",
                "http://localhost:3000",
                "http://localhost",
                "http://52.78.100.19",
                "http://52.78.48.223",
                "http://52.78.5.241"
        };
    }
}
