package portfolio.spring.boot.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersonTest {
	private Validator validator;
	
	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	public void testValidationSuccess() {
		Person person = new Person();
		person.setName("山田");
		person.setMail("test@example.com");
		person.setTel("012-3456-7890");
		person.setBirthday(LocalDate.of(1990, 1, 23));
		
		Set<ConstraintViolation<Person>> violations = validator.validate(person);
		assertThat(violations.size()).isEqualTo(0);
	}
	
	@Test
	public void testValidationFailNameBlank() {
		Person person = new Person();
		person.setMail("test@example.com");
		person.setTel("012-3456-7890");
		person.setBirthday(LocalDate.of(1990, 1, 23));
		
		Set<ConstraintViolation<Person>> violations = validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Person> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	
	@Test
	public void testValidationFailNameSize() {
		Person person = new Person();
		person.setMail("test@example.com");
		person.setTel("012-3456-7890");
		person.setBirthday(LocalDate.of(1990, 1, 23));
		
		String s = "";
		for (int i = 0; i < 10; i++) {
			s += "1234567890";
		}
		person.setName(s);
		
		Set<ConstraintViolation<Person>> violations = validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Person> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
	
	@Test
	public void testValidationMailBlank() {
		Person person = new Person();
		person.setName("山田");
		person.setTel("012-3456-7890");
		person.setBirthday(LocalDate.of(1990, 1, 23));
		
		Set<ConstraintViolation<Person>> violations = validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Person> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	
	@Test
	public void testValidationFailMailFormat() {
		Person person = new Person();
		person.setName("山田");
		person.setTel("012-3456-7890");
		person.setBirthday(LocalDate.of(1990, 1, 23));

		person.setMail("abcd");
		
		Set<ConstraintViolation<Person>> violations = validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Person> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Email.class);
		}
	}
	
	@Test
	public void testValidationFailMailSize() {
		Person person = new Person();
		person.setName("山田");
		person.setTel("012-3456-7890");
		person.setBirthday(LocalDate.of(1990, 1, 23));

		String s = "";
		for (int i = 0; i < 5; i++) {
			s += "1234567890";
		}
		s += "@";
		for (int i = 0; i < 5; i++) {
			s += "1234567890";
		}
		s += "example.com";
		person.setMail(s);
		
		Set<ConstraintViolation<Person>> violations = validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Person> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
	
	@Test
	public void testValidationFailTelBlank() {
		Person person = new Person();
		person.setName("山田");
		person.setMail("test@example.com");
		person.setBirthday(LocalDate.of(1990, 1, 23));
		
		Set<ConstraintViolation<Person>> violations = validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Person> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}
	
	@Test
	public void testValidationFailTelFormat() {
		Person person = new Person();
		person.setName("山田");
		person.setMail("test@example.com");
		person.setBirthday(LocalDate.of(1990, 1, 23));

		person.setTel("abcd");
		
		Set<ConstraintViolation<Person>> violations = validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Person> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Pattern.class);
		}
	}
	
	@Test
	public void testValidationFailBirthdayNull() {
		Person person = new Person();
		person.setName("山田");
		person.setMail("test@example.com");
		person.setTel("012-3456-7890");
		
		Set<ConstraintViolation<Person>> violations = validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Person> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(NotNull.class);
		}
	}

	@Test
	public void testValidationFailCareerSize() {
		Person person = new Person();
		person.setName("山田");
		person.setMail("test@example.com");
		person.setTel("012-3456-7890");
		person.setBirthday(LocalDate.of(1990, 1, 23));

		String s = "";
		for (int i = 0; i < 120; i++) {
			s += "1234567890";
		}
		person.setCareer(s);
		
		Set<ConstraintViolation<Person>> violations = validator.validate(person);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Person> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
}