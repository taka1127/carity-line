package portfolio.spring.boot.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import portfolio.spring.boot.model.Entry;
import portfolio.spring.boot.model.Offer;
import portfolio.spring.boot.model.Person;


@DataJpaTest
public class EntryRepositoryTest {
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private EntryRepository entryRepository;
	
	@Test
	public void testFindByOfferAndPerson() {
		Offer offer = new Offer();
		offer.setTitle("清掃ボランティア募集");
		offer.setPrefecture("東京都");
		offer.setAddress("新宿区");
		entityManager.persist(offer);
		
		Person person = new Person();
		person.setName("山田");
		person.setMail("test@example.com");
		person.setTel("012-3456-7890");
		person.setBirthday(LocalDate.of(1990, 1, 23));
		entityManager.persist(person);
		
		Entry entry = new Entry();
		entry.setOffer(offer);
		entry.setPerson(person);
		entityManager.persist(entry);
		
		Entry found = entryRepository.findByOfferAndPerson(offer, person);
		assertThat(found).isNotNull();
	}
}