package com.haelongit.devlog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 비밀번호 암호화를 위한 Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. CSRF 비활성화 (세션 방식에서는 고려해야 하지만, 여기서는 편의상 비활성화)
                .csrf(csrf -> csrf.disable())

                // 3. 요청별 접근 제어 설정
                .authorizeHttpRequests(auth -> auth
                        // '/api/user/register', '/api/user/login' 은 누구나 접근 가능
                        .requestMatchers("/api/user/register", "/api/user/login").permitAll()
                        // 나머지 모든 '/api/**' 요청은 인증된 사용자만 접근 가능
                        .requestMatchers("/api/**").authenticated()
                        // 그 외 모든 요청은 허용 (React 라우팅을 위함)
                        .anyRequest().permitAll()
                )

                // 4. 로그인 설정
                .formLogin(formLogin -> formLogin
                        // 로그인 API 경로 설정
                        .loginProcessingUrl("/api/user/login")
                        // 로그인 성공 시 처리
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                            response.getWriter().write("Login successful");
                        })
                        // 로그인 실패 시 처리
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.getWriter().write("Login failed");
                        })
                )

                // 5. 로그아웃 설정
                .logout(logout -> logout
                        // 로그아웃 API 경로 설정
                        .logoutUrl("/api/user/logout")
                        // 로그아웃 성공 시 처리
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                            response.getWriter().write("Logout successful");
                        })
                        // 세션 무효화 및 쿠키 삭제
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                // 6. 인증되지 않은 사용자의 API 접근 시 401 Unauthorized 반환
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );


        return http.build();
    }

    // React 앱(localhost:5173)과의 CORS 통신을 위한 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 쿠키를 포함한 요청을 허용하기 위한 설정
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
