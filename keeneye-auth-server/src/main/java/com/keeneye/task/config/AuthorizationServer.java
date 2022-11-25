package com.keeneye.task.config;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

import com.keeneye.task.dto.MyUserPrincipal;
import com.keeneye.task.entity.UserData;
import com.keeneye.task.repository.IUserRepository;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

	@Value("${accessTokenValiditySeconds}")
	private int accessTokenValiditySeconds;

	@Value("${refreshTokenValiditySeconds}")
	private int refreshTokenValiditySeconds;

	@Value("${keeneye.client_id}")
	private String clientId;

	@Value("${keeneye.client_secret}")
	private String clientSecret;

	@Value("${keeneye.authorized-grant-types}")
	private String[] authorizedGrantTypes;

	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private IUserRepository iUserRepository;
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager);
	}

	@Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()")
        	 .checkTokenAccess("isAuthenticated()")  ;
    }
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient(clientId)
				.secret(passwordEncoder.encode(clientSecret))
				.authorizedGrantTypes(authorizedGrantTypes)
				.accessTokenValiditySeconds(accessTokenValiditySeconds)
				.refreshTokenValiditySeconds(refreshTokenValiditySeconds)
				.scopes("all");

	}

	@EventListener
    public void authSuccessEventListener(AuthenticationSuccessEvent authorizedEvent){
        
        if(authorizedEvent.getAuthentication().getPrincipal() instanceof MyUserPrincipal) {
        	MyUserPrincipal myUserPrincipal = (MyUserPrincipal)authorizedEvent.getAuthentication().getPrincipal();
        	Optional<UserData> userDataOptional = iUserRepository.findByUsernameAndDeletedIsFalse(myUserPrincipal.getUsername());
        	if(userDataOptional.isPresent()) {
        		UserData userData = userDataOptional.get();
        		userData.setLastLoginTime(Instant.now());
        		iUserRepository.save(userData);
        	}
        	
        }
    }
		
}
