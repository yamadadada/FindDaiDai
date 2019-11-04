package com.yamada.five.filter;

import com.yamada.five.handler.MyAuthenticationFailureHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private MyAuthenticationFailureHandler failureHandler;

    public AuthenticationFilter(String defaultFilterProcessesUrl, MyAuthenticationFailureHandler failureHandler) {
        super(defaultFilterProcessesUrl);
        this.failureHandler = failureHandler;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        if (request.getRequestURI().equals("/login/form") && StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {
            String inputImageCode = req.getParameter("imageCode");
            Object imageCode = ((HttpServletRequest)req).getSession().getAttribute("imageCode");
            if (imageCode == null || !imageCode.equals(inputImageCode)) {
                failureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("验证码不正确"));
                return;
            }
        }
        chain.doFilter(req, res);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        return null;
    }
}
