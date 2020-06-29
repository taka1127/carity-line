package portfolio.spring.boot.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import portfolio.spring.boot.model.Account;
import portfolio.spring.boot.model.AccountForm;
import portfolio.spring.boot.service.AccountService;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private AccountService accountService;

	@MockBean
	private UserDetailsManager userDetailsManager;

	private static List<Account> accounts;
	private static Account account;
	//内部クラスから外部クラスの変数を参照できるようにするため
	
	@BeforeEach
	public void setUp() {
		account = new Account();
		account.setUsername("山田");
		account.setType("administrator");
		account.setActive(true);
		accounts = Arrays.asList(account);
	}

	@Test
	public void testIndexNotLogin() throws Exception {
		mockMvc.perform(get("/account"))
			.andExpect(status().isFound());
	}

	@Test
	@WithMockUser //ログインを行っている状態の実現を提供
	public void testIndexHasNotPermission() throws Exception {
		mockMvc.perform(get("/account"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles="ADMINISTRATOR") //@WithMockUserアノテーションはデフォルトでusernameに「user」、rolesに「USER」が設定されている。これを変更するには引数を設定。
	public void testIndexHasPermission() throws Exception {
		when(accountService.getAccountList()).thenReturn(accounts);
		
		//ADMINISTRATORロールなので正常にアクセスできる
		mockMvc.perform(get("/account"))
			.andExpect(status().isOk())
			.andExpect(view().name("account/index"))
			.andExpect(model().attribute("accounts", accounts));
	}

	@Test
	@WithMockUser(roles="ADMINISTRATOR")
	public void testCreateGet() throws Exception {
		
		//クエリ文字列、フォームはどちらもLinkedMultiValueMap（MultiValueMapの実装クラス）を使って値を渡す。
		//フォーム送信（POSTリクエスト）については「.with(csrf())」と付けてCSRFの送信も必要。
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); //ダイヤモンド記法(<String, String>とせずに型を省略)
		
		// メソッドチェーンの最後にandReturnを付けるとMvcResultオブジェクトが返される
		params.add("type", "administrator");
		MvcResult result = mockMvc.perform(get("/account/create").params(params))
			.andExpect(status().isOk())
			.andExpect(view().name("account/create"))
			.andReturn();
											// getModelAndView→getModel→getと呼び出すことでモデル属性取り出し
		AccountForm form = (AccountForm)result.getModelAndView().getModel().get("accountForm");
		// 個々のフィールドが設定されているかを確認
		assertThat(form.getType()).isEqualTo("administrator");
	}

	@Test
	@WithMockUser(roles="ADMINISTRATOR")
	public void testCreatePostSuccess() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("type", "administrator");
		params.add("username", "山田");
		params.add("password", "12345678");
		params.add("active", "true");
		mockMvc.perform(post("/account/create").with(csrf()).params(params))
			.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(roles="ADMINISTRATOR")
	public void testCreatePostFailValidation() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("type", "administrator");
		params.add("username", "山田");
		params.add("password", "1234");
		params.add("active", "true");
		mockMvc.perform(post("/account/create").with(csrf()).params(params))
			.andExpect(view().name("account/create"))
			.andExpect(model().hasErrors());
	}

	@Test
	@WithMockUser(roles="ADMINISTRATOR")
	public void testCreatePostFailDuplicate() throws Exception {
		// createAdministratorAccountが以下の引数で呼ばれたらDuplicateKeyExceptionを発生させる
		when(accountService.createAdministratorAccount("山田", "12345678", true))
			.thenThrow(new DuplicateKeyException(""));
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("type", "administrator");
		params.add("username", "山田");
		params.add("password", "12345678");
		params.add("active", "true");
		mockMvc.perform(post("/account/create").with(csrf()).params(params))
			.andExpect(view().name("account/create"))
			.andExpect(model().hasErrors());
	}
	
	//Spring Data JPAのWebサポートを利用したメソッドをテストするには機能をエミュレーション(模倣)する必要がある。対応するConverterを設定
	@TestConfiguration //addFormattersメソッドをオーバーライドしConverterを登録
	static class Config implements WebMvcConfigurer {

		public void addFormatters(FormatterRegistry registry) {
			registry.addConverter(String.class, Account.class, id -> account);
			//ラムダ式では引数の型を省略できる、引数の()を省略できる、{}を省略できる、returnを省略できるというルールがある
		}
		
	}

	@Test
	@WithMockUser(roles="ADMINISTRATOR")
	public void testEditGet() throws Exception {
		MvcResult result = mockMvc.perform(get("/account/win/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("account/edit"))
			.andReturn();
		AccountForm form = (AccountForm)result.getModelAndView().getModel().get("accountForm");
		assertThat(form.getUsername()).isEqualTo(account.getUsername());
		assertThat(form.getType()).isEqualTo(account.getType());
		assertThat(form.getActive()).isEqualTo(account.getActive());
	}

	@Test
	@WithMockUser(roles="ADMINISTRATOR")
	public void testEditPostSuccess() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("type", "administrator");
		params.add("username", "山田");
		params.add("password", "12345678");
		params.add("active", "true");
		mockMvc.perform(post("/account/win/edit").with(csrf()).params(params))
			.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(roles="ADMINISTRATOR")
	public void testEditPostFail() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("type", "administrator");
		params.add("username", "山田");
		params.add("password", "1234");
		params.add("active", "true");
		mockMvc.perform(post("/account/win/edit").with(csrf()).params(params))
			.andExpect(view().name("account/edit"))
			.andExpect(model().hasErrors());
	}
}

