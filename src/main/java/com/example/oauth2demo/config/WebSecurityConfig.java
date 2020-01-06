package com.example.oauth2demo.config;

import com.example.oauth2demo.entity.User;
import com.example.oauth2demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final OAuth2ClientContext oAuth2ClientContext;
    private final UserService userService;
    private final PasswordEncoder encoder;

    @Autowired
    public WebSecurityConfig(@Qualifier("oauth2ClientContext") OAuth2ClientContext oAuth2ClientContext,
                             UserService userService,
                             PasswordEncoder encoder) {
        this.oAuth2ClientContext = oAuth2ClientContext;
        this.userService = userService;
        this.encoder = encoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/login**").permitAll()
                    .antMatchers("/registration").permitAll()
                    .antMatchers("/webjars/**").permitAll()
                    .antMatchers("/js/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                .and()
                    .logout()
                    .logoutSuccessUrl("/")
                    .permitAll()
                .and()
                    .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
        //@formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(encoder);
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        OAuth2ClientAuthenticationProcessingFilter gitHubFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/gitHub");
        OAuth2RestTemplate gitHubTemplate = new OAuth2RestTemplate(gitHub(), oAuth2ClientContext);
        gitHubFilter.setRestTemplate(gitHubTemplate);
        UserInfoTokenServices gitHubTokenServices = new UserInfoTokenServices(gitHubResource().getUserInfoUri(), gitHub().getClientId());
        gitHubTokenServices.setRestTemplate(gitHubTemplate);
        gitHubTokenServices.setPrincipalExtractor(gitHubPrincipalExtractor());
        gitHubFilter.setTokenServices(gitHubTokenServices);
        filters.add(gitHubFilter);

        OAuth2ClientAuthenticationProcessingFilter googleFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/google");
        OAuth2RestTemplate googleTemplate = new OAuth2RestTemplate(google(), oAuth2ClientContext);
        googleFilter.setRestTemplate(googleTemplate);
        UserInfoTokenServices googleTokenServices = new UserInfoTokenServices(googleResource().getUserInfoUri(), google().getClientId());
        googleTokenServices.setRestTemplate(googleTemplate);
        googleTokenServices.setPrincipalExtractor(googlePrincipalExtractor());
        googleFilter.setTokenServices(googleTokenServices);
        filters.add(googleFilter);

        filter.setFilters(filters);
        return filter;
    }

    @Bean
    public PrincipalExtractor gitHubPrincipalExtractor() {
        return (map) -> {
            String login = (String) map.get("login");
            String email = (String) map.get("email");
            String avatarUrl = (String) map.get("avatar_url");
            return new User(login, email, avatarUrl);
        };
    }

    @Bean
    public PrincipalExtractor googlePrincipalExtractor() {
        return (map -> {
            String login = (String) map.get("name");
            String email = (String) map.get("email");
            String avatarUrl = (String) map.get("picture");
            return new User(login, email, avatarUrl);
        });
    }

    @Bean
    @ConfigurationProperties("google.client")
    public AuthorizationCodeResourceDetails google() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("google.resource")
    public ResourceServerProperties googleResource() {
        return new ResourceServerProperties();
    }

    @Bean
    @ConfigurationProperties("github.client")
    public AuthorizationCodeResourceDetails gitHub() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("github.resource")
    public ResourceServerProperties gitHubResource() {
        return new ResourceServerProperties();
    }

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }
}
