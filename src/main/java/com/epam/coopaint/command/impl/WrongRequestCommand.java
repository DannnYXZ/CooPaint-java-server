package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.CommandResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class WrongRequestCommand implements Command {
    @Override
    public CommandResult execute(List<String> props, String body, Object session) {
        var out = new CommandResult();
        out.setCode(HttpServletResponse.SC_BAD_REQUEST);
        return out;
    }
}
