package portfolio.spring.boot.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Size;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EntryTest {
	private Validator validator;
	
	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	public void testValidationSuccess() {
		Entry entry = new Entry();
		
		Set<ConstraintViolation<Entry>> violations = validator.validate(entry);
		assertThat(violations.size()).isEqualTo(0);
	}

	@Test
	public void testValidationFailContentsSize() {
		Entry entry = new Entry();
		
		String s = "";
		for (int i = 0; i < 120; i++) {
			s += "1234567890";
		}
		entry.setContents(s);

		Set<ConstraintViolation<Entry>> violations = validator.validate(entry);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<Entry> violation : violations) {
			Object annotation = violation.getConstraintDescriptor().getAnnotation();
			assertThat(annotation).isInstanceOf(Size.class);
		}
	}
}