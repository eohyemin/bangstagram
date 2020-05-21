package com.bangstagram.user.configure.security;

import com.bangstagram.user.domain.model.oauth.kakao.KakaoLoginApi;
import com.bangstagram.user.domain.model.oauth.kakao.KakaoProfileApi;
import com.bangstagram.user.domain.model.oauth.naver.NaverLoginApi;
import com.bangstagram.user.domain.model.oauth.naver.NaverProfileApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/***
 * author: Hyo-Jin Kim
 * Date: 2020.05.01
 */

@Configuration
@EnableConfigurationProperties({NaverProperty.class, KakaoProperty.class, JwtProperty.class})
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {
    private final JwtProperty jwtProperty;

    private final NaverProperty naverProperty;

    private final KakaoProperty kakaoProperty;

    public WebSecurityConfigure(JwtProperty jwtProperty, NaverProperty naverProperty, KakaoProperty kakaoProperty) {
        this.jwtProperty = jwtProperty;
        this.naverProperty = naverProperty;
        this.kakaoProperty = kakaoProperty;
    }

    @Bean
    public JWT jwt() {
        return new JWT(jwtProperty.getIssuer(), jwtProperty.getSecret(), jwtProperty.getExpirySeconds());
    }

    @Bean
    public NaverLoginApi naverLoginApi() {
        return new NaverLoginApi(naverProperty.getNaverClientId(),naverProperty.getNaverClientSecret(),naverProperty.getNaverTokenRequestUrl());
    }

    @Bean
    public NaverProfileApi naverProfileApi() {
        return new NaverProfileApi(naverProperty.getNaverProfileRequestUrl());
    }

    @Bean
    public KakaoLoginApi kakaoLoginApi() {
        return new KakaoLoginApi(kakaoProperty.getKakaoClientId(),kakaoProperty.getKakaoLoginTokenUrl());
    }

    @Bean
    public KakaoProfileApi kakaoProfileApi() {
        return new KakaoProfileApi(kakaoProperty.getKakaoProfileInfoUrl());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .headers()
                .disable()
                .exceptionHandling()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/users/join").permitAll()
                .antMatchers("/users/exists").permitAll()
                .antMatchers("/users/login").permitAll()
                .antMatchers("/oauth/naver").permitAll()
                .antMatchers("/oauth/kakao").permitAll()
                .antMatchers("/rooms/**").permitAll()
                .antMatchers("/**").hasRole("USER_ROLE")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .disable();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/swagger-resources", "/webjars/**", "/templates/**", "/h2/**");
    }

}