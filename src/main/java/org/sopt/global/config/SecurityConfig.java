package org.sopt.global.config;

import org.sopt.domain.member.repository.MemberRepository;
import org.sopt.global.jwt.JwtAuthenticationFilter;
import org.sopt.global.jwt.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    public SecurityConfig(JwtProvider jwtProvider, MemberRepository memberRepository) {
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF 비활성화, Authorization 헤더로 보내면 CSRF의 전제 조건이 없기 때문
                .csrf(AbstractHttpConfigurer::disable)
                // 폼 로그인, HTTP Basic 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 세션 STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그아웃은 인증된 회원만 접근
                        .requestMatchers(HttpMethod.POST, "/auth/logout")
                        .authenticated()

                        // 회원가입, 로그인, 재발급은 인증 없이 접근
                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/signup",
                                "/auth/login",
                                "/auth/reissue"
                        ).permitAll()

                        // 회원 탈퇴 접근
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/members/me")
                        .authenticated()

                        // 게시글 조회는 인증 없이 접근 허용
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/**")
                        .permitAll()

                        // 나머지 API는 인증 필요
                        .anyRequest().authenticated()
                )
                // 인증 실패 시 401 응답
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // JWT 인증 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, memberRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

