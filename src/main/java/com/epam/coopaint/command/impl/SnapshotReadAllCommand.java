package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.Snapshot;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.SnapshotService;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.command.impl.SessionAttribute.SESSION_USER;

public class SnapshotReadAllCommand implements Command {
    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        var httpSession = (HttpSession) session;
        var user = (User) httpSession.getAttribute(SESSION_USER);
        SnapshotService snapshotService = ServiceFactory.INSTANCE.getSnapshotService();
        try {
            List<Snapshot> snapshots = snapshotService.readSnapshots(user.getUuid());
            var result = new CommandResult().setBody(new ObjectMapper().writeValueAsString(snapshots));
            return result;
        } catch (ServiceException | JsonProcessingException e) {
            throw new CommandException("Failed to store snapshot.", e);
        }
    }
}
