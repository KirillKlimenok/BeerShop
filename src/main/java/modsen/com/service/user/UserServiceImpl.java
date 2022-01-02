package modsen.com.service.user;

import modsen.com.dto.UnregisteredUserDto;
import modsen.com.exceptions.NotTrueValidationUserException;
import modsen.com.repository.user.UserRepository;
import modsen.com.service.validation.ValidationsService;
import modsen.com.service.validation.ValidationsServiceImpl;
import modsen.com.service.validation.Validator;

import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationsService validationsService;

    public UserServiceImpl() {
        this.userRepository = new UserRepository();
        this.validationsService = new ValidationsServiceImpl();
    }

    @Override
    public void createNewUser(UnregisteredUserDto user, List<Validator> validators) {
        if (validationsService.validate(user, validators)) {
            try {
                if(!userRepository.isRegisteredUser(user)) {
                    userRepository.writeUser(user);
                    String token = userRepository.getUserId(user);
                    System.out.println(token);
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
