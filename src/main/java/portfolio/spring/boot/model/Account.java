package portfolio.spring.boot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data
public class Account {
	
	@Id
	@Column(length = 80)
	private String username;
	
	@Column(length = 20)
	private String type;
	
	private Boolean active;
	
	@OneToOne
	private Company company;
	
	@OneToOne
	private Person person;

}
