package com.modsen.service.user;

import com.modsen.dto.UnregisteredUserDto;
import com.modsen.exceptions.NotTrueValidationUserException;
import com.modsen.repository.user.UserRepository;
import com.modsen.service.validation.ValidationsService;
import com.modsen.service.validation.ValidationsServiceImpl;
import com.modsen.service.validation.Validator;

import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationsService validationsService;

    public UserServiceImpl(UserRepository userRepository, ValidationsService validationsService) {
        this.userRepository = userRepository;
        this.validationsService = validationsService;
    }

    @Override
    public void createNewUser(UnregisteredUserDto user, List<Validator> validators) {
        if (validationsService.validate(user, validators)) {
            try {
                if(!userRepository.isRegisteredUser(user)) {
                    userRepository.writeUser(user);
                    String token = userRepository.getUserId(user);
                    userRepository.writeToken(token);
                }
            } catch (SQLException e) {
                throw new NotTrueValidationUserException("you entered not true login or email\n" + e.getMessage());
            }
        }else{
            throw new NotTrueValidationUserException("you entered not true login or email");
        }
    }

    @Override
    public String getTokenUser(UnregisteredUserDto unregisteredUserDto) {
        return userRepository.getUserToken(unregisteredUserDto);
    }
}
