package com.example.demo.validators;


import com.example.demo.domain.FriendShip;

public class FriendValidator implements Validator<FriendShip> {
    @Override
    public void validate(FriendShip entity) throws ValidationException {
        if(entity.getId1()==entity.getId2())
            throw new ValidationException("The friendship is not valid");
    }

}
