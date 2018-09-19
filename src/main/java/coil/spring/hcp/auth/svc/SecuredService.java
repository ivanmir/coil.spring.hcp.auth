package coil.spring.hcp.auth.svc;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class SecuredService {

	public String unsecured() {
		return "An unsecured service method";
	}

	@PreAuthorize("hasRole('MANAGER')")
	//@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public String securedAdmin() throws AccessDeniedException {
		return "An service method secured with Method Security for *Managers Only*";
	}
	
	@PreAuthorize("hasRole('EMPLOYEE')")
	//@PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
	public String securedUser() throws AccessDeniedException {
		return "An service method secured with Method Security for *Employees Only*";
	}	
}
