package portfolio.spring.boot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import portfolio.spring.boot.model.Offer;

public interface OfferRepository extends JpaRepository<Offer, Integer>{
	
	List<Offer> findByActiveTrue();
	
	List<Offer> findByActiveTrueAndTitleContainsAndPrefectureContains(String word, String prefecture);

}