package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.ACL;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.EmailUserRightsDTO;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class AclAddParticipantByEmailCommand implements Command {
	@Override
	public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
		String resourceUUID = props.get(0);
		try {
			EmailUserRightsDTO dto = new ObjectMapper().readValue(body, EmailUserRightsDTO.class);
			var securityService = ServiceFactory.INSTANCE.getSecurityService();
			securityService.createAcl(dto.getEmail(), resourceUUID, dto.getActions());
			return new CommandResult();
		} catch (JsonProcessingException e) {
			throw new CommandException(e);
		}
	}
}
