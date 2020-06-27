package portfolio.spring.boot.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CompanyTest {
	private Validator validator;
	
	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	public void testValidationSuccess() {
		Company company = new Company();
		company.setName("test");
		company.setUrl("http://www.example.com/");
		company.setMail("test@example.com");
		company.setTel("012-3456-7890");
		
		Set<ConstraintViolation<Company>> violations = validator.validate(company);
		assertThat(violations.size()).isEqualTo(0);
	}
	
	@Test
	public void testValidationFailNameBlank() {
		Company company = new Company();
		company.setUrl("http://www.example.com/");
		company.setMail("test@example.com");
		company.setTel("012-3456-7890");
		
		Set<ConstraintViolation<Company>> violations = validator.validate(company);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Company> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	
	@Test
	public void testValidationFailNameSize() {
		Company company = new Company();
		company.setUrl("http://www.example.com/");
		company.setMail("test@example.com");
		company.setTel("012-3456-7890");
		
		String s = "";
		for (int i = 0; i < 10; i++) {
			s += "1234567890";
		}
		company.setName(s);
		
		Set<ConstraintViolation<Company>> violations = validator.validate(company);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Company> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
	
	@Test
	public void testValidationFailUrlBlank() {
		Company company = new Company();
		company.setName("test");
		company.setMail("test@example.com");
		company.setTel("012-3456-7890");
		
		Set<ConstraintViolation<Company>> violations = validator.validate(company);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Company> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	
	@Test
	public void testValidationFailUrlSize() {
		Company company = new Company();
		company.setName("test");
		company.setMail("test@example.com");
		company.setTel("012-3456-7890");

		String s = "";
		for (int i = 0; i < 10; i++) {
			s += "1234567890";
		}
		company.setUrl(s);
		
		Set<ConstraintViolation<Company>> violations = validator.validate(company);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Company> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
	
	@Test
	public void testValidationMailBlank() {
		Company company = new Company();
		company.setName("test");
		company.setUrl("http://www.example.com/");
		company.setTel("012-3456-7890");
		
		Set<ConstraintViolation<Company>> violations = validator.validate(company);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Company> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	
	@Test
	public void testValidationFailMailFormat() {
		Company company = new Company();
		company.setName("test");
		company.setUrl("http://www.example.com/");
		company.setTel("012-3456-7890");

		company.setMail("abcd");
		
		Set<ConstraintViolation<Company>> violations = validator.validate(company);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Company> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Email.class);
		}
	}
	
	@Test
	public void testValidationFailMailSize() {
		Company company = new Company();
		company.setName("test");
		company.setUrl("http://www.example.com/");
		company.setTel("012-3456-7890");

		String s = "";
		for (int i = 0; i < 5; i++) {
			s += "1234567890";
		}
		s += "@";
		for (int i = 0; i < 5; i++) {
			s += "1234567890";
		}
		s += "example.com";
		company.setMail(s);
		
		Set<ConstraintViolation<Company>> violations = validator.validate(company);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Company> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
	
	@Test
	public void testValidationFailTelBlank() {
		Company company = new Company();
		company.setName("test");
		company.setUrl("http://www.example.com/");
		company.setMail("test@example.com");
		
		Set<ConstraintViolation<Company>> violations = validator.validate(company);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Company> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	
	@Test
	public void testValidationFailTelFormat() {
		Company company = new Company();
		company.setName("test");
		company.setUrl("http://www.example.com/");
		company.setMail("test@example.com");

		company.setTel("abcd");
		
		Set<ConstraintViolation<Company>> violations = validator.validate(company);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Company> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Pattern.class);
		}
	}
	
	@Test
	public void testValidationFailInroductionSize() {
		Company company = new Company();
		company.setName("test");
		company.setUrl("http://www.example.com/");
		company.setMail("test@example.com");
		company.setTel("012-3456-7890");

		String s = "";
		for (int i = 0; i < 120; i++) {
			s += "1234567890";
		}
		company.setIntroduction(s);
		
		Set<ConstraintViolation<Company>> violations = validator.validate(company);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Company> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
}