package com.epam.coopaint.controller;

import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.pool.ConnectionPoolImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/*"})
public class FrontController extends HttpServlet {
    private static Logger logger = LogManager.getLogger();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        dispatch(CommandDispatcher.Method.POST, request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        dispatch(CommandDispatcher.Method.GET, request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        dispatch(CommandDispatcher.Method.PUT, request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        dispatch(CommandDispatcher.Method.DELETE, request, response);
    }

    private void dispatch(CommandDispatcher.Method method, HttpServletRequest request, HttpServletResponse response) {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            String url = request.getPathInfo();
            CommandResult result = CommandDispatcher.INSTANCE.dispatch(method, url, body, request.getSession(false));
            response.setStatus(result.getStatusCode());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            Writer out = response.getWriter();
            out.write(result.getBody());
            out.close();
        } catch (IOException | RuntimeException e) {
            logger.error(e);
        }
    }

    @Override
    public void destroy() {
        ConnectionPoolImpl.getInstance().closeAllConnections();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }
}
