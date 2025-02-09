package com.example.demo.validators;


import com.example.demo.domain.LastLogin;

public class LastLoginValidator implements Validator<LastLogin> {
    @Override
    public void validate(LastLogin entity) throws ValidationException {
        //TODO: implement method validate
        if(entity.getId()==null)
            throw new ValidationException("Login-ul nu este valid");
    }

}
