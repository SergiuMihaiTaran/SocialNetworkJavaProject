package com.example.demo.validators;

import com.example.demo.domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        //TODO: implement method validate
        if(entity.getTo().size()<1 || entity.getFrom()==null)
            throw new ValidationException("Mesajul nu este valid");
    }
}
