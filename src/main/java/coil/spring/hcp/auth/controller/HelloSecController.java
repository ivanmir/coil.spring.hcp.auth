package coil.spring.hcp.auth.controller;

import java.security.Principal;

import javax.annotation.security.PermitAll;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.PersistenceException;
import com.sap.security.um.user.UnsupportedUserAttributeException;
import com.sap.security.um.user.User;

import coil.spring.hcp.auth.svc.SecuredService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping({"secured"})
public class HelloSecController {

	@Autowired
	private SecuredService service;

	@PermitAll
	@GetMapping("")
	@ResponseBody
	public String getUserInfo(HttpServletRequest req) throws PersistenceException, UnsupportedUserAttributeException {
		return getUserAttributes(req.getUserPrincipal());
	}

	private String getUserAttributes(Principal principal)
			throws PersistenceException, UnsupportedUserAttributeException {
		// Get user from user storage based on principal name
		String userName = principal.getName();
		User user = UserManagementAccessor.getUserProvider().getUser(userName);

		StringBuilder roleSb = new StringBuilder();
		for (String role : user.getRoles()) {
			if (roleSb.length() > 0) {
				roleSb.append(" | ");
			}
			roleSb.append(role);
		}

		StringBuilder attrSb = new StringBuilder();
		for (String attr : user.listAttributes()) {
			attrSb.append(attr).append(": ").append(user.getAttribute(attr)).append("<br/>");
		}

		return String.format("User-ID: %s<br/>Roles: %s<br/>Attributes:<br/>%s", userName, roleSb.toString(),
				attrSb.toString());
	}
	
	@GetMapping("admin")
	@ResponseBody
	public String adminMethodSecured() {
		try {
			return service.securedAdmin();
		} catch (AccessDeniedException e) {
			log.debug("Oops...you do not have the required priviledge to be here: " + e.getCause());
			return "Error code: " + Response.SC_FORBIDDEN;
		}
	}
	
	@GetMapping("user")
	@ResponseBody
	public String userMethodSecured() {
		try {
			return service.securedUser();
		} catch (AccessDeniedException e) {
			log.debug("Oops...you do not have the required priviledge to be here: " + e.getCause());
			return "Error code: " + Response.SC_FORBIDDEN;
		}
	}
	
	
}
