package com.modsen.service;

import com.modsen.entitys.UserRequest;
import com.modsen.entitys.UserResponse;
import com.modsen.entitys.dto.UnregisteredUserDto;
import com.modsen.exceptions.NotFoundUserException;
import com.modsen.repository.user.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationsService validationsService;
    private final List<Validator> validators;

    public UserServiceImpl(UserRepository userRepository, ValidationsService validationsService, List<Validator> validators) {
        this.userRepository = userRepository;
        this.validationsService = validationsService;
        this.validators = validators;
    }

    @Override
    public void createNewUser(UserRequest user) throws SQLException {
        UnregisteredUserDto userDto = new UnregisteredUserDto(user.getLogin(), user.getPassword(), user.getEmail());

        validationsService.validate(userDto, validators);

        if (!isRegisteredUser(userDto)) {
            userRepository.writeUser(userDto);
            Optional<String> optional = userRepository.getUserId(userDto);
            if (optional.isPresent()) {
                String token = optional.get();
                userRepository.writeToken(token);
            }
        }
    }

    @Override
    public UserResponse getTokenUser(UserRequest userRequest) throws SQLException {
        UnregisteredUserDto userDto = new UnregisteredUserDto(userRequest);
        Optional<String> optional = userRepository.getUserToken(userDto);

        if (!optional.isPresent()) {
            throw new NotFoundUserException("Not found your account. please check your data and try again");
        }
        String token = optional.get();
        return new UserResponse(token);
    }

    private boolean isRegisteredUser(UnregisteredUserDto userDto) throws SQLException {
        Optional<String> optional = userRepository.checkUser(userDto);
        return optional.isPresent();
    }
}
