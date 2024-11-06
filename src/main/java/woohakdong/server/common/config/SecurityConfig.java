package woohakdong.server.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import woohakdong.server.common.security.jwt.JWTFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //csrf disable
        http.csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http.formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());

        // cors 설정 disable
        http.cors(Customizer.withDefaults());

        //권한 설정
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/v1/auth/login/social", "/v1/auth/refresh", "/health-check").permitAll()
                .requestMatchers("/v1/groups/payment/webhook").permitAll()
                .requestMatchers("/v1/admin/auth/login").permitAll()
                .requestMatchers(getSwaggerUIPath()).permitAll()
                .requestMatchers("/v1/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        //세션 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    private String[] getSwaggerUIPath() {
        return new String[]{"/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs"};
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
