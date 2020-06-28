package portfolio.spring.boot.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import portfolio.spring.boot.model.Account;
import portfolio.spring.boot.model.AccountForm;
import portfolio.spring.boot.service.AccountService;

@Controller
@RequestMapping("/account")//クラス自体に@RequestMappingアノテーションを付けることで階層化を行うことができる(GETのみを受け付けるという場合は「@RequestMapping("/", method=RequestMethod.GET)」のように指定)
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("") 
	public String index(Model model) {
		
		//アカウント一覧を「accounts」属性に設定する
		List<Account> accounts = accountService.getAccountList();
		model.addAttribute("accounts", accounts);
		
		//ビューとして「account/index」を返す
		return "account/index";
	}
	
	@GetMapping("/create")
	public String create(AccountForm accountForm) {
		
		//ビューとして「account/create」を返す
		return "account/create";
	}
	
	@PostMapping("/create")
	public String create(@Valid AccountForm accountForm, BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			return "account/create";
		}
	
		try {
			String username = accountForm.getUsername();
			String password = accountForm.getPassword();
			boolean active = accountForm.getActive();
			
			// Java7からはswitch文に文字列を使用可能
			//AccountFormに設定されているtypeに応じて、管理者アカウント、企業アカウント、求人者アカウントのいずれかを作成
			switch(accountForm.getType()) {
				case "administrator":
				accountService.createAdministratorAccount(username, password, active);
				break;
				
				case "company":
				accountService.createCompanyAccount(username, password, active);
				break;
				
				case "person":
				accountService.createPersonAccount(username, password, active);
				break;
			}
			return "redirect:/account"; //作成できたら「/account」にリダイレクトする
			
		} catch (DuplicateKeyException e) { //FieldErrorオブジェクトを作成し追加
			bindingResult.addError(new FieldError("accountForm", "username", "すでに存在するユーザIDです"));
			//同じユーザIDのユーザを作ろうとするとDuplicateKeyExceptionが発生するのでusernameフィールドに対して
			//「すでに存在するユーザIDです」というエラーメッセージを設定し「account/create」を返す
			return "account/create"; 
		}
	}
	
	@GetMapping("/{username}/edit")
	public String edit(@PathVariable("username") Account account, AccountForm accountForm) {
		//＠PathVariableで取得されるAccountオブジェクトの情報をフォームで編集できるようにAccountFormにコピーする
		accountForm.setType(account.getType());
		accountForm.setUsername(account.getUsername());
		accountForm.setActive(account.getActive());
		//ビューとして「account/edit」を返す
		return "account/edit";
	}
	
	@PostMapping("/{username}/edit") //@PathVariable:パス中に含まれるIDなどの文字列を引数として受け取る。引数の型としてエンティティクラスを指定することで内部で検索が行われる
	public String edit(@PathVariable("username") Account account, @Valid AccountForm accountForm, BindingResult bindingResult) {
		//パスワードが空の場合、変更は行わないのでバリデーションエラーは問題ない
		//バリデーションはパスワードが入力されている場合のみ確認する。バリデーションエラーの場合はビューとして「account/edit」を返す。
		if (!accountForm.getPassword().equals("") && bindingResult.hasErrors()) {
			return "account/edit";
		}
		//アカウント情報を更新する。更新できたら「/account」にリダイレクトする
		accountService.updateAccount(accountForm.getUsername(), accountForm.getPassword(), accountForm.getActive());
		return "redirect:/account";
	}
}

