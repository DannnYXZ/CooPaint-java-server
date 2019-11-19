package com.epam.coopaint.controller.command.impl2;

import com.epam.coopaint.controller.command.Command2;
import com.epam.coopaint.domain.CommandResult;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class WrongRequestCommand2 implements Command2 {
    @Override
    public CommandResult execute(List<String> props, String body, Object session) {
        var out = new CommandResult();
        out.setCode(HttpServletResponse.SC_BAD_REQUEST);
        return out;
    }
}
