package ru.sberbank.card2card.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import ru.sberbank.card2card.security.jwt.JwtConfigurer;
import ru.sberbank.card2card.security.jwt.JwtTokenProvider;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String CARD_ENDPOINT = "/api/card/**";
    private static final String AUTH_ENDPOINT = "/api/auth/**";
    private static final String HISTORY_ENDPOINT = "/api/operations/history/**";

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers(AUTH_ENDPOINT).permitAll()
                .antMatchers(CARD_ENDPOINT).authenticated()
                .antMatchers(HISTORY_ENDPOINT).authenticated()
                .anyRequest().authenticated()
                .and().apply(new JwtConfigurer(jwtTokenProvider));
    }
}
