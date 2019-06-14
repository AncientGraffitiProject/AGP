package edu.wlu.graffiti.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import edu.wlu.graffiti.bean.User;

public class UserValidator implements Validator {

	@Override
	public boolean supports(Class clazz) {
		// just validate the user
		return User.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "required.userName", "Field name is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password", "Field name is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "required.confirmPassword",
				"Field name is required.");

		User us = (User) target;

		if (!(us.getPassword().equals(us.getConfirmPassword()))) {
			errors.rejectValue("password", "notmatch.password");
		}

	}
}