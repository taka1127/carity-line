package portfolio.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import portfolio.spring.boot.model.Person;

public interface PersonRepository extends JpaRepository<Person, Integer>{

}
