/**
 * 
 */
package it.polito.ToMi.pojo;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author m04ph3u5
 *
 */
public class SubscribeDTO {

    @NotNull
    @NotEmpty
	private String username;
    @NotNull
    @NotEmpty
	private String password;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
