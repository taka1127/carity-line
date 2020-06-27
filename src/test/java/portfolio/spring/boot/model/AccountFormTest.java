package portfolio.spring.boot.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountFormTest {
	//モデルクラスを「単体テスト」する場合はこのSpring Frameworkの仕組み(@Valid)は利用できない。バリデーション処理を手動で実行する必要がある。
	private Validator validator;
	
	@BeforeEach //JUnitにより提供されているもので各テストの前処理を記述。
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test //成功の場合
	public void testValidationSucccess() {
		AccountForm form = new AccountForm();
		form.setUsername("user");
		form.setPassword("12345678");
		
		Set<ConstraintViolation<AccountForm>>violations = validator.validate(form);
		assertThat(violations.size()).isEqualTo(0);
	}
	
	@Test //usernameなしの場合
	public void testValidationFailUsername() {
		AccountForm form = new AccountForm();
		form.setPassword("12345678");
	
		Set<ConstraintViolation<AccountForm>> violations = validator.validate(form);
		assertThat(violations.size()).isEqualTo(1);
		// Set（集合）に含まれる各要素について繰り返し　45~46お決まりの形
		for (ConstraintViolation<AccountForm> violation : violations) {
		Object annotation = violation.getConstraintDescriptor().getAnnotation();
		// usernameのNotBlankエラーが発生したことを確認
		assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}

	@Test //passwordなしの場合
	public void testValidationFailPasswordBlank() {
		AccountForm form = new AccountForm();
		form.setUsername("user");
	
		Set<ConstraintViolation<AccountForm>> violations = validator.validate(form);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<AccountForm> violation : violations) {
		Object annotation = violation.getConstraintDescriptor().getAnnotation();
		assertThat(annotation).isInstanceOf(NotBlank.class);
		}
	}

	@Test //passwordが8文字以下の場合
	public void testValidationFailPasswordSize() {
		AccountForm form = new AccountForm();
		form.setUsername("user");
		form.setPassword("1234");
	
		Set<ConstraintViolation<AccountForm>> violations = validator.validate(form);
		assertThat(violations.size()).isEqualTo(1);
		for (ConstraintViolation<AccountForm> violation : violations) {
		Object annotation = violation.getConstraintDescriptor().getAnnotation();
		assertThat(annotation).isInstanceOf(Size.class);
		}
	}
}