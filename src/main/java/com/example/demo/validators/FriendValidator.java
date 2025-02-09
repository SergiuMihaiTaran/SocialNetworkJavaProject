package com.example.demo.validators;


import com.example.demo.domain.FriendShip;

public class FriendValidator implements Validator<FriendShip> {
    @Override
    public void validate(FriendShip entity) throws ValidationException {
        //TODO: implement method validate
        if(entity.getId1()==entity.getId2())
            throw new ValidationException("Utilizatorul nu este valid");
    }

}
