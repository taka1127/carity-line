package portfolio.spring.boot.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class Offer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@NotBlank(message = "募集タイトルを入力してください")
	@Size(max = 80, message = "募集タイトルは80文字以内で入力してください")
	@Column(length = 80)
	private String title;
	
	@Size(max = 1000, message = "募集詳細は1000文字以内で入力してください")
	@Column(length = 1000)
	private String contents;
	
	@NotBlank(message = "都道府県を入力してください")
	@Column(length = 4)
	private String prefecture;
	
	@NotBlank(message = "活動場所を入力してください")
	@Size(max = 80, message = "活動場所は80文字以内で入力してください")
	@Column(length = 80)
	private String address;
	
	@Size(max = 80, message = "有償ボランティアなどの報酬情報は80文字以内で入力してください")
	@Column(length = 80)
	private String salary;
	
	@Size(max = 80, message = "活動時間は80文字以内で入力してください")
	@Column(length = 80)
	private String workingHours;
	
	@Size(max = 80, message = "休日情報は80文字以内で入力してください")
	@Column(length = 80)
	private String holiday;
	
	@Size(max = 80, message = "保険やその他特記事項は80文字以内で入力してください")
	@Column(length = 80)
	private String insurance;
	
	private Boolean active;
	
	@ManyToOne
	private Company company;
	
	@OneToMany(mappedBy = "offer")
	private List<Entry> entries;

}