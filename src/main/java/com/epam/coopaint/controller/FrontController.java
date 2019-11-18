package com.epam.coopaint.controller;

import com.epam.coopaint.controller.command.CommandResult;
import com.epam.coopaint.controller.command.Command;
import com.epam.coopaint.pool.ConnectionPoolImpl;
import com.epam.coopaint.util.StringUtil;
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
    private CommandProvider commandProvider = new CommandProvider();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String rawCommandName = request.getPathInfo();
        Command command = commandProvider.getCommand(StringUtil.toEnumString(rawCommandName));
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            String url = request.getPathInfo();
            CommandResult result = CommandDispatcher.INSTANCE.dispatch(CommandDispatcher.Method.POST, url, body, request.getSession(false));
            response.setStatus(result.getStatusCode());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            Writer out = response.getWriter();
            out.write(result.getBody());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
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
