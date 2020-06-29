package portfolio.spring.boot.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class Company {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@NotBlank(message = "企業名または団体名を入力してください")
	@Size(max = 80, message = "企業名または団体名は80文字以内で入力してください")
	@Column(length = 80)
	private String name;
	
	@NotBlank(message = "URLを入力してください")
	@Size(max = 80, message = "URLは80文字以内で入力してください")
	@Column(length = 80)
	private String url;
	
	@NotBlank(message = "メールアドレスを入力してください")
	@Email(message = "正しいメールアドレスを入力してください")
	@Size(max = 80, message = "メールアドレスは80文字以内で入力してください")
	@Column(length = 80)
	private String mail;
	
	@NotBlank(message = "電話番号をを入力してください")
	@Pattern(regexp = "0\\d{1,2}-\\d{2,4}-\\d{4}", message = "電話番号はXXX-XXXX-XXXXの形式で入力してください")
	@Column(length = 20)
	private String tel;
	
	@Size(max = 1000, message = "企業紹介や団体情報などは1000文字以内で入力してください")
	@Column(length = 1000)
	private String introduction;
	
	@OneToMany(mappedBy = "company")
	private List<Offer> offers;

}