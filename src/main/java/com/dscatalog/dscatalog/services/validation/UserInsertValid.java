package com.dscatalog.dscatalog.services.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

//Este código é um boiler plate, definindo uma annotation
@Constraint(validatedBy = UserInsertValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserInsertValid {
	String message() default "Validation error";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
