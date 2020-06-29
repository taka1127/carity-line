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
import portfolio.spring.boot.model.Entry;
import portfolio.spring.boot.model.Offer;
import portfolio.spring.boot.model.Person;
import portfolio.spring.boot.service.PersonService;

@Controller
@RequestMapping("/person")
public class PersonController {
	
	@Autowired
	private PersonService personService;
	
	@ModelAttribute("person")
	public Person currentPerson(Account account) {
		//アカウントに対応するPersonオブジェクトを返す
		return account.getPerson();
	}
	
	@GetMapping("")
	public String index() {
		return "person/index";
	}
	
	@GetMapping("/edit")
	public String edit() {
		return "person/form";
	}
	
	//求職者情報を更新する。更新できたら「/person」にリダイレクトする。
	//バリデーションエラーの場合はビューとして「person/form」を返す
	@PostMapping("/edit")
	public String edit(@Valid Person person, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return "person/form";
		}
		
		personService.updatePerson(person);
		return "redirect:/person";
	}
	
	//offerとpersonに対応するEntryオブジェクトを取得し「entry」属性に設定する。
	@GetMapping("/offer/{offerId}")
	public String showOffer(@PathVariable("offerId") Offer offer, Person person, Model model) {
		Entry entry = personService.getEntry(offer, person);
		model.addAttribute("entry", entry);
	
		return "person/offer/form";
	}

	//応募情報を保存する。更新できたら「/person」にリダイレクトする
	@PostMapping("/offer/{offerId}")
	public String entryOffer(@Valid Entry entry, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
		return "person/offer/form";
	}

	personService.createEntry(entry);
		return "redirect:/person";
	}
	
	@ResponseStatus(HttpStatus.FORBIDDEN)
	private class ForbiddenEntryAccessException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public ForbiddenEntryAccessException(String message) {
			super(message);
		}
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	private class EntryNotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public EntryNotFoundException(String message) {
			super(message);
		}
	}

	//Entryに設定されているPersonと現在のPerson（モデル属性の”person”）を比較し、異なる場合は403エラーにする
	//（「@ResponseStatus(HttpStatus.FORBIDDEN)」アノテーションを付けた例外オブジェクトを送出する）。entryがnullの場合は404エラーにする
	private void checkEntryOwner(Entry entry, Model model) {
		if (entry == null) {
			throw new EntryNotFoundException("Not Found");
		}
		
		Map<String, Object> map = model.asMap();
		Person person = (Person)map.get("person");
		if (!person.equals(entry.getPerson())) {
			throw new ForbiddenEntryAccessException("Forbidden");
		}
	}
	
	//応募内容表示
	@GetMapping("/entry/{entryId}") //引数のEntryオブジェクトを「entry」属性に設定する
	public String showEntry(@PathVariable("entryId") Entry entry, Model model) {
		//checkEntryOwnerメソッドを呼び出し別応募者からのアクセスを防御する
		checkEntryOwner(entry, model);

		model.addAttribute("entry", entry);
		return "person/offer/entry";
	}

	

}
