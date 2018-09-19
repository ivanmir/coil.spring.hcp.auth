# coil.spring.hcp.auth   

## Spring application with Security for SAP Cloud Platform - Neo   

### Overview    

The application implements the Spring Security integrated with the JEE Security requirements for the container started by Neo SDK (in this case it is a Java Web Tomcat 8 container). The following logic will apply when url suffix ends with:   

- '/'				: Opens the Welcome Page with a link to the 'secured'.    <br/>  

- '/secured'		: Triggers Neo's authentication and displays user's info such as roles assigned.    <br/>   

- '/secured/admin'	: Displays a simple text denoting you have logged with a user having the role *MANAGER*.    
					  Otherwise, it displays error code 403 (Unauthorized).    <br/>

- '/secured/user'	: Similar to the above is case the user has to the role *EMPLOYEE*.    
					  Otherwise, it displays error code 403 (Unauthorized).    <br/>

### Usage

Deploy the application on your Neo's sub-account (it works on trial). After the application is deployed, assign the available roles to your user and test the URLs above.  

### Details   

#### Controllers   

This application implements two rest controllers:   
 
- <code>WelcomeController</code>: This will start the application at the root context "/". It will display a Welcome message and a link to the secured path where all requests will demand authentication from SCP-Neo.    <br/>   

- <code>HelloSecController</code>: This controller implements a Rest Interface for path "secured". The first time it is called it will trigger authentication according to the sub-account settings. Once authenticated, the service will display details on the authenticated user - such as roles that have been assigned.       

#### Services  

The **secured** path will forward requests to the <code>SecuredService</code> class that implements the services themselves using the annotation <code>@Service</code>. In that class there are two methods protected by the Spring Security framework with <code>@PreAuthorize</code> annotation.

#### Security Config   

The <code>WebSecurityConfig</code> class extends <code>WebSecurityConfigurerAdapter</code> by overriding the configure method. Here is where we protect the **secured** path and any subsequent paths using Neo's standard authentication provided by the JEE Container (<code>J2eePreAuthenticatedProcessingFilter</code>).   

The annotation <code>@EnableGlobalMethodSecurity</code> with **prePostEnabled** will allow to specify method security using annotations such as <code>@PreAuthorize</code>.     

The configure method also sets up the JEE filter <code>J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource</code> as user detail source - which is used to capture the authentication being provided by the JEEcontainer. This detail source contains two beans: one of type <code>WebXmlMappableAttributesRetriever</code> and and the other of type <code>Attributes2GrantedAuthoritiesMapper</code>.   

The bean of type <code>WebXmlMappableAttributesRetriever</code> opens the web.xml artifact to look for roles defined and tries to map them to the Spring Security (**Grated Authorities**). However, this seems not to work with Neo, as no roles get mapped to **Grated Authorities**. Hence there is also the other bean of type <code>Attributes2GrantedAuthoritiesMapper</code> which loads the Neo roles assigned to the current user via Cloud Cockpit and sets it to the JEE Filter. Once this is done, the user's roles assigned via cockpit with be available to Spring Security to validate against protected methods.

All other beans are required to provide callbacks to the JEE authentication mechanism used by the Filter.

The bean of type *Attributes2GrantedAuthoritiesMapper* uses the class <code>NeoRoles2GrantedAuthoritiesMapper</code> which implements <code>Attributes2GrantedAuthoritiesMapper</code>. This is usually used when the User Details Source is an LDAP server and you need to map the LDAP attributes to **Grated Authorities** according to some custom of rule. Since the automatic mapping was not possible with Neo's implementation, this came in handy to load the rules from the authenticated user as **Grated Authorities**.

