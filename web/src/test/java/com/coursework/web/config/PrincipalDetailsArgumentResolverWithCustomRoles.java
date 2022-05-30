package com.coursework.web.config;

import com.coursework.web.security.AuthenticationUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class PrincipalDetailsArgumentResolverWithCustomRoles implements HandlerMethodArgumentResolver {

    private Set<String> roles;

    public PrincipalDetailsArgumentResolverWithCustomRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(AuthenticationUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        AuthenticationUser principalDetails = new AuthenticationUser(
                1, "user", "pass", "mail", true, roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase())).collect(Collectors.toSet())
        );
        return principalDetails;
    }
}