package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler; // Import this handler

import com.example.demo.repository.UserRepository;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;
    
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.example.demo.model.User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在: " + username);
            }
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        };
    }
    
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        // Explicitly configure the CSRF request handler (though it's often the default)
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        // The default header name expected by this handler when using CookieCsrfTokenRepository is X-XSRF-TOKEN
        // requestHandler.setCsrfRequestAttributeName("_csrf"); // Default attribute name in request
        // requestHandler.setHeaderName("X-XSRF-TOKEN"); // Default header name to check

        http
            // Configure CSRF
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Store token in a cookie accessible by JS
                .csrfTokenRequestHandler(requestHandler) // Explicitly set the handler
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/","/home","/auth/login","/auth/register","/auth/perform-register","/hello", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")             // 自定义登录页面的路径
                .loginProcessingUrl("/auth/perform-login")  // 处理登录表单提交的URL
                .defaultSuccessUrl("/home")      // 登录成功后如果没有之前的请求页面则跳转到/home
                .failureUrl("/auth/login?error")      // 登录失败后跳转的URL
                .permitAll()                     // 允许所有用户访问登录页面
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/home")
                .permitAll()
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
