package com.epam.coopaint.controller;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.command.impl.*;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.ResourceAction;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.SecurityService;
import com.epam.coopaint.service.impl.ServiceFactory;
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

import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_USER;
import static com.epam.coopaint.domain.ACLData.RESOURCE_ANY;
import static com.epam.coopaint.domain.ResourceAction.*;
import static java.text.MessageFormat.format;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

/**
 * Performs request route matching system and access control via SecurityService
 */
enum CommandProvider {
    INSTANCE;

    public enum Method {POST, DELETE, PUT, GET}

    private static Logger logger = LogManager.getLogger();
    private static final String REGEX_UUID = "[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}";
    private static final String REGEX_SNAPSHOT = "[0-9a-zA-Z]*";
    private final List<CommandDescriptor> commandDescriptors = new ArrayList<>();

    private static class CommandDescriptor {
        Method method;
        String routePattern;
        String resourceUID;
        ResourceAction action;
        List<Integer> argumentIndices = new ArrayList<>();
        Command command;

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

        CommandDescriptor command(Command command) {
            this.command = command;
            return this;
        }
    }

    CommandProvider() {
        commandDescriptors.add(new CommandDescriptor().method(Method.POST)
                .pattern("/auth").action(READ_SITE).command(new AuthCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST)
                .pattern("/sign-up").action(READ_SITE).command(new SignUpCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST)
                .pattern("/sign-in").action(READ_SITE).command(new SignInCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST)
                .pattern("/sign-out").action(READ_SITE).command(new SignOutCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.GET)
                .pattern("/lang-pack/(EN|RU)").action(READ_SITE).command(new LangPackCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST)
                .pattern("/set-avatar").action(UPLOAD_FILE).command(new UploadSetAvatarCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.PUT)
                .pattern("/user/update").action(UPDATE_USER).command(new UserUpdateCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.GET)
                .pattern(format("/chat/({0})/messages", REGEX_UUID)).indices(List.of(0)).action(READ_CHAT)
                .command(new ChatReadHistoryCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST)
                .pattern(format("/chat/({0})/messages", REGEX_UUID)).indices(List.of(0)).action(UPDATE_CHAT)
                .command(new ChatAcceptMessagesCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.GET)
                .pattern(format("/board/({0})", REGEX_UUID)).indices(List.of(0)).action(READ_BOARD)
                .command(new BoardReadCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST)
                .pattern(format("/board/({0})/elements", REGEX_UUID)).indices(List.of(0)).action(UPDATE_BOARD)
                .command(new BoardAcceptElementCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.PUT)
                .pattern(format("/board/({0})", REGEX_UUID)).indices(List.of(0)).action(UPDATE_BOARD)
                .command(new BoardUpdateCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.DELETE)
                .pattern(format("/board/({0})", REGEX_UUID)).indices(List.of(0)).action(DELETE_BOARD)
                .command(new BoardDeleteCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.GET)
                .pattern(format("/snapshot/all", REGEX_UUID)).action(READ_SITE)
                .command(new SnapshotReadAllCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.GET)
            .pattern(format("/snapshot/({0})", REGEX_SNAPSHOT)).action(GET_SNAPSHOT)
            .command(new SnapshotGetCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.GET)
            .pattern(format("/access/({0})", REGEX_UUID)).indices(List.of(0)).action(READ_ACL)
            .command(new AclExtendedReadCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.PUT)
            .pattern(format("/access/({0})", REGEX_UUID)).indices(List.of(0)).action(UPDATE_ACL)
            .command(new AclUpdateCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST)
            .pattern(format("/access/({0})", REGEX_UUID)).indices(List.of(0)).action(CREATE_ACL)
            .command(new AclAddParticipantByEmailCommand()));
        commandDescriptors.add(new CommandDescriptor().method(Method.POST)
                .pattern(".*").command(new WrongRequestCommand()));
    }

    private boolean canAccess(List<String> resources, ResourceAction action, User user) throws ServiceException {
        SecurityService securityService = ServiceFactory.INSTANCE.getSecurityService();
        boolean canAccess = false;
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
        urlResources.add(RESOURCE_ANY);
        try {
            CommandResult result;
            if (canAccess(urlResources, matchedDescriptor.action, user)) {
                result = matchedDescriptor.command.execute(props, req, session);
            } else {
                result = new CommandResult().setCode(HttpServletResponse.SC_FORBIDDEN);
            }
            return result;
        } catch (ServiceException | CommandException | RuntimeException e) {
            logger.fatal("Internal server error", e);
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
