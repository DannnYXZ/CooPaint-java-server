package com.epam.coopaint.controller;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.controller.command.impl2.*;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.domain.ResourceAction;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.SecurityService;
import com.epam.coopaint.service.ServiceFactory;
import com.epam.coopaint.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;
import static java.text.MessageFormat.format;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;


enum CommandDispatcher {
    INSTANCE;

    public enum Method {POST, DELETE, PUT, GET}

    private static Logger logger = LogManager.getLogger();
    private static final String UUID = "[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}";
    private final List<CommandDescriptor> commandDescriptors = new ArrayList<>();

    private static class CommandDescriptor {
        Method method;
        String routePattern;
        String resourceUID;
        ResourceAction action;
        List<Integer> argumentIndices;
        Command2 command;

        CommandDescriptor() {
            argumentIndices = new ArrayList<>();
        }

        CommandDescriptor(Method method, String routePattern, List<Integer> resourceIndices, ResourceAction action, Command2 command) {
            this.method = method;
            this.routePattern = routePattern;
            // this.resourceUID = resourceUID;
            this.action = action;
            this.argumentIndices = resourceIndices;
            this.command = command;
        }

        CommandDescriptor setMethod(Method method) {
            this.method = method;
            return this;
        }

        CommandDescriptor setRoutePattern(String routePattern) {
            this.routePattern = routePattern;
            return this;
        }

        CommandDescriptor setResourceUID(String resourceUID) {
            this.resourceUID = resourceUID;
            return this;
        }

        CommandDescriptor setActions(ResourceAction action) {
            this.action = action;
            return this;
        }

        CommandDescriptor setArgumentIndices(List<Integer> argumentIndices) {
            this.argumentIndices = argumentIndices;
            return this;
        }

        CommandDescriptor setCommand(Command2 command) {
            this.command = command;
            return this;
        }
    }


    CommandDispatcher() {
        commandDescriptors.add(new CommandDescriptor().setMethod(Method.POST).setRoutePattern("/auth").setCommand(new AuthCommand2()));
        commandDescriptors.add(new CommandDescriptor().setMethod(Method.POST).setRoutePattern("/sign-up").setCommand(new SignUpCommand2()));
        commandDescriptors.add(new CommandDescriptor().setMethod(Method.POST).setRoutePattern("/sign-in").setCommand(new SignInCommand2()));
        commandDescriptors.add(new CommandDescriptor().setMethod(Method.POST).setRoutePattern("/sign-out").setCommand(new SignOutCommand2()));
        commandDescriptors.add(new CommandDescriptor().setMethod(Method.POST).setRoutePattern("/lang-pack").setCommand(new LangPackCommand2()));
        commandDescriptors.add(new CommandDescriptor().setMethod(Method.POST).setRoutePattern("/set-avatar").setCommand(new UploadSetAvatarCommand2()));
        commandDescriptors.add(new CommandDescriptor(Method.GET, format("/chat/({0})", UUID), List.of(0), ResourceAction.READ_CHAT, new ChatReadHistoryCommand2()));
        commandDescriptors.add(new CommandDescriptor(Method.POST, format("/chat/({0})", UUID), List.of(0), ResourceAction.UPDATE_CHAT, new ChatAcceptMessageCommand2()));
        commandDescriptors.add(new CommandDescriptor(Method.GET, format("/board/({0})", UUID), List.of(0), ResourceAction.READ_BOARD, new ChatAcceptMessageCommand2()));
        commandDescriptors.add(new CommandDescriptor(Method.PUT, format("/board/({0})", UUID), List.of(0), ResourceAction.UPDATE_BOARD, new ChatAcceptMessageCommand2()));
        commandDescriptors.add(new CommandDescriptor().setMethod(Method.POST).setRoutePattern(".*").setCommand(new WrongRequestCommand2()));
    }

    private boolean canAccess(List<String> resources, ResourceAction action, User user) throws ServiceException {
        SecurityService securityService = ServiceFactory.getInstance().getSecurityService();
        boolean canAccess = true;
        for (String resource : resources) {
            if (!securityService.canAccess(resource, action, user)) {
                canAccess = false;
                break;
            }
        }
        return canAccess;
    }

    private CommandDescriptor getMatchingDescriptor(Method method, String url) {
        CommandDescriptor matchedDescriptor = new CommandDescriptor();
        for (CommandDescriptor descriptor : commandDescriptors) {
            if (descriptor.method.equals(method) && url.matches(descriptor.routePattern)) {
                matchedDescriptor = descriptor;
                break;
            }
        }
        return matchedDescriptor;
    }

    public CommandResult dispatch(Method method, String url, String req, HttpSession httpSession) {
        CommandDescriptor matchedDescriptor = getMatchingDescriptor(method, url);
        List<String> props = StringUtil.parseGroups(url, matchedDescriptor.routePattern);
        User user = (User) httpSession.getAttribute(SESSION_USER);
        List<String> urlResources = matchedDescriptor.argumentIndices.stream().map(props::get).collect(Collectors.toList());
        try {
            CommandResult result;
            if (canAccess(urlResources, matchedDescriptor.action, user)) {
                result = matchedDescriptor.command.execute(props, req, httpSession);
            } else {
                result = new CommandResult().setCode(HttpServletResponse.SC_FORBIDDEN);
            }
            return result;
        } catch (ServiceException | CommandException e) {
            logger.error(e);
            return new CommandResult().setCode(SC_BAD_REQUEST).setBody(e.getMessage());
        } catch (RuntimeException e) {
            logger.fatal(e);
            return new CommandResult().setCode(SC_INTERNAL_SERVER_ERROR).setBody("Internal ERROR ಠ╭╮ಠ.");
        }
    }
}
