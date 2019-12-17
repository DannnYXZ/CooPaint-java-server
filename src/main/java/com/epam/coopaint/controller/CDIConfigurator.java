package com.epam.coopaint.controller;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_HTTP;

public class CDIConfigurator extends ServerEndpointConfig.Configurator {

    public <T> T getEndpointInstance(Class<T> endpointClass) {
        return CDI.current().select(endpointClass).get();
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        ServletContext servletContext = httpSession.getServletContext();
        sec.getUserProperties().put(SESSION_HTTP, httpSession);
    }
}