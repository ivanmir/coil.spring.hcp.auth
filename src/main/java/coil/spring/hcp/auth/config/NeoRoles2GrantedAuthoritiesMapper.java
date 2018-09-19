package coil.spring.hcp.auth.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.Attributes2GrantedAuthoritiesMapper;

import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.PersistenceException;
import com.sap.security.um.user.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NeoRoles2GrantedAuthoritiesMapper implements Attributes2GrantedAuthoritiesMapper {

	  @Override
	  public Collection<? extends GrantedAuthority> getGrantedAuthorities(Collection<String> attributes) {
	    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	    
	    Set<String> roles=getUserRoles();
		log.debug(">>>>> mapping SCP user's role to Spring's authorities");
		
		for (String sRoleName : roles) {
			log.debug(">>>>> mapping {} to authority {}",sRoleName, "ROLE_"+sRoleName);
			authorities.add(new SimpleGrantedAuthority("ROLE_" + sRoleName.toUpperCase(Locale.ENGLISH)));
		}

		log.debug(">>>>> {}", authorities);
		return authorities;		
	    
	  }

		private Set<String> getUserRoles() {
			
			User scpUser = null;
			try {
				log.debug("<<<<< Retrieving SCP User");
				scpUser = UserManagementAccessor.getUserProvider().getCurrentUser();
				log.debug(">>>>> this user has sucessfully logged on jee >>>>> {}", scpUser);
			} catch (PersistenceException e) {
				log.debug(">>>>> Unable to retrieve user", e.getCause());
			}
			
			return scpUser.getRoles();
		}	  
	  
	}
