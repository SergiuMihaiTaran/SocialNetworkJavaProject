package com.example.demo.validators;

import com.example.demo.domain.User;

public class UtilizatorValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        //TODO: implement method validate
        if(entity.getFirstName().equals(""))
            throw new ValidationException("Utilizatorul nu este valid");
    }
}
