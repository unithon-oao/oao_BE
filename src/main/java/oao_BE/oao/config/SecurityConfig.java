package oao_BE.oao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity // Spring Security 활성화
public class SecurityConfig {

    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (API 서버이므로)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // CORS 설정 적용

        // 다른 보안 설정들...

        return http.build();
    }

    // CORS 정책을 정의하는 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 오리진(Origin)을 설정합니다.
        config.setAllowedOrigins(List.of(
                "http://localhost:5175"
        ));

        // 허용할 HTTP 메서드를 설정합니다.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 허용할 헤더를 설정합니다. (인증 관련 헤더 포함)
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // 쿠키와 같은 인증 정보를 포함한 요청을 허용할지 설정합니다.
        config.setAllowCredentials(true);

        // 모든 경로(/**)에 대해 위에서 설정한 CORS 정책을 적용합니다.
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}