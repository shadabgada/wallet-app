package com.gaming.wallet.security;

import com.gaming.wallet.security.service.DatabaseUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConf extends WebSecurityConfigurerAdapter {
    @Autowired
    private DatabaseUserDetailService userDetailService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/wallet/deposit").hasAnyAuthority("PLAYER","ADMIN")
                .antMatchers(HttpMethod.POST, "/wallet/withdraw").hasAnyAuthority("PLAYER","ADMIN")
                .antMatchers(HttpMethod.POST, "/wallet/bet").hasAnyAuthority("PLAYER","ADMIN")
                .antMatchers(HttpMethod.POST, "/wallet/win").hasAnyAuthority("PLAYER","ADMIN")
                .antMatchers(HttpMethod.GET, "/wallet/balance/**").hasAnyAuthority("PLAYER","ADMIN")
                .antMatchers(HttpMethod.GET, "/wallet/transactions").hasAuthority("ADMIN").and()
                .csrf().disable();
    }
}
