package portfolio.spring.boot;


import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

//Spring Securityを使用しているため、WebサーバからCSSを取得しようとするとログインが必要になってしまう
//WebSecurityConfigで「configure(WebSecurity)」メソッドをオーバーライドし「このパス以下はアクセス制御は必要ない（無視する）」という設定を記述
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/css/**");
	}
	
	//Spring Securityでの認可の設定
	 protected void configure(HttpSecurity http) throws Exception {
	        // メソッド内に何も書かなければ、デフォルトのアクセス制御がすべてオフになる。
		 	//antMatchersに続きhasRoleメソッドを呼び出すことでパスに対して必要なロールを設定することができる。
		 	//ユーザが指定するロールを持たない場合はアクセス拒否
		 http.authorizeRequests()
		 .antMatchers("/account/**").hasRole("ADMINISTRATOR")
		 .antMatchers("/company/**").hasRole("COMPANY")
		 .antMatchers("/person/**").hasRole("PERSON")
		 .and()
		 .formLogin().loginPage("/login").permitAll()
		 .and()
		 .logout().logoutSuccessUrl("/");

	 }
	 
	 //ユーザの作成も行うのでUserDetailsManagerとして定義
	 @Bean
	 public UserDetailsManager userDetailsManager(DataSource dataSource) {
		 return new JdbcUserDetailsManager(dataSource);
	 }


}
