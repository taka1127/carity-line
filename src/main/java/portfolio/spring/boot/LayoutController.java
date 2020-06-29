package portfolio.spring.boot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LayoutController {
	@GetMapping("/layout1")
		public String layout1() {
		return "layout1";
}

	@GetMapping("/layout2")
		public String layout2() {
		return "layout2";
	}
}
