package com.epam.coopaint.controller;

import com.epam.coopaint.domain.User;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;

public class CDIConfigurator extends ServerEndpointConfig.Configurator {

    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return CDI.current().select(endpointClass).get();
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        ServletContext servletContext = httpSession.getServletContext();
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
        // User user = (User) httpSession.getAttribute(SESSION_USER);
        // user.setName("EKEKE");
        // httpSession.setAttribute(SESSION_USER, user);
    }
}