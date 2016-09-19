package com.janeullah.healthinspectionrecords.org.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: jane
 * Date:  9/18/2016
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * http://blog.netgloo.com/2014/09/28/spring-boot-enable-the-csrf-check-selectively-only-for-some-requests/
     * @param http HttpSecurity object
     * @throws Exception exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Build the request matcher for CSRF
        RequestMatcher csrfRequestMatcher = new RequestMatcher() {

            private RegexRequestMatcher requestMatcher =
                    new RegexRequestMatcher("/data/refreshPages", null);

            @Override
            public boolean matches(HttpServletRequest request) {

                // Enable the CSRF
                //if(requestMatcher.matches(request))
                //    return true;

                // You can add here any other rule on the request object, returning
                // true if the CSRF must be enabled, false otherwise
                // ....

                // No CSRF for other requests
                return false;
            }

        }; // new RequestMatcher

        http
                // Enable csrf only on some request matches
                .csrf()
                    .requireCsrfProtectionMatcher(csrfRequestMatcher)
                    .and()
                .authorizeRequests()
                    .antMatchers("/restaurants", "/restaurant/id/*","/data/refreshPages").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                .permitAll();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }
}