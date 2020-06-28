package portfolio.spring.boot;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//Spring Securityを使用しているため、WebサーバからCSSを取得しようとするとログインが必要になってしまう
//WebSecurityConfigで「configure(WebSecurity)」メソッドをオーバーライドし「このパス以下はアクセス制御は必要ない（無視する）」という設定を記述
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/css/**");
	}
	
	 protected void configure(HttpSecurity http) throws Exception {
	        // メソッド内に何も書かなければ、デフォルトのアクセス制御がすべてオフになる。
	}

}
