package portfolio.spring.boot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import portfolio.spring.boot.model.Offer;
import portfolio.spring.boot.service.HomeService;

@Controller
public class HomeController {
	
	@Autowired
	private HomeService homeService;
	
	@GetMapping("/")
	public String home(Model model) {
		//募集中の求人情報を「offers」属性に設定する
		List<Offer> offers = homeService.getOfferList();
		model.addAttribute("offers", offers);
		
		return "index";
	}
	
	@GetMapping("/search") //@RequestParamアノテーションを使用することで、クエリ文字列を引数として受け取ることができる(/search?word=〇〇&prefecture=〇〇)
	public String search(@RequestParam String word, @RequestParam String prefecture, Model model) {
		//条件を満たす求人情報を「offers」属性に設定する
		List<Offer> offers = homeService.findOfferList(word, prefecture);
		model.addAttribute("offers", offers);

		return "index";
	}

	@GetMapping("/offer/{offerId}") ////引数のOfferオブジェクトを「offer」属性に設定する
	public String showOffer(@PathVariable("offerId") Offer offer, Model model) {
		model.addAttribute("offer", offer);
		
		return "offer";
	}

	
	@GetMapping("/login")
	public String login() {
		
		return "login";
	}

}
