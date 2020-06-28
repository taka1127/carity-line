package portfolio.spring.boot.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class SpringUserServiceTest {
	
	@TestConfiguration
	static class Config {
		
		@Bean
		public UserDetailsManager userDetailsManager() {
			return new InMemoryUserDetailsManager();
		}
		
		@Bean
		public SpringUserService springUserService() {
			return new SpringUserService();
		}
		
	}
	
	@Autowired
	private UserDetailsManager userDetailsManager;
	
	@Autowired
	private SpringUserService springUserService;
	
	@Test
	public void testCreateSpringUser() {
		springUserService.createSpringUser("admin1", "admin1", "ADMINISTRATOR", true);
	
		UserDetails userDetails = userDetailsManager.loadUserByUsername("admin1");
		assertThat(userDetails).isNotNull();
		assertThat(userDetails.isEnabled()).isEqualTo(true);
				// 設定されている権限を確認
		        // 権限は設定したロールに対して「ROLE_」が付けられる
		for (GrantedAuthority auth : userDetails.getAuthorities()) {
		assertThat(auth.getAuthority()).isEqualTo("ROLE_ADMINISTRATOR");
		}
	}
	
	@Test
	public void testUpdateSpringUserChangePassword() {
		UserBuilder builder = User.withDefaultPasswordEncoder();
		UserDetails userDetails = 
		builder.username("admin2").password("admin2").roles("ADMINISTRATOR").build();
		userDetailsManager.createUser(userDetails);
		
		springUserService.updateSpringUser("admin2", "ADMIN2", true);
		
		UserDetails newUserDetails = userDetailsManager.loadUserByUsername("admin2");
		assertThat(newUserDetails.getPassword()).isNotEqualTo(userDetails.getPassword());
	}
	
	@Test
	public void testUpdateSpringUserNotChangePassword() {
		UserBuilder builder = User.withDefaultPasswordEncoder();
		UserDetails userDetails = 
		builder.username("admin3").password("admin3").roles("ADMINISTRATOR").build();
		userDetailsManager.createUser(userDetails);
		
		springUserService.updateSpringUser("admin3", "", true);
		
		UserDetails newUserDetails = userDetailsManager.loadUserByUsername("admin3");
		assertThat(newUserDetails.getPassword()).isEqualTo(userDetails.getPassword());
	}
}

