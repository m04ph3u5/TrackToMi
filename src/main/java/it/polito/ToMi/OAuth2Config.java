/**
 * 
 */
package it.polito.ToMi;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import it.polito.ToMi.service.UserServiceImpl;

/**
 * @author m04ph3u5
 *
 */
@Configuration
public class OAuth2Config {
	
	private static final String RESOURCE_ID = "restservice";
	
//	@Value("${oauth.username}")
	private static String oauthClientCredential="appAndroidTomi";
	
//	@Value("${oauth.password}")
	private static String oauthClientPassword="xcc33Ht_123";
	
	@PostConstruct
	public void init(){
		System.out.println(oauthClientCredential+" "+oauthClientPassword);
	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter{

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			// @formatter:off
			resources
			.resourceId(RESOURCE_ID);
			// @formatter:on
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
			.authorizeRequests()
//			.antMatchers("/api/v1/test").permitAll()
			.antMatchers("/api/v1/subscribe").anonymous()
			.antMatchers("/api/**").hasRole("USER");
			// @formatter:on
		}
	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends
	AuthorizationServerConfigurerAdapter {


		private TokenStore tokenStore = new InMemoryTokenStore();


		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;

		@Autowired
		private UserServiceImpl userService;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			// @formatter:off
			endpoints
			.tokenStore(this.tokenStore)
			.authenticationManager(this.authenticationManager)
			.userDetailsService(userService);
			// @formatter:on
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @formatter:off
			clients
			.inMemory()
			.withClient(oauthClientCredential)
			.authorizedGrantTypes("password", "refresh_token")
			.authorities("USER")
			.scopes("read", "write")
			.resourceIds(RESOURCE_ID)
			.secret(oauthClientPassword)
			.accessTokenValiditySeconds(3600);
			// @formatter:on
		}

		@Bean
		@Primary
		public DefaultTokenServices tokenServices() {
			DefaultTokenServices tokenServices = new DefaultTokenServices();
			tokenServices.setSupportRefreshToken(true);
			tokenServices.setTokenStore(this.tokenStore);

			return tokenServices;
		}

	}

}
