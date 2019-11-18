package com.epam.coopaint.controller.command;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface Command2 {
    CommandResult execute(List<String> props, String body, HttpSession session);
}
