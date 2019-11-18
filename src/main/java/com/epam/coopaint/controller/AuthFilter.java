package com.epam.coopaint.controller;

import com.epam.coopaint.domain.User;
import com.epam.coopaint.service.ServiceFactory;
import com.epam.coopaint.service.UserService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;

@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession(false);
        if (session == null) {
            // GUEST
            session = request.getSession();
            UserService userService = ServiceFactory.getInstance().getUserService();
            User guest = userService.createGuest();
            session.setAttribute(SESSION_USER, guest);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
