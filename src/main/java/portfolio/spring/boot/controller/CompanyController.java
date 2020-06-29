package portfolio.spring.boot.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import portfolio.spring.boot.model.Account;
import portfolio.spring.boot.model.Company;
import portfolio.spring.boot.model.Entry;
import portfolio.spring.boot.model.Offer;
import portfolio.spring.boot.service.CompanyService;

@Controller
@RequestMapping("/company")
public class CompanyController {
	@Autowired
	private CompanyService companyService;

	//アカウントに対応するCompanyオブジェクトを返す
	//現在のログインアカウント（Spring Securityのログインユーザに対応するAccountオブジェクト）」を取得する処理はCommonControllerAdviceクラスに記述
	@ModelAttribute("company")
	public Company currentCompany(Account account) {
		return account.getCompany();
	}
	
	//ビューとして「company/index」を返す
	@GetMapping("")
	public String index() {
		return "company/index";
	}
	
	//ビューとして「company/form」を返す
	@GetMapping("/edit")
	public String edit() {
		return "company/form";
	}
	
	//企業情報を更新する。更新できたら「/company」にリダイレクトする
	@PostMapping("/edit")
	public String edit(@Valid Company company, BindingResult bindingResult) {
		
		//バリデーションエラーの場合はビューとして「company/form」を返す
		if (bindingResult.hasErrors()) {
			return "company/form";
		}

		//現在のログインアカウントに対応するCompanyオブジェクトをモデル属性として設定
		companyService.updateCompany(company);
		return "redirect:/company";
	}

	//offerにcompanyを関連付ける。ビューとして「company/offer/form」を返す
		//Offerオブジェクトは新しく作られてモデル属性に設定、メソッドに渡される。
		//CompanyオブジェクトはcurrentCompanyメソッドで設定されるモデル属性がメソッドに渡されます
	@GetMapping("/offer/create")
	public String createOffer(Offer offer, Company company) {
		offer.setCompany(company);
		return "company/offer/form";
	}
	
	//Accountオブジェクトの検索時に、「Accountに関連するCompanyオブジェクト」→「Companyに関連するOfferオブジェクト（複数）」と芋づる式に取得が行われるため
	
	//offerを保存する。保存できたら「/company/offer/{offer.id}」にリダイレクトする。	
	@PostMapping("/offer/create")
	public String createOffer(@Valid Offer offer, BindingResult bindingResult) {
		
		//バリデーションエラーの場合はビューとして「company/offer/form」を返す
		if (bindingResult.hasErrors()) {
			return "company/offer/form";
		}

		companyService.createOffer(offer);
		return "redirect:/company/offer/" + offer.getId();
	}

	//引数のOfferオブジェクトを「offer」属性に設定する。ビューとして「company/offer/show」を返す。
		//OfferオブジェクトはSpring Data JPAにより検索されたエンティティでありモデル属性ではない。そのため、 ビューで利用するために別途Modelインターフェースを受け取りモデル属性として設定する必要がある。
	@GetMapping("/offer/{offerId}")
	public String showOffer(@PathVariable("offerId") Offer offer, Model model) {
		//checkOfferOwnerメソッドを呼び出し別団体・企業からのアクセスを防御する
		checkOfferOwner(offer, model);

		model.addAttribute("offer", offer);
		return "company/offer/show";
	}

	
	@GetMapping("/offer/{offerId}/entry/{entryId}")//EntryオブジェクトからOfferオブジェクトにアクセス可能なため、引数のEntryオブジェクトを「entry」属性に設定する
	public String showEntry(@PathVariable("entryId") Entry entry, Model model) {
		checkOfferOwner(entry.getOffer(), model);

		model.addAttribute("entry", entry);
		return "company/offer/entry";
	}

	
	//引数のEntryオブジェクトを「処理済み」にする。処理ができたら「/company/{offer.id}/entry/{entry.id}」にリダイレクトする。
	@PostMapping("/offer/{offerId}/entry/{entryId}")
	public String processEntry(@PathVariable("entryId") Entry entry) {
		companyService.processEntry(entry);
		String path = "/company/offer/" + entry.getOffer().getId() + "/entry/" + entry.getId();
		return "redirect:" + path;
	}

	@ResponseStatus(HttpStatus.FORBIDDEN) //RuntimeExceptionをスーパークラスとすると、メソッドが送出する可能性のある例外をthrowsで宣言しなくてもよくなる
	private class ForbiddenOfferAccessException extends RuntimeException {
		 // 例外クラスはシリアライズ可能なため、定義が必要
		private static final long serialVersionUID = 1L;

		public ForbiddenOfferAccessException(String message) {
			super(message);
		}
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)//引数としてHttpStatusで定義されているステータスコードに対応する定数を指定
	private class OfferNotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public OfferNotFoundException(String message) {
			super(message);
		}
	}
	
	private void checkOfferOwner(Offer offer, Model model) {
		if (offer == null) {
			throw new OfferNotFoundException("Not Found");
		}
		 // Modelから"company"属性を取り出す
		Map<String, Object> map = model.asMap();
		Company company = (Company)map.get("company");
		
		// offerを作ったcompanyと現在ログインしているcompanyが一致するか確認
        // 一致しない場合は例外送出→403ステータスが返される
		if (!company.equals(offer.getCompany())) {
			throw new ForbiddenOfferAccessException("Forbidden");
		}
	}

	@GetMapping("/offer/{offerId}/edit")
	public String editOffer(@PathVariable("offerId") Offer offer, Model model) {
		checkOfferOwner(offer, model);

		model.addAttribute("offer", offer);
		return "company/offer/form";
	}
	
	@PostMapping("/offer/{offerId}/edit")
	public String editOffer(@Valid Offer offer, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "company/offer/form";
		}

		companyService.updateOffer(offer);
		return "redirect:/company/offer/" + offer.getId();
	}
}
