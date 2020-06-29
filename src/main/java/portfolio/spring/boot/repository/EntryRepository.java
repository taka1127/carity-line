package portfolio.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import portfolio.spring.boot.model.Entry;
import portfolio.spring.boot.model.Offer;
import portfolio.spring.boot.model.Person;

public interface EntryRepository extends JpaRepository<Entry, Integer>{
	
	Entry findByOfferAndPerson(Offer offer, Person person);

}