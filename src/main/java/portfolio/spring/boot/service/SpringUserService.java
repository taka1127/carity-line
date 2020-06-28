package portfolio.spring.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class SpringUserService {
	@Autowired
	private UserDetailsManager userDetailsManager;
	
	public void createSpringUser(String username, String password, String role, boolean active) {
		UserBuilder builder = User.withDefaultPasswordEncoder();
		UserDetails userDetails =
				builder
				.username(username)
				.password(password)
				.roles(role)
				.disabled(!active)
				.build();
		userDetailsManager.createUser(userDetails);
	}
	
	public void updateSpringUser(String username, String password, boolean active) {
		UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
		UserBuilder builder;
		
		// 新規パスワードが渡された場合はそれを設定する。渡されていない場合は元のパスワードを設定する。
		if(!password.equals("")) {
			builder = User.withDefaultPasswordEncoder();
			builder.password(password);
		}else {
			builder = User.builder();
			builder.password(userDetails.getPassword());
		}
		
		UserDetails newUserDetails = 
				builder
				.username(username)
				.authorities(userDetails.getAuthorities())
				.disabled(!active)
				.build();
		userDetailsManager.updateUser(newUserDetails);
	}

}
