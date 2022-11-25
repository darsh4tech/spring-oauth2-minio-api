package com.keeneye.resourceserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{
	
    @Override
    public void configure(final ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("user-resource-1");
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.anonymous().disable()
        	.authorizeRequests()
        	.antMatchers("/products").permitAll()
        	.anyRequest().authenticated()
        	.and()
        	.exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
            
    }

}
