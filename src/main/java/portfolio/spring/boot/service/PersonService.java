package portfolio.spring.boot.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import portfolio.spring.boot.model.Entry;
import portfolio.spring.boot.model.Offer;
import portfolio.spring.boot.model.Person;
import portfolio.spring.boot.repository.EntryRepository;
import portfolio.spring.boot.repository.PersonRepository;

@Service
public class PersonService {
	@Autowired
	private PersonRepository personRepository;
	
	public Person createDefaultValuePerson() {
		Person person = new Person();
		person.setName("名前を設定してください");
		person.setMail("please@set.email");
		person.setTel("000-0000-0000");
		person.setBirthday(LocalDate.of(1980, 1, 1));
		personRepository.save(person);
		return person;
	}
	
	@Autowired
	private EntryRepository entryRepository;

	//応募者情報を更新する
	public void updatePerson(Person person) {
		personRepository.save(person);
	}

	//offer, personに対応する応募情報を取得する。存在しない場合は新規のEntryオブジェクトを返す
	public Entry getEntry(Offer offer, Person person) {
		Entry entry = entryRepository.findByOfferAndPerson(offer, person);
		//EntryRepositoryがnullを返したら新規Entryを作る
		if (entry == null) {
			entry = new Entry();
			entry.setOffer(offer);
			entry.setPerson(person);
			entry.setActive(true);
		}
		return entry;
	}

	//応募情報を作成する
	public Entry createEntry(Entry entry) {
		return entryRepository.save(entry);
	}
}

