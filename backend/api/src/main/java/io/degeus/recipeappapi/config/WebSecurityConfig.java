package io.degeus.recipeappapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String BCRYPT_PREFIX = "bcrypt";
    public static final int USER_PASSWORD_ENCODER_BCRYPT_STRENGTH = 10;

    private final UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http
            .csrf()
                .disable()
            .httpBasic()
                .and()
            .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/recipes")
                .permitAll()
                .and()
            .authorizeRequests()
                .anyRequest()
                .authenticated();
        //@formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    public DelegatingPasswordEncoder delegatingPasswordEncoder() {
        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(BCRYPT_PREFIX, passwordEncodersMap());
        return delegatingPasswordEncoder;
    }

    /* For password encoding alternatives, see e.g. https://spring.io/blog/2017/11/01/spring-security-5-0-0-rc1-released#password-storage-format */
    @SuppressWarnings({"unchecked", "deprecation"}) // we control what went in, so ok
    @Bean
    public Map<String, PasswordEncoder> passwordEncodersMap() {
        Map encoders = new HashMap<>();
        encoders.put(BCRYPT_PREFIX, new BCryptPasswordEncoder(USER_PASSWORD_ENCODER_BCRYPT_STRENGTH));
        return encoders;
    }
}
