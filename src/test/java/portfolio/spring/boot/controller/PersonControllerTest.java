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
import portfolio.spring.boot.service.PersonService;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private PersonService personService;

	@MockBean
	private AccountService accountService;

	@MockBean
	private UserDetailsManager userDetailsManager;

	private static Account account;
	private static Offer offer;
	private static Map<String, Entry> entryMap;

	@BeforeEach
	public void setUp() {
		// ログインアカウントの設定。person1をログインアカウントに関連付ける
		Person person1 = new Person();
		person1.setName("春田");
		account = new Account();
		account.setPerson(person1);
		when(accountService.find("user")).thenReturn(account);
		
		// person2は他者アカウント
		Person person2 = new Person();
		person2.setName("田中");

		Company company1 = new Company();
		company1.setName("win");
		
		offer = new Offer();
		offer.setContents("");
		offer.setCompany(company1);
		
		entryMap = new HashMap<String, Entry>();

		// 自分の応募情報
		Entry entry1 = new Entry();
		entry1.setOffer(offer);
		entry1.setPerson(person1);
		entry1.setContents("");
		entryMap.put("1", entry1);
		
		// 他人の応募情報
		Entry entry2 = new Entry();
		entry2.setOffer(offer);
		entry2.setPerson(person2);
		entryMap.put("2", entry2);
	}

	@TestConfiguration
	static class Config implements WebMvcConfigurer {

		@Override
		public void addFormatters(FormatterRegistry registry) {
			registry.addConverter(String.class, Offer.class, id -> offer);
			registry.addConverter(String.class, Person.class, id -> account.getPerson());
			registry.addConverter(String.class, Entry.class, id -> entryMap.get(id));
		}
		
	}

	@Test
	public void testIndexNotLogin() throws Exception {
		mockMvc.perform(get("/person"))
			.andExpect(status().isFound());
	}

	@Test
	@WithMockUser
	public void testIndexHasNotPermission() throws Exception {
		mockMvc.perform(get("/person"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles="PERSON")
	public void testIndexHasPermission() throws Exception {
		mockMvc.perform(get("/person"))
			.andExpect(status().isOk())
			.andExpect(view().name("person/index"))
			.andExpect(model().attribute("person", account.getPerson()));
	}

	@Test
	@WithMockUser(roles="PERSON")
	public void testEditGet() throws Exception {
		mockMvc.perform(get("/person/edit"))
			.andExpect(status().isOk())
			.andExpect(view().name("person/form"));
	}
	
	@Test
	@WithMockUser(roles="PERSON")
	public void testEditPostSuccess() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("name", "山田");
		params.add("mail", "test@example.com");
		params.add("tel", "012-3456-7890");
		params.add("birthday", "1990-01-23");
		mockMvc.perform(post("/person/edit").with(csrf()).params(params))
			.andExpect(status().isFound());
	}
	
	@Test
	@WithMockUser(roles="PERSON")
	public void testEditPostFail() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		mockMvc.perform(post("/person/edit").with(csrf()).params(params))
			.andExpect(view().name("person/form"))
			.andExpect(model().hasErrors());
	}

	@Test
	@WithMockUser(roles="PERSON")
	public void testShowOffer() throws Exception {
		Person person = account.getPerson();
		Entry entry = new Entry();
		entry.setOffer(offer);
		entry.setPerson(person);
		when(personService.getEntry(offer, person)).thenReturn(entry);
		
		MvcResult result = mockMvc.perform(get("/person/offer/1"))
			.andExpect(status().isOk())
			.andExpect(view().name("person/offer/form"))
			.andReturn();
		Entry e = (Entry)result.getModelAndView().getModel().get("entry");
		assertThat(e.getOffer()).isEqualTo(offer);
		assertThat(e.getPerson()).isEqualTo(person);
	}

	@Test
	@WithMockUser(roles="PERSON")
	public void testEntryOfferSuccess() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		mockMvc.perform(post("/person/offer/1").with(csrf()).params(params))
			.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(roles="PERSON")
	public void testEntryOfferFail() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		
		String s = "";
		for (int i = 0; i < 120; i++) {
			s += "1234567890";
		}
		params.add("offer", "1");
		params.add("person", "1");
		params.add("contents", s);
		
		mockMvc.perform(post("/person/offer/1").with(csrf()).params(params))
			.andExpect(view().name("person/offer/form"))
			.andExpect(model().hasErrors());
	}

	@Test
	@WithMockUser(roles="PERSON")
	public void testShowEntrySuccess() throws Exception {
		mockMvc.perform(get("/person/entry/1"))
			.andExpect(status().isOk())
			.andExpect(view().name("person/offer/entry"))
			.andExpect(model().attribute("entry", entryMap.get("1")));
	}

	@Test
	@WithMockUser(roles="PERSON")
	public void testShowEntryFail() throws Exception {
		mockMvc.perform(get("/person/entry/2"))
		.andExpect(status().isForbidden());
	}
}