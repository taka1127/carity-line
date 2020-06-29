package portfolio.spring.boot.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import portfolio.spring.boot.model.Account;
import portfolio.spring.boot.model.Company;
import portfolio.spring.boot.model.Entry;
import portfolio.spring.boot.model.Offer;
import portfolio.spring.boot.model.Person;
import portfolio.spring.boot.service.AccountService;
import portfolio.spring.boot.service.CompanyService;

@WebMvcTest(CompanyController.class)
public class CompanyControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CompanyService companyService;

	@MockBean
	private AccountService accountService;

	@MockBean
	private UserDetailsManager userDetailsManager;

	private static Account account;
	
	// IDに対して異なるオブジェクトを返すようにする
	private static Map<String, Offer> offerMap;
	private static Map<String, Entry> entryMap;

	@BeforeEach
	public void setUp() {
		// ログインアカウントの設定。company1をログインアカウントに関連付ける
		Company company1 = new Company();
		company1.setName("test");
		account = new Account();
		account.setCompany(company1);
		when(accountService.find("user")).thenReturn(account);

		// company2は他社アカウント
		Company company2 = new Company();
		company2.setName("other");

		offerMap = new HashMap<String, Offer>();

		// 自社が作成した求人情報
		Offer offer1 = new Offer();
		offer1.setContents("");
		offer1.setCompany(company1);
		offerMap.put("1", offer1);

		// 他社が作成した求人情報
		Offer offer2 = new Offer();
		offer2.setCompany(company2);
		offerMap.put("2", offer2);
		
		entryMap = new HashMap<String, Entry>();
		
		Person person1 = new Person();
		person1.setName("山田");
		person1.setCareer("");

		// 自社求人情報に対する応募
		Entry entry1 = new Entry();
		entry1.setOffer(offer1);
		entry1.setPerson(person1);
		entry1.setContents("");
		entryMap.put("1", entry1);
		
		// 他社求人情報に対する応募
		Entry entry2 = new Entry();
		entry2.setOffer(offer2);
		entryMap.put("2", entry2);
	}
	
	@TestConfiguration
	static class Config implements WebMvcConfigurer {

		@Override
		public void addFormatters(FormatterRegistry registry) {
			registry.addConverter(String.class, Offer.class, id -> offerMap.get(id));
			registry.addConverter(String.class, Entry.class, id -> entryMap.get(id));
		}
		
	}

	@Test
	public void testIndexNotLogin() throws Exception {
		mockMvc.perform(get("/company"))
			.andExpect(status().isFound());
	}

	@Test
	@WithMockUser
	public void testIndexHasNotPermission() throws Exception {
		mockMvc.perform(get("/company"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testIndexHasPermission() throws Exception {
		mockMvc.perform(get("/company"))
			.andExpect(status().isOk())
			.andExpect(view().name("company/index"))
			.andExpect(model().attribute("company", account.getCompany()));
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testEditGet() throws Exception {
		mockMvc.perform(get("/company/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("company/form"));
	}
	
	@Test
	@WithMockUser(roles="COMPANY")
	public void testEditPostSuccess() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("name", "test");
		params.add("url", "http://www.example.com/");
		params.add("mail", "test@example.com");
		params.add("tel", "012-3456-7890");
		mockMvc.perform(post("/company/edit").with(csrf()).params(params))
			.andExpect(status().isFound());
	}
	
	@Test
	@WithMockUser(roles="COMPANY")
	public void testEditPostFail() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		mockMvc.perform(post("/company/edit").with(csrf()).params(params))
			.andExpect(view().name("company/form"))
			.andExpect(model().hasErrors());
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testCreateOfferGet() throws Exception {
		MvcResult result = mockMvc.perform(get("/company/offer/create"))
			.andExpect(status().isOk())
			.andExpect(view().name("company/offer/form"))
			.andReturn();
		Offer offer = (Offer)result.getModelAndView().getModel().get("offer");
		assertThat(offer.getCompany()).isEqualTo(account.getCompany());
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testCreateOfferPostSuccess() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("title", "清掃ボランティア募集");
		params.add("prefecture", "東京都");
		params.add("address", "新宿区");
		mockMvc.perform(post("/company/offer/create").with(csrf()).params(params))
			.andExpect(status().isFound());
	}


	@Test
	@WithMockUser(roles="COMPANY")
	public void testCreateOfferPostFail() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		mockMvc.perform(post("/company/offer/create").with(csrf()).params(params))
			.andExpect(view().name("company/offer/form"))
			.andExpect(model().hasErrors());
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testShowOfferHasPermission() throws Exception {
		mockMvc.perform(get("/company/offer/1"))
			.andExpect(status().isOk())
			.andExpect(view().name("company/offer/show"))
			.andExpect(model().attribute("offer", offerMap.get("1")));
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testShowOfferHasNotPermission() throws Exception {
		mockMvc.perform(get("/company/offer/2"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testEditOfferHasPermission() throws Exception {
		mockMvc.perform(get("/company/offer/1/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("company/offer/form"))
			.andExpect(model().attribute("offer", offerMap.get("1")));
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testEditOfferHasNotPermission() throws Exception {
		mockMvc.perform(get("/company/offer/2/edit"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testEditOfferPostSuccess() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("title", "清掃ボランティア募集");
		params.add("prefecture", "東京都");
		params.add("address", "新宿区");
		mockMvc.perform(post("/company/offer/1/edit").with(csrf()).params(params))
			.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testEditOfferPostFail() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		mockMvc.perform(post("/company/offer/1/edit").with(csrf()).params(params))
			.andExpect(view().name("company/offer/form"))
			.andExpect(model().hasErrors());
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testShowEntryHasPermission() throws Exception {
		mockMvc.perform(get("/company/offer/1/entry/1"))
		.andExpect(status().isOk())
		.andExpect(view().name("company/offer/entry"))
		.andExpect(model().attribute("entry", entryMap.get("1")));
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testShowEntryHasNotPermission() throws Exception {
		mockMvc.perform(get("/company/offer/2/entry/2"))
		.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles="COMPANY")
	public void testProcessEntry() throws Exception {
		mockMvc.perform(post("/company/offer/1/entry/1").with(csrf()))
		.andExpect(status().isFound());
	}
}

