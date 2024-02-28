package com.encore.ordering.securities;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//토큰 설정

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 권한관리 ROLE_ADMIN 관리자일 때 들어갈 수 있는 페이지 권한관리
// pre : 사전, post : 사후, 사전/사후에 인증/권한 검사 어노테이션 사용가능
public class SecurityConfig {

    private final JwtAuthFilter authFilter;

    public SecurityConfig(JwtAuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean // Filter
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
//                csrf보안공격에 대한 설정은 하지 않겠다라는 의미
//                xss와 csrf의 차이 정리 필요
                .csrf().disable()
                .cors().and() // CORS 활성화
                .httpBasic().disable()
                .authorizeRequests()
//                인증 미적용 url 패턴 작성
                    .antMatchers("/member/create", "/doLogin", "/items", "/item/*/image")
                    .permitAll()
//                그외 요청은 모두 인증필요
                .anyRequest().authenticated()
                .and()
//                세션을 사용하지 않겠다라는 설정 추가
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class) //authFilter 사용
//                커스텀한 Filter를 사용하면 무조건 Filter를 타게된다.
                .build();
    }
}
