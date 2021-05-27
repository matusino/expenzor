package com.matus.expenzor.security;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.ServletException;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

import static java.util.Date.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String testUri = "/testUri";

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    @BeforeEach
    void init(){
        userDetails = new org.springframework.security.core.userdetails.User("username"
                ,"password",true, true,
                true,true, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void shouldDoFilterInternal() throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(from(Instant.now()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*10))
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization","Bearer " + token);
        request.setRequestURI(testUri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        given(jwtProvider.extractUsername(any(String.class))).willReturn(authentication.getName());
        given(userDetailsService.loadUserByUsername(any(String.class))).willReturn(userDetails);
        given(jwtProvider.validateToken(any(String.class), any(UserDetails.class))).willReturn(true);

        //when
        jwtAuthenticationFilter.doFilterInternal(request,response,filterChain);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
    }
}