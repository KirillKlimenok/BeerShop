package com.modsen.service;

import com.modsen.exception.UserNotFoundException;
import com.modsen.exception.UserRegistrationException;
import com.modsen.repository.UserRepository;
import com.modsen.service.dto.UnregisteredUserDto;
import com.modsen.сontroller.model.UserRequest;
import com.modsen.сontroller.model.UserResponse;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class RegistrationAndAuthServiceImpl implements RegistrationAndAuthService {
    private final UserRepository userRepository;
    private final List<Validator<UserRequest>> validators;
    private final TokenService tokenService;

    @Override
    public void createNewUser(UserRequest user) throws SQLException, UserRegistrationException {
        validators.forEach(x -> x.check(user));

        UnregisteredUserDto userDto = UnregisteredUserDto.builder().
                login(user.getLogin()).
                password(String.valueOf(Objects.hash(user.getPassword()))).
                email(user.getEmail()).
                token(tokenService.generateNewToken()).
                build();

        checkIsRegisteredUser(userDto.getEmail(), userDto.getLogin());

        userRepository.save(userDto);
    }

    @Override
    public UserResponse getTokenUser(UserRequest user) throws SQLException, UserNotFoundException {
        validators.forEach(x -> x.check(user));

        return userRepository.
                getUserToken(user.getLogin(), String.valueOf(Objects.hash(user.getPassword()))).
                map(UserResponse::new).
                orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private void checkIsRegisteredUser(String email, String login) throws SQLException, UserRegistrationException {
        List<String> list = userRepository.getListUsersWithSameLoginOrPassword(login, email);

        if (list.stream().anyMatch(x -> x.contains(login))) {
            throw new UserRegistrationException("User with this " + login + " login already registered");
        }
        if (list.stream().anyMatch(x -> x.contains(email))) {
            throw new UserRegistrationException("User with this " + email + " email already registered");
        }
    }
}
