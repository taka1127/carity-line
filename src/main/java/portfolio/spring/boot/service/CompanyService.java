package portfolio.spring.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import portfolio.spring.boot.model.Company;
import portfolio.spring.boot.repository.CompanyRepository;

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

}
