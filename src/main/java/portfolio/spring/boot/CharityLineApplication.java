package portfolio.spring.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DuplicateKeyException;

import portfolio.spring.boot.service.AccountService;

@SpringBootApplication
public class CharityLineApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CharityLineApplication.class, args);
	}
	
	//起動時に管理者アカウントを作る
	//@Componentアノテーションは@Serviceや@ControllerのスーパークラスのようなものでSpring Frameworkによりインスタンス化される
	//(@SpringBootApplicationは@Componentを含むためrunメソッドが実行される)
	
	// JobboardApplicationは@DataJpaTestを付けたテストでもインスタンス化されるが
    // その場合AccountServiceインスタンスがないのでエラーになる。
    // 「required = false」とするとインスタンスがない場合でもエラーにならない。
	@Autowired(required = false)
	private AccountService accountService;
	
	@Override
	public void run(String... args) throws Exception {
        // インスタンスがDIされていない（テスト時）は実行しない
        if (accountService == null) {
        	return;
        }
	
		String adminUsername = "admin";
		String adminPassword = "admin";
		
		try {
			accountService.createAdministratorAccount(adminUsername, adminPassword, true);
		} catch (DuplicateKeyException e) {
			//データを挿入または更新しようとした結果、主キーまたは一意の制約に違反した場合にスローされる例外。
		}
	}
}
