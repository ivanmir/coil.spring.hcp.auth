package coil.spring.hcp.auth.controller;

import javax.annotation.security.PermitAll;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

	@PermitAll
	@GetMapping("")
	@ResponseBody
	public String welcomeInfo() {
		return "<h1>Welcome</h1><br><a href='secured'>go secure</a>";
	}

}
