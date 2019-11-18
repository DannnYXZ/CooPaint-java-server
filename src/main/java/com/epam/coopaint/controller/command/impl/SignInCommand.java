package com.epam.coopaint.controller.command.impl;

import com.epam.coopaint.controller.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignInCommand implements Command {
    private static Logger logger = LogManager.getLogger();

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        /*
        try (Writer out = response.getWriter()) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                UserService userService = ServiceFactory.getInstance().getUserService();
                ObjectMapper mapper = new ObjectMapper();
                try {
                    SignInUpBundle signInUpBundle = mapper.readValue(request.getReader(), SignInUpBundle.class);
                    User user = userService.singIn(signInUpBundle);
                    user.setAvatar(Paths.get(SERVE_PATH_AVATAR, user.getAvatar()).toString());
                    String jsonUser = mapper.writeValueAsString(user);
                    session = request.getSession();
                    session.setAttribute(SESSION_USER, user);
                    out.write(jsonUser);
                } catch (ServiceException e) {
                    var error = new ErrorInfo(SC_NOT_FOUND, "sign.in.failed.no.such");
                    response.setStatus(SC_NOT_FOUND);
                    out.write(mapper.writeValueAsString(error));
                    logger.error("Not registered user tried to sign in.");
                } catch (RuntimeException e) {
                    logger.error(e);
                    var error = new ErrorInfo(SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    response.setStatus(SC_INTERNAL_SERVER_ERROR);
                    out.write(mapper.writeValueAsString(error));
                }
            } else {
                var mapper = new ObjectMapper();
                User user = (User) session.getAttribute(SESSION_USER);
                out.write(mapper.writeValueAsString(user));
            }
        } catch (IOException e) {
            logger.error(e);
        }
        */
    }
}
