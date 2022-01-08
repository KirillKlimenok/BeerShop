package com.modsen.service;

import com.modsen.exception.UserRegistrationException;
import com.modsen.сontroller.model.UserRequest;
import com.modsen.сontroller.model.UserResponse;
import com.modsen.service.dto.UnregisteredUserDto;
import com.modsen.exception.UserNotFoundException;
import com.modsen.repository.UserRepository;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final List<Validator<UserRequest>> validators;
    private final TokenService tokenService;

    @Override
    public void createNewUser(UserRequest user) throws SQLException, UserRegistrationException {
        validators.forEach(x -> x.check(user));

        UnregisteredUserDto userDto = new UnregisteredUserDto(user.getLogin(), String.valueOf(Objects.hash(user.getPassword())), user.getEmail(), tokenService.generateNewToken());

        isRegisteredUser(userDto.getEmail(), userDto.getLogin());

        userRepository.save(userDto);
    }

    @Override
    public UserResponse getTokenUser(UserRequest user) throws SQLException, UserNotFoundException {
        validators.forEach(x -> x.check(user));

        Optional<String> optional = userRepository.getUserToken(user.getLogin(), String.valueOf(Objects.hash(user.getPassword())));

        UserResponse userResponse = optional.map(UserResponse::new).orElse(null);
        if (userResponse == null) {
            throw new UserNotFoundException("user not found");
        } else {
            return userResponse;
        }
    }

    private void isRegisteredUser(String email, String login) throws SQLException, UserRegistrationException {
        if (userRepository.isUserExist("login", login)) {
            throw new UserRegistrationException("User with this " + login + " login already registered");
        }
        if (userRepository.isUserExist("email", email)) {
            throw new UserRegistrationException("User with this " + email + " email already registered");
        }
    }
}
