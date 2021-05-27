package com.matus.expenzor.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;


import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    private User user;

    @BeforeEach
    void setUp() {
        user = new org.springframework.security.core.userdetails.User("username"
                ,"password",true, true,
                true,true, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void generateToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user,"pass");
        String token = jwtProvider.generateToken(authentication);
        assertThat(token).isNotEmpty();
    }

    @Test
    void extractUsername() {
        String token = jwtProvider.generateToken(new UsernamePasswordAuthenticationToken(user,"pass"));
        assertThat(jwtProvider.extractUsername(token)).isEqualTo(user.getUsername());
    }

    @Test
    void extractExpiration() {
        String token = jwtProvider.generateToken(new UsernamePasswordAuthenticationToken(user,"pass"));
        assertThat(jwtProvider.extractExpiration(token)).isBeforeOrEqualTo(new Date(System.currentTimeMillis()+1000*60*60*10));
    }

    @Test
    void validateToken() {
        String token = jwtProvider.generateToken(new UsernamePasswordAuthenticationToken(user,"pass"));
        assertThat(jwtProvider.validateToken(token,user)).isTrue();
    }
}