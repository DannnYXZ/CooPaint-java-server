package com.epam.coopaint.controller;

import com.epam.coopaint.command.impl.SessionAttribute;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.service.UserService;
import com.epam.coopaint.service.impl.ServiceFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession(false);
        if (session == null) {
            // GUEST
            session = request.getSession();
            UserService userService = ServiceFactory.INSTANCE.getUserService();
            User guest = userService.createGuest();
            session.setAttribute(SessionAttribute.SESSION_USER, guest);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
