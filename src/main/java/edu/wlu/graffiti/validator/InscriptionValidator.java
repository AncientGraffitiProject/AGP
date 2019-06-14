package edu.wlu.graffiti.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.wlu.graffiti.bean.Inscription;

 
public class InscriptionValidator implements Validator{
 
	@Override
	public boolean supports(Class clazz) {
		return Inscription.class.isAssignableFrom(clazz);
	}
 
	@Override
	public void validate(Object target, Errors errors) {
 
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "eagleId",
			"required.userName", "Field name is required.");
 
		Inscription insc = (Inscription)target;
 
		if(!insc.getEdrId().substring(0,3).equals("EDR")){
			errors.rejectValue("eagleId", "notCorrectForm.eagleId");
		}

	}
}