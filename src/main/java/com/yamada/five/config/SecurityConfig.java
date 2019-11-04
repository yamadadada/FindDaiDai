package com.yamada.five.config;

import com.yamada.five.filter.AuthenticationFilter;
import com.yamada.five.handler.MyAuthenticationFailureHandler;
import com.yamada.five.handler.MyAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProvider provider;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MyAuthenticationSuccessHandler successHandler;

    @Autowired
    private MyAuthenticationFailureHandler failureHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

//        自定义拦截器用户图形验证码验证
        AuthenticationFilter myFilter = new AuthenticationFilter("/login/form", failureHandler);

//        super.configure(http);
        http
                .addFilterBefore(myFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                .usernameParameter("studentId").loginPage("/login").loginProcessingUrl("/login/form")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .and()
                .authorizeRequests()
                .antMatchers(// 允许对于网站静态资源的无授权访问
                        "/toRegistered",
                        "/signup",
                        "/js/**",
                        "/images/**",
                        "/login",
                        "/search**",
                        "/sms/**",
                        "/me/toUpdatePassword",
                        "/me/updatePassword",
                        "/imageCode"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .rememberMe().tokenValiditySeconds(3600)
                .tokenRepository(persistentTokenRepository())
                .and()
                .logout()
                .logoutSuccessUrl("/logoutSuccess").permitAll()
                .and()
                .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(provider);
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
