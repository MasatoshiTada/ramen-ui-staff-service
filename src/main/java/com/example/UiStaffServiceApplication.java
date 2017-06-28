package com.example;

import org.apache.catalina.filters.RequestDumperFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableRedisHttpSession
@EnableBinding(Source.class)
public class UiStaffServiceApplication {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(UiStaffServiceApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.detectRequestFactory(true)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "request.dump.enable", havingValue = "true")
    RequestDumperFilter requestDumperFilter() {
        return new RequestDumperFilter();
    }

    @EnableWebSecurity
    static class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("tokyo").password("password").roles("STAFF").and()
                    .withUser("osaka").password("password").roles("STAFF");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement()
                    .sessionFixation().changeSessionId()
                    .sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
            http.formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/order", true)
                    .failureUrl("/login?error")
                    .usernameParameter("shopId")
                    .passwordParameter("password")
                    .permitAll();
            http.logout()
                    .logoutUrl("/logout")
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/login")
                    .permitAll();
            http.authorizeRequests()
                    .antMatchers("/resources/**").permitAll()
                    .anyRequest().authenticated();
        }
    }
}

