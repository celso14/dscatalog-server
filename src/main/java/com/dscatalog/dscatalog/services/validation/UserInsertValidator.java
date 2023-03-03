package com.dscatalog.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dscatalog.dscatalog.dto.UserInsertDto;
import com.dscatalog.dscatalog.entities.User;
import com.dscatalog.dscatalog.repositories.UserRepository;
import com.dscatalog.dscatalog.resources.exceptions.FieldMessage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

//No generics -> tipo da annotation, e o tipo da classe que vai receber este annotation
public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDto> {
	
	@Autowired
	private UserRepository repository;
	
	@Override
	public void initialize(UserInsertValid ann) {
	}

	@Override
	public boolean isValid(UserInsertDto dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		
		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista
		
		User user = repository.findByEmail(dto.getEmail());
		
		if (user != null) {
			list.add(new FieldMessage("email", "E-mail já cadastrado!"));
		}
		
		//Inserindo na lista de error no Beans Validation, do tipo dele
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		//Se a lista começar vazia, e terminar vazia, significa que passou na validação.
		return list.isEmpty();
	}
}
