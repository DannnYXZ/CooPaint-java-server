package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.ExtendedAclDTO;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class AclExtendedReadCommand implements Command {
	@Override
	public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
		String resourceUUID = props.get(0);
		try {
			var securityService = ServiceFactory.INSTANCE.getSecurityService();
			ExtendedAclDTO extendedAclDTO =  securityService.readExtendedAcl(resourceUUID);
			var mapper = new ObjectMapper();
			return new CommandResult().setBody(mapper.writeValueAsString(extendedAclDTO));
		} catch (ServiceException | JsonProcessingException e) {
			throw new CommandException(e);
		}
	}
}
