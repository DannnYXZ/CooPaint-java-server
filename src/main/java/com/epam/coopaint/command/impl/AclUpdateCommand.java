package com.epam.coopaint.command.impl;

import com.epam.coopaint.command.Command;
import com.epam.coopaint.domain.ACL;
import com.epam.coopaint.domain.CommandResult;
import com.epam.coopaint.domain.ExtendedAclDTO;
import com.epam.coopaint.domain.ResourceAction;
import com.epam.coopaint.exception.CommandException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.impl.ServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AclUpdateCommand implements Command {
	@Override
	public CommandResult execute(List<String> props, String body, Object session) throws CommandException {
		String resourceUUID = props.get(0);
		try {
			ACL updatedACL = new ObjectMapper().readValue(body, ACL.class);
			var securityService = ServiceFactory.INSTANCE.getSecurityService();
			securityService.updateAcl(resourceUUID, updatedACL);
			return new CommandResult();
		} catch (JsonProcessingException e) {
			throw new CommandException(e);
		}
	}
}
