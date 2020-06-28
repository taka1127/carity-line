package portfolio.spring.boot.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import portfolio.spring.boot.model.Account;
import portfolio.spring.boot.service.AccountService;

//各コントローラクラス共通で行いたいことがある場合、@ControllerAdviceアノテーションをつける。
//定義されているモデル属性（@ModelAttributeアノテーションが付けられたメソッド）はハンドラメソッドが呼び出される前に呼び出され設定が行われる。
@ControllerAdvice
public class CommonControllerAdvice {
	
	@Autowired
	private AccountService accountService;
	
	
	//Spring Securityのログイン情報取得の処理
	@ModelAttribute("account")
	public Account currentAccount() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		Object principal = auth.getPrincipal();
		if(principal instanceof UserDetails) {
			UserDetails userDetails = (UserDetails)auth.getPrincipal();
			return accountService.find(userDetails.getUsername());
		}else {
			return null;
		}
	}
	
	@ModelAttribute("prefectures")
	public List<String> prefectures() {
		final String[] prefectures = {
			"北海道",
			"青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県",
			"茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県",
			"新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県", "静岡県", "愛知県",
			"三重県", "滋賀県", "京都府", "大阪府", "兵庫県", "奈良県", "和歌山県",
			"鳥取県", "島根県", "岡山県", "広島県", "山口県",
			"徳島県", "香川県", "愛媛県", "高知県",
			"福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県"
		};
		        // 配列をListに変換
		return Arrays.asList(prefectures);
	}

}
