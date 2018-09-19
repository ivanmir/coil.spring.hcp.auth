package coil.spring.hcp.auth.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.Attributes2GrantedAuthoritiesMapper;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.j2ee.WebXmlMappableAttributesRetriever;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(preAuthenticatedAuthenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable().cacheControl().disable();
		http.sessionManagement().sessionFixation().migrateSession().disable();
		http.addFilterBefore(preAuthenticatedProcessingFilter(), RequestHeaderAuthenticationFilter.class).authenticationProvider(preAuthenticatedAuthenticationProvider())
				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/secured/**")
				.authenticated();		
	}
	
	//Creates the http filter with jee as data source 
	private J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() throws Exception {
		J2eePreAuthenticatedProcessingFilter filter = new J2eePreAuthenticatedProcessingFilter();
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationDetailsSource(detailSource());
		return filter;
	}

	//Assigns the user data source as JEE 
	@Bean
	public J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource detailSource() {
		J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource detailSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource();
		detailSource.setMappableRolesRetriever(mappableRolesRetriever());
		detailSource.setUserRoles2GrantedAuthoritiesMapper(userRoles2GrantedAuthoritiesMapper());
		return detailSource;
	}
	
	//Reads the list of roles from web.xml file. It is here just to be listed in the cockpit
	@Bean
	public WebXmlMappableAttributesRetriever mappableRolesRetriever() {
		return new WebXmlMappableAttributesRetriever();
	}

	//Not sure if this is really needed - leave it alone for now 
	@Bean
	public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
		PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
		provider.setPreAuthenticatedUserDetailsService(preAuthenticatedUserDetailsService());
		return provider;
	}

	//Not sure if this is really needed - leave it alone for now
	@Bean
	public PreAuthenticatedGrantedAuthoritiesUserDetailsService preAuthenticatedUserDetailsService() {
		return new PreAuthenticatedGrantedAuthoritiesUserDetailsService();
	}	

    //Custom mapper retrieves a list os roles from SCP and adds as Granted Authority to Spring 
	@Bean
    public Attributes2GrantedAuthoritiesMapper userRoles2GrantedAuthoritiesMapper() {
		final NeoRoles2GrantedAuthoritiesMapper simpleAttributes2GrantedAuthoritiesMapper = new NeoRoles2GrantedAuthoritiesMapper();
		return simpleAttributes2GrantedAuthoritiesMapper;
    }
    
	//Not sure if this is really needed - leave it alone for now
    @Bean
    public AuthenticationManager authenticationManager()
    {
        final List<AuthenticationProvider> authenticationProvider = new ArrayList<AuthenticationProvider>();
        authenticationProvider.add(preAuthenticatedAuthenticationProvider());
        final AuthenticationManager authenticationManager = new ProviderManager(authenticationProvider);
        return authenticationManager;
    }
    
	    
	
}



