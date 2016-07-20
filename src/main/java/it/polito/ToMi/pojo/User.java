package it.polito.ToMi.pojo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import it.polito.ToMi.exception.BadRequestException;

@Document
public class User implements UserDetails{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5587754868440437695L;
	
	@Id
	private String id;
	@Indexed(unique=true)
	private String username;
	private String password;
	private List<Role> roles;
	private String deviceId;

	public User(){}
	
	public User(SubscribeDTO u) throws BadRequestException{
		this();
		this.password = u.getPassword();
		this.username = u.getUsername();
		int i = u.getUsername().indexOf("-");
		if(i<0)
			throw new BadRequestException("Malformed username");
		this.deviceId = u.getUsername().substring(0, i); 
	}
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}
