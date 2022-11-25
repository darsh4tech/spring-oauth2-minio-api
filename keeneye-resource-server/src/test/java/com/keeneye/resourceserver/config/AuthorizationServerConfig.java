package com.keeneye.resourceserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

@Configuration
@EnableAuthorizationServer
@SuppressWarnings("static-method")
class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

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
    private AuthenticationManager   authenticationManager;

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
        .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
		.withClient(clientId)
		.secret(passwordEncoder.encode(clientSecret))
		.authorizedGrantTypes(authorizedGrantTypes)
		.accessTokenValiditySeconds(accessTokenValiditySeconds)
		.refreshTokenValiditySeconds(refreshTokenValiditySeconds)
		.scopes("all");
    }
    
}
