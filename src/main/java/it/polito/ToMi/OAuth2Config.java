/**
 * 
 */
package it.polito.ToMi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
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
	
	private static String oauthClientCredential;
    private static String oauthClientPassword;
//	private static String oauthClientCredential="appAndroidTomi";
//	private static String oauthClientPassword="df_67h%fb8_h7HFSl_9";
	

    @Value("${oauth.username}")
    public void setOauthClientCredential(String username) {
      oauthClientCredential = username;
    }
    
    @Value("${oauth.password}")
    public void setOauthClientPassword(String password) {
      oauthClientPassword = password;
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
			.antMatchers("/").permitAll()
			.antMatchers(HttpMethod.GET, "/api/v1/data/position").permitAll()
            .antMatchers(HttpMethod.GET, "/api/v1/data/cluster").permitAll()
            .antMatchers(HttpMethod.GET, "/api/v1/data/init").permitAll()
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
			.accessTokenValiditySeconds(7200);
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
