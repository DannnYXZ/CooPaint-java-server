package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.controller.command.WSCommand;
import com.epam.coopaint.exception.CommandException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpSession;
import java.util.List;

public class ChatAcceptMessageCommand2 extends WSCommand {
    @Override
    public CommandResult execute(List<String> props, String body, HttpSession session) throws CommandException {
        try {
            // add to chat
            JsonNode msg = new ObjectMapper().readTree(body);
            sessionHandler.addMessage(msg.path("message"));
            return new CommandResult(body);
        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }
    }
}
