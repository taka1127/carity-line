package portfolio.spring.boot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import portfolio.spring.boot.model.Offer;
import portfolio.spring.boot.repository.OfferRepository;

@Service
public class HomeService {
	
	@Autowired
	private OfferRepository offerRepository;
	
	//募集中(active=true)の求人情報を取得する
	public List<Offer> getOfferList(){
		return offerRepository.findByActiveTrue();
	}
	
	//求人タイトル、都道府県が指定条件に合う求人情報を検索する
	public List<Offer> findOfferList(String word, String prefecture){
		return offerRepository.findByActiveTrueAndTitleContainsAndPrefectureContains(word, prefecture);
	}

}
