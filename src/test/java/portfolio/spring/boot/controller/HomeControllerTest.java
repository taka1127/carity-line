package portfolio.spring.boot.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.format.FormatterRegistry;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import portfolio.spring.boot.model.Company;
import portfolio.spring.boot.model.Offer;
import portfolio.spring.boot.service.AccountService;
import portfolio.spring.boot.service.HomeService;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private HomeService homeService;

	@MockBean
	private AccountService accountService;

	@MockBean
	private UserDetailsManager userDetailsManager;
	
	private static List<Offer> offers;
	private static Offer offer;
	
	@BeforeEach
	public void setUp() {
		Company company = new Company();
		company.setName("win");
		
		offer = new Offer();
		offer.setContents("");
		offer.setCompany(company);
		
		offers = Arrays.asList(offer);
	}

	@TestConfiguration
	static class Config implements WebMvcConfigurer {

		@Override
		public void addFormatters(FormatterRegistry registry) {
			registry.addConverter(String.class, Offer.class, id -> offer);
		}
		
	}
	
	@Test
	public void testIndex() throws Exception {
		when(homeService.getOfferList()).thenReturn(offers);
		
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attribute("offers", offers));
	}

	@Test
	public void testSearch() throws Exception {
		when(homeService.findOfferList("清掃", "大阪府")).thenReturn(offers);
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("word", "清掃");
		params.add("prefecture", "大阪府");
		mockMvc.perform(get("/search").params(params))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andExpect(model().attribute("offers", offers));
	}
	
	@Test
	public void testShowOffer() throws Exception {
		mockMvc.perform(get("/offer/{offerId}", 1))
			.andExpect(status().isOk())
			.andExpect(view().name("offer"))
			.andExpect(model().attribute("offer", offer));
	}

	@Test
	public void testLogin() throws Exception {
		mockMvc.perform(get("/login"))
			.andExpect(status().isOk())
			.andExpect(view().name("login"));
	}
}

