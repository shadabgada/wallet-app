package com.gaming.wallet.security.service;

import com.gaming.wallet.entity.Player;
import com.gaming.wallet.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class DatabaseUserDetailService implements UserDetailsService {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player user = playerRepository.findPlayerByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not present"));
        user.setPassword(passwordEncoder().encode(user.getPassword()));

        UserDetails userDetails = User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(user)).build();

        return userDetails;
    }

    private Collection<GrantedAuthority> getAuthorities(Player user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRoleCode()));
        return authorities;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}