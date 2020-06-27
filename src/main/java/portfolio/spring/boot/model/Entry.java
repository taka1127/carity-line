package portfolio.spring.boot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class Entry {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Size(max = 1000, message = "自己PRは1000文字で入力してください")
	@Column(length = 1000)
	private String contents;
	
	private Boolean active;
	
	@ManyToOne
	private Offer offer;
	
	@ManyToOne
	private Person person;

}