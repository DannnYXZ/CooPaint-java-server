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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.coopaint.domain.ACLData.RESOURCE_ALL;
import static com.epam.coopaint.domain.ResourceAction.*;
import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;
import static java.text.MessageFormat.format;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;


enum CommandDispatcher {
    INSTANCE;

    public enum Method {POST, DELETE, PUT, GET}

    private static Logger logger = LogManager.getLogger();
    private static final String UUID = "[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}";
    private static final String SNAPSHOT = "[0-9a-zA-Z]*";
    private final List<CommandDescriptor> commandDescriptors = new ArrayList<>();

    private static class CommandDescriptor {
        Method method;
        String routePattern;
        String resourceUID;
        ResourceAction action;
        List<Integer> argumentIndices = new ArrayList<>();
        Command2 command;

        CommandDescriptor method(Method method) {
            this.method = method;
            return this;
        }

        CommandDescriptor pattern(String routePattern) {
            this.routePattern = routePattern;
            return this;
        }

        CommandDescriptor setResourceUID(String resourceUID) {
            this.resourceUID = resourceUID;
            return this;
        }

        CommandDescriptor action(ResourceAction action) {
            this.action = action;
            return this;
        }

        CommandDescriptor indices(List<Integer> argumentIndices) {
            this.argumentIndices = argumentIndices;
            return this;
        }

        CommandDescriptor command(Command2 command) {
            this.command = command;
            return this;
        }
    }

    CommandDispatcher() {
        commandDescriptors.add(new CommandDescriptor().method(Method.POST).pattern("/auth").action(READ_SITE).command(new AuthCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST).pattern("/sign-up").action(READ_SITE).command(new SignUpCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST).pattern("/sign-in").action(READ_SITE).command(new SignInCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST).pattern("/sign-out").action(READ_SITE).command(new SignOutCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST).pattern("/lang-pack").action(READ_SITE).command(new LangPackCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST).pattern("/set-avatar").action(UPLOAD_FILE).command(new UploadSetAvatarCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.GET).pattern(format("/chat/(^$|{0})", UUID)).action(READ_SITE).command(new ChatConnectCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.GET).pattern(format("/chat/({0})/messages", UUID)).indices(List.of(0)).action(READ_CHAT).command(new ChatReadHistoryCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST).pattern(format("/chat/({0})/messages", UUID)).indices(List.of(0)).action(UPDATE_CHAT).command(new ChatAcceptMessagesCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.GET).pattern(format("/board/({0})", UUID)).indices(List.of(0)).action(READ_BOARD).command(new ChatAcceptMessagesCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST).pattern(format("/board/({0})", UUID)).indices(List.of(0)).action(UPDATE_BOARD).command(new ChatAcceptMessagesCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.GET).pattern(format("/snapshot/({0})", SNAPSHOT)).action(GET_SNAPSHOT).command(new GetSnapshotCommand2()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST).pattern(".*").command(new WrongRequestCommand2()));
    }

    private boolean canAccess(List<String> resources, ResourceAction action, User user) throws ServiceException {
        SecurityService securityService = ServiceFactory.getInstance().getSecurityService();
        boolean canAccess = false; // FIXME: strict
        for (String resource : resources) {
            if (securityService.canAccess(resource, action, user)) {
                canAccess = true;
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

    public CommandResult dispatch(Method method, String url, String req, HttpSession identityWard, Object session) {
        CommandDescriptor matchedDescriptor = getMatchingDescriptor(method, url);
        List<String> props = StringUtil.parseGroups(url, matchedDescriptor.routePattern);
        User user = (User) identityWard.getAttribute(SESSION_USER);
        List<String> urlResources = matchedDescriptor.argumentIndices.stream().map(props::get).collect(Collectors.toList());
        urlResources.add(RESOURCE_ALL);
        try {
            CommandResult result;
            if (canAccess(urlResources, matchedDescriptor.action, user)) {
                result = matchedDescriptor.command.execute(props, req, session);
            } else {
                result = new CommandResult().setCode(HttpServletResponse.SC_FORBIDDEN);
            }
            return result;
        } catch (ServiceException | CommandException e) {
            logger.error(e);
            return new CommandResult().setCode(SC_BAD_REQUEST).setBody(e.getMessage());
        } catch (RuntimeException e) {
            logger.fatal(e);
            var mapper = new ObjectMapper();
            ObjectNode err = new ObjectMapper().createObjectNode().put("body", "Internal ERROR ಠ╭╮ಠ.");
            var result = new CommandResult().setCode(SC_INTERNAL_SERVER_ERROR);
            try {
                return result.setBody(mapper.writeValueAsString(err));
            } catch (JsonProcessingException ex) {
                logger.error(ex);
                return result;
            }
        }
    }
}
