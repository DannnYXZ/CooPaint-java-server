package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.WSBoardService;
import com.epam.coopaint.service.WSBoardService2;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpSession;
import java.util.List;

import static com.epam.coopaint.domain.SessionAttribute.SESSION_USER;

public class ReadMyBoardsMeta2 implements Command2 {

    @Override
    public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
        try {
            var httpSession = (HttpSession) session;
            User user = (User) httpSession.getAttribute(SESSION_USER);
            var boardService = CDI.current().select(WSBoardService2.class).get();
            List<Board> boards = boardService.getUserBoardsMeta(user.getUuid());
            return new CommandResult().setBody(new ObjectMapper().writeValueAsString(boards));
        } catch (JsonProcessingException | ServiceException e) {
            throw new CommandException(e);
        }
    }
}
