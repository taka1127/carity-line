package portfolio.spring.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import portfolio.spring.boot.model.Company;
import portfolio.spring.boot.model.Entry;
import portfolio.spring.boot.model.Offer;
import portfolio.spring.boot.repository.CompanyRepository;
import portfolio.spring.boot.repository.EntryRepository;
import portfolio.spring.boot.repository.OfferRepository;

@Service
public class CompanyService {
	
	@Autowired
	private CompanyRepository companyRepository;
	
	public Company createDefaultValueCompany() {
		Company company = new Company();
		company.setName("名前を入力してください");
		company.setUrl("please set url");
		company.setMail("please@set.email");
		company.setTel("000-0000-0000");
		companyRepository.save(company);
		return company;
	}
	
	@Autowired
	private OfferRepository offerRepository;
	
	@Autowired
	private EntryRepository entryRepository;
	
	//団体・企業情報を更新する
	public void updateCompany(Company company) {
		companyRepository.save(company);
	}
	
	//募集情報を作成する
	public Offer createOffer(Offer offer) {
		return offerRepository.save(offer);
	}
	
	//募集情報を更新する
	public void updateOffer(Offer offer) {
		offerRepository.save(offer);
	}
	
	//応募情報を「処理済み（activeをfalse）」にする
	public void processEntry(Entry entry) {
		entry.setActive(false);
		entryRepository.save(entry);
	}

}
